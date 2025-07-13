package Engine;

import Parser.ParsedValues;
import Parser.ProductInfo;
import Parser.StoreInfo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class PricePoint implements Comparable<PricePoint>, Serializable {
    private final ProductInfo productInfo;
    private final double price;
    private List<StoreInfo> stores = null;
    private PricePoint previousNode = null;
    private PricePoint nextNode = null;

    public PricePoint(ProductInfo productInfo, double price) {
        this.productInfo = productInfo;
        this.price = price;
        this.stores = new ArrayList<>();
    }

    public ProductInfo getProductInfo() {
        return this.productInfo;
    }

    public double getPrice() {
        return this.price;
    }

    public List<StoreInfo> getStores() {
        return this.stores;
    }

    private void setPreviousNode(PricePoint previousNode) {
        this.previousNode = previousNode;
    }

    private void setNextNode(PricePoint nextNode) {
        this.nextNode = nextNode;
    }

    /**Returns true if the given store is in this price point.*/
    // The name of this function is kinda funky since, the function returns true if Price point contains a given store,
    // meaning in the real world true if this price is in this store.
    private boolean inStore(StoreInfo oStore){
        for (StoreInfo store : this.stores){
            if(store.getAddress().equals(oStore.getAddress())) {
                return true;
            }
        }
        return false;
    }

    /**Add store to node*/
    public void addStore(StoreInfo oStore){
        this.stores.add(oStore);
    }

    /**Remove store from node*/
    private void removeStore(StoreInfo oStore, ConcurrentHashMap<Long ,PricePoint> hashMap){
        this.stores.remove(oStore);
        if(this.stores.isEmpty()){
            this.removePricePoint(hashMap);
        }
    }

    /**Open next node*/
    public PricePoint getNextNode() {
        return nextNode;
    }

    /**Open previous node*/
    @SuppressWarnings("unused") //Currently unused, but removing it would imply a rewrite of the whole class to a singly linked list
    public PricePoint getPreviousNode(){
        return previousNode;
    }
    /**Add node*/
    private void addPricePoint(double Price){
        this.nextNode = new PricePoint(this.productInfo,Price);
        nextNode.setPreviousNode(this);
    }

    /**Remove node*/
    private void removePricePoint(ConcurrentHashMap<Long, PricePoint> hashMap){

        if(this.previousNode == null & this.nextNode == null){
            hashMap.remove(this.productInfo.getBarcode());
        }
        if(this.previousNode == null & this.nextNode != null){
            hashMap.replace(nextNode.productInfo.getBarcode(), nextNode);
        }
        if(this.previousNode != null & this.nextNode == null){
            previousNode.setNextNode(null);
        }
        if(this.previousNode != null & this.nextNode != null)
        {
            previousNode.setNextNode(nextNode);
            nextNode.setPreviousNode(previousNode);
        }

        this.previousNode = null;
        this.nextNode = null;
    }

    /**Search for price node*/
    private static PricePoint getPricePointByPrice(PricePoint firstNode,Double price){
        PricePoint node = firstNode;
        while (node != null){
            if(Math.abs(node.getPrice() - price) < 0.01){
                return node;
            }
            node = node.getNextNode();
        }
        return null;
    }

    /**Search for StoreInfo*/
    private static PricePoint getPricePointByStoreAddress(PricePoint firstNode, StoreInfo store){
        PricePoint node = firstNode;
        while (node != null){
            if(node.inStore(store)){
                return node;
            }
            node = node.getNextNode();
        }
        return null;
    }

    /**Update logic*/
    public boolean updatePrice(PricePoint firstNode, ParsedValues pv, ConcurrentHashMap<Long, PricePoint> hashMap) {
        PricePoint pricePointExists = getPricePointByPrice(firstNode, pv.getPrice());
        if (pricePointExists != null) {
            if (!pricePointExists.inStore(pv.getStoreInfo())) { //Yes price is already up to date, no add store to price node
                PricePoint currentPrice = getPricePointByStoreAddress(firstNode, pv.getStoreInfo());
                if (currentPrice != null){ // Found current price, now we remove it
                    currentPrice.removeStore(pv.getStoreInfo(), hashMap);
                }
                pricePointExists.addStore(pv.getStoreInfo());//Add the new price now
                return true;
            }
        } else {
            PricePoint currentPrice = getPricePointByStoreAddress(firstNode, pv.getStoreInfo());
            if (currentPrice != null) { // Price needs to be updated
                currentPrice.removeStore(pv.getStoreInfo(), hashMap);
            }
            PricePoint node = firstNode;
            while (node.getNextNode() != null) { //Forward to last active node
                node = node.getNextNode();
            }
            node.addPricePoint(pv.getPrice());
            node = node.getNextNode();
            node.addStore(pv.getStoreInfo());
            return true;
        }
        return false;
    }

    @Override
    public int compareTo(PricePoint o) {
        return Double.compare(this.price, o.getPrice());
    }
}
