package DTO.response;

public class ProductNameBarcodeDTO {
    private final String productName;
    private final Long barcode;

    public ProductNameBarcodeDTO(String productName, Long barcode) {
        this.productName = productName;
        this.barcode = barcode;
    }

    public String getProductName() {
        return productName;
    }

    public Long getBarcode() {
        return barcode;
    }
}
