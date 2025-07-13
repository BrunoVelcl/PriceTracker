package Engine;

import Parser.ParsedValues;
import Parser.ProductInfo;
import Parser.StoreInfo;
import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class BarcodeMap implements Serializable{
    private final ConcurrentHashMap<Long, PricePoint> barcodeMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Long> productToBarcode = new ConcurrentHashMap<>();
    private final List<StoreInfo> stores = new ArrayList<>();

    public List<StoreInfo> getStores() {
        return stores;
    }

    /**Give the data structure a parsed value to process.
     * Returns <i>true</i> if there was a change.
     * Returns <i>false</i> if nothing changed.*/
    public boolean update(ParsedValues pv){
        if(barcodeMap.containsKey(pv.getBarcode())){
            PricePoint firstNode = barcodeMap.get(pv.getBarcode());
            return firstNode.updatePrice(firstNode, pv, this.barcodeMap);
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
            return true;
        }
    }
    /**Searches the structure for all prices of a single product and returns them*/
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

    /**Returns the barcode of a product given its name.*/
    public long getBarcodeForProductName(String pName){
        return this.productToBarcode.get(pName);
    }

    /**Returns store id of the store in the stores List or null if not found*/
    public Integer getStoreIdFromAddress(String storeAddress){
        for(StoreInfo store : this.stores){
            if(store.getAddress().equals(storeAddress)){
                return store.getId();
            }
        }
        return null;
    }

    /** Return a list of keys that contain more than one price point.*/
    public List<Long> findPriceVariations(){
        List<Long> foundKeys = new ArrayList<>();
        for(Long key : barcodeMap.keySet()){
            if(barcodeMap.get(key).getNextNode() != null){
                foundKeys.add(key);
            }
        }
        return foundKeys;
    }

    /**Takes a list of stores and returns a list of barcodes where one of those stores price for a given item,
     * is not the worst price currently available*/
    public List<Long> findDealForSelectedStores(List<StoreInfo> storesToCheck){
        List<Long> foundKeys = new ArrayList<>();
        keyIteration:
            for(Long key : barcodeMap.keySet()) {
                List<PricePoint> temp = new ArrayList<>();
                PricePoint node = barcodeMap.get(key);
                while (node != null) {
                    temp.add(node);
                    node = node.getNextNode();
                }
                if(temp.size() > 1) {
                    temp.sort(null);
                    for (int i = 0; i < temp.size(); i++) {
                        for (StoreInfo si : temp.get(i).getStores()) {
                            for (StoreInfo oStores : storesToCheck) {
                                if (oStores.compare(si, oStores) == 0) {
                                    if(i < temp.size()-1) {
                                        foundKeys.add(key);
                                        continue keyIteration;
                                    }

                                }
                            }
                        }
                    }
                }
            }
        return foundKeys;
    }

    /**Adds a new store if it doesn't exist.*/
    public void storeUpdate(StoreInfo storeInfo) {
        if(!this.stores.contains(storeInfo)){
            stores.add(storeInfo);
        }
    }

    /**Returns a list of products that match to the search term that was passed in.*/
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


