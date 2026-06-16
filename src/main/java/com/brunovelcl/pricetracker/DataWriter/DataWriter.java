package com.brunovelcl.pricetracker.DataWriter;

import com.brunovelcl.pricetracker.DataParser.entities.ParsedValues;
import com.brunovelcl.pricetracker.DataWriter.dtos.*;
import com.brunovelcl.pricetracker.Text.Text;
import com.brunovelcl.pricetracker.database.entities.BrandNameProduct;
import com.brunovelcl.pricetracker.database.entities.PricePoint;
import com.brunovelcl.pricetracker.database.entities.Stores;
import com.brunovelcl.pricetracker.database.repositories.DatabaseRepository;
import com.brunovelcl.pricetracker.database.services.interfaces.BrandNameProductsService;
import com.brunovelcl.pricetracker.database.services.interfaces.PricePointService;
import com.brunovelcl.pricetracker.database.services.interfaces.PricePointStoreService;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

@Service
public class DataWriter {

    private static final String TEMP_TABLE_SUFIX = "_temp";

    private final BrandNameProductsService brandNameProductsService;
    private final PricePointService pricePointService;
    private final PricePointStoreService pricePointStoreService;
    private final DatabaseRepository databaseRepository;

    public DataWriter(BrandNameProductsService brandNameProductsService, PricePointService pricePointService, PricePointStoreService pricePointStoreService, DatabaseRepository databaseRepository) {
        this.brandNameProductsService = brandNameProductsService;
        this.pricePointService = pricePointService;
        this.pricePointStoreService = pricePointStoreService;
        this.databaseRepository = databaseRepository;
    }

    public void updateDatabase(List<ParsedValues> parsedValues) {
        Map<Long, ProductDTO> loadedMap = createMap();
        updateMap(parsedValues, loadedMap);
        OutputTablesDTO outputTablesDTO = createTables(loadedMap);

        final String ppOld = OutputTablesDTO.pricePointTableName;
        final String ppNew = ppOld + TEMP_TABLE_SUFIX;
        final String ppsOld = OutputTablesDTO.pricePointStoreTableName;
        final String ppsNew = ppsOld + TEMP_TABLE_SUFIX;
        final String pricePointJoinColumn = "price_point_id";


        try{

            this.databaseRepository.createTableBySchemaCopy(ppNew, ppOld);
            this.databaseRepository.createTableBySchemaCopy(ppsNew, ppsOld);

            this.pricePointService.ingestTable(outputTablesDTO.pricePoints(), ppNew);
            this.pricePointStoreService.ingestTable(outputTablesDTO.pricePointStore(), ppsNew);

            this.databaseRepository.swapTwoTablesWithDrop(ppOld, ppNew);
            this.databaseRepository.swapTwoTablesWithDrop(ppsOld, ppsNew);

        } catch (Exception e) {
            System.err.println(Text.ErrorMessages.TABLE_INGESTION_FAILED);
            System.err.println(e.getMessage());
            System.err.println(e.getStackTrace());
        }
    }

    private Map<Long, ProductDTO> createMap(){

        List<ProductDTO> products = this.brandNameProductsService.findAllProductIds();
        List<PricePointDTO> pricePoints = this.pricePointService.findAllCustom();
        List<PricePointStoreDTO> joinTable = this.pricePointStoreService.findAllCustom();

        Map<Long, ProductDTO> map = new HashMap<>();
        products.forEach(productDTO -> {
            map.put(productDTO.getProductId(), productDTO);
        });

        Map<Long, PricePointDTO> pricePointMap = new HashMap<>();
        pricePoints.forEach(pp -> {
            pricePointMap.put(pp.getId(), pp);
        });

        joinTable.forEach(row -> {
            PricePointDTO pricePointDTO = pricePointMap.get(row.getPricePointId());
            pricePointDTO.getPricePointStoreDTOS().add(row);
        });

        pricePointMap.forEach( (k, v) -> {
            ProductDTO productDTO = map.get(v.getProductId());
            productDTO.getPricePointDTOS().put(k,v);
        });

        Map<Long, ProductDTO> barcodeMap = new HashMap<>();
        map.forEach((k,v) ->{
            barcodeMap.put(v.getBarcode(), v);
        });
        return barcodeMap;
    }

