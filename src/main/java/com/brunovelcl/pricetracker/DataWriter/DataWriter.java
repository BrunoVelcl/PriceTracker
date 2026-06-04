package com.brunovelcl.pricetracker.DataWriter;

import com.brunovelcl.pricetracker.DataParser.entities.ParsedValues;
import com.brunovelcl.pricetracker.database.DTOs.PricePointPricePointStoreDTO;
import com.brunovelcl.pricetracker.database.entities.BrandNameProduct;
import com.brunovelcl.pricetracker.database.entities.PricePoint;
import com.brunovelcl.pricetracker.database.entities.PricePointStore;
import com.brunovelcl.pricetracker.database.entities.Stores;
import com.brunovelcl.pricetracker.database.services.interfaces.BrandNameProductsService;
import com.brunovelcl.pricetracker.database.services.interfaces.PricePointService;
import com.brunovelcl.pricetracker.database.services.interfaces.PricePointStoreService;
import org.springframework.data.querydsl.binding.OptionalValueBinding;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class DataWriter {

    private final BrandNameProductsService brandNameProductsService;
    private final PricePointService pricePointService;
    private final PricePointStoreService pricePointStoreService;

    public DataWriter(BrandNameProductsService brandNameProductsService, PricePointService pricePointService, PricePointStoreService pricePointStoreService) {
        this.brandNameProductsService = brandNameProductsService;
        this.pricePointService = pricePointService;
        this.pricePointStoreService = pricePointStoreService;
    }


    public void updateDatabase(List<ParsedValues> parsedValues) {

        for (ParsedValues parsedValue : parsedValues) {

            Stores store = parsedValue.getStore();

            Optional<BrandNameProduct> productOptional = this.brandNameProductsService.findByBarcode(parsedValue.getBarcode());

            //This branch runs fow new products
            if (productOptional.isEmpty()) {
                BrandNameProduct newProduct = this.brandNameProductsService.save(BrandNameProduct.mapFromParsedValue(parsedValue));
                PricePoint newPricePoint = this.pricePointService.save(new PricePoint(parsedValue.getPrice(), newProduct));
                PricePointStore newPricePointStore = new PricePointStore(newPricePoint.getId(), store.getId());
                this.pricePointStoreService.save(newPricePointStore);
                continue;
            }

            BrandNameProduct product = productOptional.get();

            //If product exist query for PricePoints and PricePointStore

                Optional<PricePointPricePointStoreDTO> dtoOpt = this.pricePointStoreService.findByBrandNameProductAndStore(product.getId(), store.getId());


            if(dtoOpt.isPresent()) {
                PricePointPricePointStoreDTO dto = dtoOpt.get();
                PricePointStore currentPPS = new PricePointStore(dto.getPricePointStoreId(), dto.getPricePointId(), dto.getStoreId(), dto.getLastUpdated());
                // If prices haven't changed, log update and return
                if(dto.getPrice().equals(parsedValue.getPrice())){
                    currentPPS.setLastUpdated(Instant.now());
                    this.pricePointStoreService.save(currentPPS);
                    continue;
                }
                // If prices changed remove PricePointStoreEntry
                this.pricePointStoreService.delete(currentPPS);
            }


                //At this point the entry doesn't exist, we need to check if there is a PricePoint with the current price
                Optional<PricePoint> existingPPOpt = this.pricePointService.findByPriceAndBrandNameProduct(parsedValue.getPrice(), product);

                //If a PricePoint exists associate it with the store, if it doesn't create it and associate it
                if (existingPPOpt.isPresent()) {
                    PricePoint existingPP = existingPPOpt.get();
                    this.pricePointStoreService.save(new PricePointStore(existingPP.getId(), store.getId()));
                } else {
                    PricePoint newPricePoint = new PricePoint(parsedValue.getPrice(), product);
                    this.pricePointService.save(newPricePoint);
                    this.pricePointStoreService.save(new PricePointStore(newPricePoint.getId(), store.getId()));
                }
        }

    }

}
