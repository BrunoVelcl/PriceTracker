package com.brunovelcl.pricetracker.ProductManager;

import com.brunovelcl.pricetracker.DTO.request.PriceQueryDTO;
import com.brunovelcl.pricetracker.DTO.response.PriceDataDTO;
import com.brunovelcl.pricetracker.DTO.response.ProductNameBarcodeDTO;
import com.brunovelcl.pricetracker.DataParser.entities.ParsedValues;
import com.brunovelcl.pricetracker.DataParser.entities.ParsedValuesContainer;
import com.brunovelcl.pricetracker.DataParser.entities.Store;
import com.brunovelcl.pricetracker.DataParser.repositories.StoreRepo;
import com.brunovelcl.pricetracker.DataParser.repositories.StoreRepoImpl;
import com.brunovelcl.pricetracker.ProductManager.Entities.PricePoint;
import com.brunovelcl.pricetracker.ProductManager.Entities.Product;
import com.brunovelcl.pricetracker.ProductManager.SaveFIleManager.SaveFileManager;
import jdk.jshell.spi.ExecutionControl;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ProductManager {

    private final ConcurrentHashMap<Long, Product> productHashMap;
    private final ConcurrentHashMap<String, Long> productNameToBarcodeMap;
    private final StoreRepo storeRepo;

    public ProductManager() {
        this.productHashMap = SaveFileManager.loadBackup();
        this.productNameToBarcodeMap = new ConcurrentHashMap<>();
        this.productHashMap.forEach((k, v) -> {
            this.productNameToBarcodeMap.put(v.getName(), k);
        });
        this.storeRepo = StoreRepoImpl.load();
    }

    public ProductManager(ConcurrentHashMap<Long, Product> productHashMap) {
        this.productHashMap = productHashMap;
        this.productNameToBarcodeMap = new ConcurrentHashMap<>();
        this.productHashMap.forEach((k, v) -> {
            this.productNameToBarcodeMap.put(v.getName(), k);
        });
        this.storeRepo = StoreRepoImpl.load();

    }

    //TODO: this updates only products with valid barcode
    public void update(ParsedValuesContainer parsedValues) {
        this.updateBrandedProducts(parsedValues.getBrandedProducts().keySet());
        //TODO: implement the method below
        //this.updateBrandedProducts(parsedValues.getChainSpecificProducts());
    }

    private void updateBrandedProducts(Set<ParsedValues> brandedProducts) {
        for (ParsedValues value : brandedProducts) {
            try {
                if (!this.productHashMap.containsKey(value.getBarcode())) {
                    this.productHashMap.put(value.getBarcode(), new Product(value));
                    this.productNameToBarcodeMap.put(value.getProductName(), value.getBarcode());
                    continue;
                }
            } catch (Exception e) {
                System.err.println(e.getMessage());
                System.err.println(value.toString());
            }
            if (this.checkIfCorrect(value)) {
                continue;
            }

            updateProduct(value);
        }
    }

    private void updateChainSpecificProducts(List<ParsedValues> chainSpecificProducts) throws ExecutionControl.NotImplementedException {
        throw new ExecutionControl.NotImplementedException("Method not implemented!");
    }

    public void save(StringBuilder sb) {
        SaveFileManager.saveBackup(this.productHashMap, sb);
    }

    public static ProductManager loadBackup() {
        return new ProductManager(SaveFileManager.loadBackup());
    }

    public void loadFromParsedValues(){
        this.updateBrandedProducts(SaveFileManager.loadParsedValues());
        this.storeRepo.loadFromFile();
    }

    //TODO: this method should never return true, after ParsedValues are cleaned correctly it can be deleted.
    private boolean checkIfCorrect(ParsedValues parsedValue) {
        Product product = this.productHashMap.get(parsedValue.getBarcode());
        Optional<PricePoint> pricePoint = product.findPricePoint(parsedValue.getPrice());
        return pricePoint.isPresent() && pricePoint.get().getStores().contains(parsedValue.getStoreInfo());
    }

    private void updateProduct(ParsedValues parsedValue) {
        Product product = this.productHashMap.get(parsedValue.getBarcode());
        product.updatePrice(parsedValue.getPrice(), parsedValue.getStoreInfo());
    }

    public Optional<Product> findByBarcode(Long barcode) {
        return (this.productHashMap.containsKey(barcode)) ? Optional.of(this.productHashMap.get(barcode)) : Optional.empty();
    }

    public List<ProductNameBarcodeDTO> findByProductName(String name) {
        List<ProductNameBarcodeDTO> foundMatches = new ArrayList<>();
        this.productNameToBarcodeMap.forEach((k, v) -> {
            if (k.toLowerCase().contains(name.toLowerCase())) {
                foundMatches.add(new ProductNameBarcodeDTO(k, v));
            }
        });
        return foundMatches;
    }

    public List<Store> getStores(){
        return this.storeRepo.getStores();
    }

    public List<PriceDataDTO> getPrices(PriceQueryDTO request){
        List<PriceDataDTO> responseList = new ArrayList<>();
        request.getProductCodes().forEach( code -> {
            Product product = this.productHashMap.get(code);
            request.getStoreCodes().forEach( store ->
                    responseList.add(new PriceDataDTO(store, code, product.findPriceByStoreId(store))));
        });
        return responseList;
    }

}
