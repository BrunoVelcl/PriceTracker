package Parser;

import java.io.Serializable;
import java.util.Comparator;

public class ProductInfo implements Serializable {
    private final Long barcode;
    private final String productName;
    private final String brand;
    private final String unit_quantity;
    private final String unit;

    public ProductInfo(Long barcode, String productName, String brand, String unit_quantity, String unit) {
        this.barcode = barcode;
        this.productName = productName;
        this.brand = brand;
        this.unit_quantity = unit_quantity;
        this.unit = unit;
    }

    public static Comparator<ProductInfo> byBarcode = Comparator.comparing(ProductInfo::getBarcode);
    public static Comparator<ProductInfo> byProductName = Comparator.comparing(ProductInfo::getProductName).thenComparing(ProductInfo::getBarcode);
    public static Comparator<ProductInfo> byBrand = Comparator.comparing(ProductInfo::getBrand).thenComparing(ProductInfo::getProductName);

    public Long getBarcode() {
        return barcode;
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

}
