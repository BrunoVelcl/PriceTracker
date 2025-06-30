package Parser;

// Data container to help with creating parsed values and keeping that one private and final
public class ParsedValuesTempContainer {

    public Long barcode = null;
    public Double price = null;
    public String productName = null;
    public String brand = null;
    public String unit_quantity = null;
    public String unit = null;
    public StoreInfo storeInfo = null;

    public void resetContainer(){
        this.barcode = null;
        this.price = null;
        this.productName = null;
        this.brand = null;
        this.unit_quantity = null;
    }

    // Creates a parsed value from this container
    public ParsedValues createParsedValues(){
        return new ParsedValues(this.barcode, this.price, this.productName,this.brand,this.unit_quantity, this.unit,this.storeInfo);
    }
}
