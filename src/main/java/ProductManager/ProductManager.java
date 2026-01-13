package ProductManager;

import DTO.response.ProductNameBarcodeDTO;
import DataParser.entities.ParsedValues;
import DataParser.entities.ParsedValuesContainer;
import ProductManager.Entities.PricePoint;
import ProductManager.Entities.Product;
import ProductManager.SaveFIleManager.SaveFileManager;
import jdk.jshell.spi.ExecutionControl;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class ProductManager {

    private final ConcurrentHashMap<Long, Product> productHashMap;
    private final ConcurrentHashMap<String, Long> productNameToBarcodeMap;

    public ProductManager() {
        this.productHashMap = new ConcurrentHashMap<>();
        this.productNameToBarcodeMap = new ConcurrentHashMap<>();
    }

    public ProductManager(ConcurrentHashMap<Long, Product> productHashMap) {
        this.productHashMap = productHashMap;
        this.productNameToBarcodeMap = new ConcurrentHashMap<>();
        this.productHashMap.forEach((k, v) -> {
            this.productNameToBarcodeMap.put(v.getName(), k);
        });

    }

    //TODO: this updates only products with valid barcode
    public void update(ParsedValuesContainer parsedValues) {
        this.updateBrandedProducts(parsedValues.getBrandedProducts());
        //TODO: implement the method below
        //this.updateBrandedProducts(parsedValues.getChainSpecificProducts());
    }

    private void updateBrandedProducts(List<ParsedValues> brandedProducts) {
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
        SaveFileManager.save(this.productHashMap, sb);
    }

    public static ProductManager load() {
        return new ProductManager(SaveFileManager.load());
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

}