    private void updateMap(List<ParsedValues> parsedValues, Map<Long, ProductDTO> loadedMap){
        for(ParsedValues pv : parsedValues) {
            //This path is if the product already exists
            if(loadedMap.containsKey(pv.getBarcode())){
                ProductDTO productDTO = loadedMap.get(pv.getBarcode());
                //First we try to find if the store exists in the data
                Optional<PricePointDTO> ppOpt = findPricePointByStore(pv.getStore() ,productDTO.getPricePointDTOS());
                //If we find the store we check if there is a change in pricing
                if(ppOpt.isPresent()){
                    PricePointDTO pp = ppOpt.get();
                    //If the price is the same we update the timestamp and continue
                    if(pp.getPrice().equals(pv.getPrice())){
                        PricePointStoreDTO ppsDTO = findPricePointStoreDTOByStoreId(pp.getPricePointStoreDTOS(), pv.getStore().getId());
                        assert ppsDTO != null;
                        ppsDTO.setLastUpdated(Instant.now());
                        continue;
                    }
                    //If there is a new price we must remove the store from the current price point
                    List<PricePointStoreDTO> ppsDTOList = pp.getPricePointStoreDTOS();
                    ppsDTOList.remove(findListIndexOfStore(ppsDTOList, pv.getStore().getId()));
                }
                //At this point the store is not associated with anything
                //Now we need to check if a price point for the current price exists
                ppOpt = findPricePointByPrice(pv.getPrice(), productDTO.getPricePointDTOS());
                //If we found it we add the store to it and continue
                if(ppOpt.isPresent()){
                    PricePointDTO pp = ppOpt.get();
                    pp.getPricePointStoreDTOS().add(new PricePointStoreDTO(pp.getId(), pv.getStore().getId(), Instant.now()));
                    continue;
                }
                //If it doesn't exist we create a new PricePoint and add the store
                //The entity below must exist since we are in the found barcode path
                BrandNameProduct brandNameProduct = this.brandNameProductsService.findByBarcode(pv.getBarcode()).get();
                PricePoint newPricePoint = this.pricePointService.save(new PricePoint(pv.getPrice(), brandNameProduct));
                PricePointDTO pp = new PricePointDTO(newPricePoint.getId(), newPricePoint.getPrice(), newPricePoint.getBrandNameProduct().getId());
                pp.getPricePointStoreDTOS().add(new PricePointStoreDTO(pp.getId(), pv.getStore().getId(), Instant.now()));
                //And we add the new price point to the product
                productDTO.getPricePointDTOS().put(pp.getProductId(), pp);
            }else {
             //This path is for new products
             BrandNameProduct newProduct = this.brandNameProductsService.save(BrandNameProduct.mapFromParsedValue(pv));
             PricePoint newPricePoint = this.pricePointService.save(new PricePoint(pv.getPrice(), newProduct));

             ProductDTO newProductDTO = new ProductDTO(newProduct.getId(), newProduct.getBarcode());
             PricePointDTO newPricePointDTO = new PricePointDTO(newPricePoint.getId(), newPricePoint.getPrice(), newPricePoint.getBrandNameProduct().getId());
             newPricePointDTO.getPricePointStoreDTOS().add(new PricePointStoreDTO(newPricePointDTO.getId(), pv.getStore().getId(), Instant.now()));
             newProductDTO.getPricePointDTOS().put(newPricePointDTO.getProductId(), newPricePointDTO);
            }
        }
    }

    private Optional<PricePointDTO> findPricePointByStore(Stores store, Map<Long, PricePointDTO> map){
        Set<Long> keys = map.keySet();
        for(Long key : keys) {
            PricePointDTO pp = map.get(key);
            for(PricePointStoreDTO dto : pp.getPricePointStoreDTOS()){
                if(store.getId().equals(dto.getStoreId())) return Optional.of(pp);
            }
        }
        return Optional.empty();
    }

    private Optional<PricePointDTO> findPricePointByPrice(BigDecimal price ,Map<Long, PricePointDTO> map){
        Set<Long> keys = map.keySet();
        for(Long key : keys){
            PricePointDTO pp = map.get(key);
            if(pp.getPrice().equals(price)) return Optional.of(pp);
        }
        return Optional.empty();
    }

    private int findListIndexOfStore(List<PricePointStoreDTO> list, int id){
        for(int idx = 0; idx < list.size(); idx++){
            PricePointStoreDTO dto = list.get(idx);
            if(dto == null) continue;
            if(dto.getStoreId().equals(id)) return idx;
        }
        return -1;
    }

    private PricePointStoreDTO findPricePointStoreDTOByStoreId(List<PricePointStoreDTO> list, int storeId){
        for(PricePointStoreDTO dto : list){
            if(dto.getStoreId().equals(storeId)) return dto;
        }
        return null;
    }

    private OutputTablesDTO createTables(Map<Long, ProductDTO> map){
        List<PricePointRowDTO> pricePointsTable = new ArrayList<>();
        List<PricePointStoreDTO> joinTable = new ArrayList<>();

        Set<Long> keys = map.keySet();
        for(Long key : keys){
            ProductDTO product = map.get(key);
            product.getPricePointDTOS().forEach((k,v) -> {
                PricePointRowDTO pprDTO = new PricePointRowDTO(v.getId(), v.getPrice(), v.getProductId());
                pricePointsTable.add(pprDTO);
                joinTable.addAll(v.getPricePointStoreDTOS());
            });
        }

        return new OutputTablesDTO(pricePointsTable, joinTable);
    }
}
