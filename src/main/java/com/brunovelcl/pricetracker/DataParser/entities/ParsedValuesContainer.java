package com.brunovelcl.pricetracker.DataParser.entities;


import com.brunovelcl.pricetracker.Text.Text;
import java.util.*;

public class ParsedValuesContainer {

    private static final Long BARCODE_BORDER_VALUE = 100000L; //placeholder value

    private final HashMap<ParsedValues, ParsedValues> brandedProducts;
    private final List<ParsedValues> chainSpecificProducts;

    public ParsedValuesContainer() {
        this.brandedProducts = new HashMap<>();
        this.chainSpecificProducts = new ArrayList<>();
    }

    public HashMap<ParsedValues, ParsedValues> getBrandedProducts() {
        return brandedProducts;
    }

    public List<ParsedValues> getChainSpecificProducts() {
        return chainSpecificProducts;
    }

    public void add(ParsedValues parsedValue){
        Long barcode = parsedValue.getBarcode();
        if(barcode == null || barcode < BARCODE_BORDER_VALUE){
            this.chainSpecificProducts.add(parsedValue);
        }else {
            if(!this.isDuplicate(parsedValue)) {
                this.brandedProducts.put(parsedValue, parsedValue);
            }
        }
    }

    public boolean isEmpty(){
        return this.chainSpecificProducts.isEmpty() && this.brandedProducts.isEmpty();
    }

    private boolean isDuplicate(ParsedValues parsedValue){
        ParsedValues foundDuplicate = this.brandedProducts.get(parsedValue);
        if(foundDuplicate == null) return false;
        if(foundDuplicate.getPrice().equals(parsedValue.getPrice())) return true;
        System.out.printf(Text.Messages.CONFLICTING_DATA_ENTRY,
                foundDuplicate.getProductName(), foundDuplicate.getBarcode(), foundDuplicate.getStoreInfo().getAddress(), foundDuplicate.getStoreInfo().getChain(), foundDuplicate.getPrice(),
                parsedValue.getProductName(), parsedValue.getBarcode(), parsedValue.getStoreInfo().getAddress(), parsedValue.getStoreInfo().getChain(), parsedValue.getPrice());
        return true;
    }

}
