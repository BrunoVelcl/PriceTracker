package Engine;

import Parser.ParsedValues;
import Parser.ProductInfo;
import Parser.StoreInfo;

import java.io.*;
import java.nio.file.Path;
import java.util.*;

public class BarcodeMap implements Serializable{
    private final Map<Long, PricePoint> barcodeMap = new HashMap<>();
    private final Map<String, Long> productToBarcode = new HashMap<>();
    private final Set<StoreInfo> storeSet = new HashSet<>();

    public void update(ParsedValues pv){
        if(barcodeMap.containsKey(pv.getBarcode())){
            PricePoint firstNode = barcodeMap.get(pv.getBarcode());
            firstNode.updatePrice(firstNode, pv);
        }else {
            barcodeMap.put(
                    pv.getBarcode(),
                    new PricePoint(
                            new ProductInfo(
                                    pv.getBarcode(), pv.getProductName(), pv.getBrand(), pv.getUnit_quantity(), pv.getUnit()
                            ),
                            pv.getPrice()
                    )
            );
            productToBarcode.put(pv.getProductName(), pv.getBarcode());
            PricePoint firstNode = barcodeMap.get(pv.getBarcode());
            firstNode.addStore(pv.getStoreInfo());
        }
    }

    public List<PricePoint> getPricesForBarcode(Long barcode){
        if(!barcodeMap.containsKey(barcode)){
            return null;
        }
        PricePoint Node = barcodeMap.get(barcode);
        List<PricePoint> list = new ArrayList<>();
        while(Node != null){
            list.add(Node);
            Node = Node.getNextNode();
        }
        Collections.sort(list);
        return list;
    }

    public List<PricePoint> getPricesForProductName(String pName){
        return getPricesForBarcode(this.productToBarcode.get(pName));
    }

    public long getBarcodeForProductName(String pName){
        return this.productToBarcode.get(pName);
    }

    public Set<StoreInfo> getStoreSet() {
        return storeSet;
    }

    // Return a set of keys that contain more than one price point.
    public List<Long> findPriceVariations(){
        List<Long> foundKeys = new ArrayList<>();
        for(Long key : barcodeMap.keySet()){
            if(barcodeMap.get(key).getNextNode() != null){
                foundKeys.add(key);
            }
        }
        return foundKeys;
    }

    public List<Long> findDealForSelectedStores(List<StoreInfo> storesToCheck){
        List<Long> foundKeys = new ArrayList<>();
        keyIterator:
            for(Long key : barcodeMap.keySet()) {
                PricePoint node = barcodeMap.get(key);
                while (node.getNextNode() != null) { // This condition will not check the last node
                    for (StoreInfo store : node.getStores()) {
                        for (StoreInfo oStore : storesToCheck) {
                            if (store.equals(oStore)) {
                                foundKeys.add(key);
                                continue keyIterator;
                            }
                        }
                    }
                node = node.getNextNode();
                }
            }
        return foundKeys;
    }

    public void storeUpdate(StoreInfo storeInfo) {
        this.storeSet.add(storeInfo);
    }

    public List<String> searchProduct(String searchTerm){
        List<String> found = new ArrayList<>();
        Set<String> keySet = this.productToBarcode.keySet();
        for (String key : keySet){
            if(key.toLowerCase().contains(searchTerm.toLowerCase())){
                found.add(key);
            }
        }
        return found;
    }
}


