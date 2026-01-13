package DataParser.entities;

public class ParsedValuesBuilder {

    private Long barcode;
    private Double price;
    private String productName;
    private String brand;
    private String unit_quantity;
    private String unit;
    private final Store store;

    public ParsedValuesBuilder(Store store) {
        this.store = store;
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

}
