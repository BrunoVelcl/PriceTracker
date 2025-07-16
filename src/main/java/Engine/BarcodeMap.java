package Engine;

import FileFetcher.Store;
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
    @Serial
    private static final long serialVersionUID = -8123781007419010359L; //TODO: remove when running on a server, only used while in development so that everything doesn't brake when a trivial change is made.

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

    public void save(File saveFile){
        final short transition = -2; //Marks transition between mapped data and stores
        final long terminator = -1; //Marks EOF
        final double notPrice = -1;
        try(DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(saveFile)))){
            for(StoreInfo store : this.stores){
                dos.writeShort(store.getId());
                dos.writeUTF(store.getAddress());
                dos.writeByte(store.getChain().ordinal());
            }
            dos.writeShort(transition);
            for(long barcode : this.barcodeMap.keySet()){
                PricePoint pp = this.barcodeMap.get(barcode);
                dos.writeLong(pp.getProductInfo().getBarcode());
                dos.writeUTF(pp.getProductInfo().getProductName());
                dos.writeUTF(pp.getProductInfo().getBrand());
                dos.writeUTF(pp.getProductInfo().getUnit_quantity());
                dos.writeUTF(pp.getProductInfo().getUnit());
                while (pp != null){
                    dos.writeDouble(pp.getPrice());
                    dos.writeShort(pp.getStores().size()); //The number of shorts that are after this point
                    for(StoreInfo store : pp.getStores()){
                        dos.writeShort(store.getId());
                    }
                    pp = pp.getNextNode();
                }
                dos.writeDouble(notPrice);
            }
            dos.writeLong(terminator);
        }catch (IOException e){
            System.err.println("Problem with writing to savefile: " + e.getMessage());
        }
    }

    public void load(File saveFile){
        final long transition = -2L; //Marks transition between mapped data and stores
        final long terminator = -1; //Marks EOF
        final double notPrice = -1;
        long currentBarcode = -1;
        String currentProductName = "";
        String currentBrand = "";
        String currentUnitQuantity = "";
        String currentUnit = "";
        double currentPrice = -1;
        short idsToRead = 0;
        String currentAddress = "";
        Store currentChain = null;
        int currentId = -2;
        try(DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(saveFile)))){

            while(true){
                currentId = dis.readShort();
                if(currentId == transition) {break;}
                currentAddress = dis.readUTF();
                currentChain = Store.values()[dis.readByte()];
                this.stores.add(new StoreInfo(currentAddress, currentChain, currentId));
            }
            while(true){
                currentBarcode = dis.readLong();
                if(currentBarcode == terminator){break;}
                currentProductName = dis.readUTF();
                currentBrand = dis.readUTF();
                currentUnitQuantity = dis.readUTF();
                currentUnit = dis.readUTF();
                while (true) {
                    currentPrice = dis.readDouble();
                    if(currentPrice == notPrice){break;}
                    PricePoint pp = new PricePoint(new ProductInfo(currentBarcode, currentProductName, currentBrand, currentUnitQuantity, currentUnit),currentPrice);
                    if(!this.barcodeMap.containsKey(currentBarcode)){
                        this.barcodeMap.put(currentBarcode, pp);
                        this.productToBarcode.put(currentProductName, currentBarcode);
                    }else{
                        PricePoint node = barcodeMap.get(currentBarcode);
                        while (node.getNextNode() != null){
                            node = node.getNextNode();
                        }
                        node.setNextNode(pp);
                        pp.setPreviousNode(node);
                    }

                    idsToRead = dis.readShort();
                    for(short i = 0; i < idsToRead; i++){
                        pp.addStore(this.stores.get(dis.readShort()));
                    }
                }
            }
        }catch (IOException e){
            System.err.println("Problem with reading saveFile: " + e.getMessage());
        }
    }
}


