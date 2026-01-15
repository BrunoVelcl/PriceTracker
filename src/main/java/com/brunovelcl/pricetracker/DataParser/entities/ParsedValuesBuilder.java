package com.brunovelcl.pricetracker.DataParser.entities;

public class ParsedValuesBuilder {

    private Long barcode;
    private Double price;
    private String productName;
    private String brand;
    private String unit_quantity;
    private String unit;
    private Store store;

    public ParsedValuesBuilder() {
    }

    public void barcode(Long barcode) {
        this.barcode = barcode;
    }

    public void price(Double price) {
        this.price = price;
    }

    public void productName(String productName) {
        this.productName = productName;
    }

    public void brand(String brand) {
        this.brand = brand;
    }

    public void unit_quantity(String unit_quantity) {
        this.unit_quantity = unit_quantity;
    }

    public void unit(String unit) {
        this.unit = unit;
    }

    public void store(Store store){
        this.store = store;
    }

    private void reset(){
        this.barcode = null;
        this.price = null;
        this.productName = null;
        this.brand = null;
        this.unit_quantity = null;
        this.unit = null;
    }

    private boolean isValidParsedValue(){
        return (this.price != null);
    }

    public ParsedValues consume(){
        if(!isValidParsedValue()) {
            this.reset();
            return null;
        }

        ParsedValues pv =  new ParsedValues(this.barcode, this.price, this.productName, this.brand, this.unit_quantity, this.unit, this.store);
        this.reset();
        return pv;
    }

    // temp to string for debug
    @Override
    public String toString() {
        return this.productName;
    }
}
