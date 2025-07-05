package Engine;

import Parser.ParsedValues;
import Parser.ProductInfo;
import Parser.StoreInfo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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

    public double getPrice() {
        return price;
    }

    public ProductInfo getProductInfo() {
        return productInfo;
    }

    private void setPreviousNode(PricePoint previousNode) {
        this.previousNode = previousNode;
    }

    private void setNextNode(PricePoint nextNode) {
        this.nextNode = nextNode;
    }

    // The name of this function is kinda funky since, the function return true if Price point contains a given store,
    // meaning in the real world true if this price is in this store
    private boolean inStore(StoreInfo oStore){
        for (StoreInfo store : this.stores){
            if(store.getAddress().equals(oStore.getAddress())) {
                return true;
            }
        }
        return false;
    }
    //***Returns a list of stores from the current node*/
    public List<StoreInfo> getStores() {
        return stores;
    }

    //  Add store to list
    public void addStore(StoreInfo oStore){
        this.stores.add(oStore);
    }

    // Remove store from list
    private void removeStore(StoreInfo oStore){
        this.stores.remove(oStore);
    }

    // Open next node
    public PricePoint getNextNode() {
        return nextNode;
    }

    // Open previous node
    public PricePoint getPreviousNode(){
        return previousNode;
    }
    // Add node
    private void addPricePoint(double Price){
        this.nextNode = new PricePoint(this.productInfo,Price);
        nextNode.setPreviousNode(this);
    }

    //Remove node
    private void removePricePoint(){
        if(previousNode != null) {
            previousNode.setNextNode(this.nextNode);
        }
        if(nextNode != null)
        {
            nextNode.setPreviousNode(this.previousNode);
        }
        this.nextNode = null;
        this.previousNode = null;
    }

    // Search for price node
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

    // Search for StoreInfo
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

    // Update logic
    public void updatePrice(PricePoint firstNode, ParsedValues pv) {
        PricePoint pricePointExists = getPricePointByPrice(firstNode, pv.getPrice());
        if (pricePointExists != null) {
            if (!pricePointExists.inStore(pv.getStoreInfo())) { //Yes price is already up to date, no add store to price node
                PricePoint currentPrice = getPricePointByStoreAddress(firstNode, pv.getStoreInfo());
                if (currentPrice != null){ // Found current price, now we remove it
                    currentPrice.removeStore(pv.getStoreInfo());
                    if(currentPrice.getStores().isEmpty()){
                        currentPrice.removePricePoint(); //Remove node if no stores have this price point
                    }
                }
                pricePointExists.addStore(pv.getStoreInfo()); //Add the new price now
            }
        } else {
            PricePoint currentPrice = getPricePointByStoreAddress(firstNode, pv.getStoreInfo());
            if (currentPrice != null) { // Price needs to be updated
                currentPrice.removeStore(pv.getStoreInfo());
                if(currentPrice.getStores().isEmpty()){
                    currentPrice.removePricePoint(); //Remove node if no stores have this price point
                }
            }
            PricePoint node = firstNode;
            while (node.getNextNode() != null) { //Forward to last active node
                node = node.getNextNode();
            }
            node.addPricePoint(pv.getPrice());
            node = node.getNextNode();
            node.addStore(pv.getStoreInfo());
        }
    }

    @Override
    public int compareTo(PricePoint o) {
        return Double.compare(this.price, o.getPrice());
    }
}
