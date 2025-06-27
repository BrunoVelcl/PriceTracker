package Parser;

public class ParsedValues {

    private final Long barcode;
    private final Double price;
    private final String productName;
    private final String brand;
    private final String unit_quantity;
    private final String unit;
    private final String storeAddress;
    private final Integer storeID;
    private final Integer chainID;


    public ParsedValues(Long barcode, Double price, String productName, String brand, String unit_quantity, String unit, String storeAddress, Integer storeID, Integer chainID) {
        this.chainID = chainID;
        this.barcode = barcode;
        this.price = price;
        this.productName = productName;
        this.brand = brand;
        this.unit_quantity = unit_quantity;
        this.unit = unit;
        this.storeAddress = storeAddress;
        this.storeID = storeID;
    }

    public long getBarcode() {
        return barcode;
    }

    public Double getPrice() {
        return price;
    }

    public String getProductName() {
        return productName;
    }

    public String getBrand() {
        return brand;
    }

    public String getUnit_quantity() {
        return unit_quantity;
    }

    public String getUnit() {
        return unit;
    }

    public String getStoreAddress() {
        return storeAddress;
    }

    public Integer getStoreID() {
        return storeID;
    }

    public boolean isValidInput(){
        return (barcode != null) & (price != -1) & (storeID != null);
    }
}

