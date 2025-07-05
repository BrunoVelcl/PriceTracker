package Engine;

import FileFetcher.Store;
import Parser.ParserLidl;
import Parser.ProductInfo;
import Parser.StoreInfo;

import java.io.*;
import java.util.*;

public class Engine {
    private BarcodeMap barcodeMap = new BarcodeMap();
    private final File defaultFile = new File("save.bin");
    private List<StoreInfo> selectedStores = new ArrayList<>();
    private final File userData = new File("userData.bin");

    // Save method with custom dir path
    public void save(File save){
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(save));
            oos.writeObject(this.barcodeMap);
        }catch (IOException e){
            System.err.println("File read error: " + e.getMessage());
        }
    }

    // Default save method
    public void save() {
        save(defaultFile);
    }

    // Load method with custom dir path
    public void load(File save) {
        try {
           ObjectInputStream ois = new ObjectInputStream(new FileInputStream(save));
           this.barcodeMap = (BarcodeMap) ois.readObject();
        } catch (IOException e) {
            System.err.println("File read error: " + e.getMessage());
        } catch (ClassNotFoundException e){
            System.err.println("Couldn't read object from provided file: " + e.getMessage());
        }
    }

    // Default load method
    public void load(){
        load(defaultFile);
    }

    private void printPrices(long barcode){
        List<PricePoint> pricePoints = this.barcodeMap.getPricesForBarcode(barcode);
        System.out.println("\u001b[37m");
        for(PricePoint pricePoint : pricePoints){
            double price = pricePoint.getPrice();
            String product = pricePoint.getProductInfo().getProductName();
            String brand = pricePoint.getProductInfo().getBrand();
            System.out.println("\u001b[94mProizvod: \u001b[92m" + product);
            System.out.println("\u001b[94mBrand: \u001b[92m" + brand);
            System.out.println("\u001b[94mCijena: \u001b[97m" + price);
            List<StoreInfo> stores = pricePoint.getStores();
            for(StoreInfo store : stores){
                System.out.print(store.getChain() + ":" + store.getAddress() + " || ");
            }
            System.out.println("\n\u001b[37m*****************************************************++++++++++++++++++++");
        }
    }

    public void run(){
        Scanner scanner = new Scanner(System.in);
        boolean run = true;
        long barcode;
        String option;
        Engine engine = new Engine();
        engine.load();
        engine.loadUserData();
        while (run){
            System.out.println("Options: 1. Barcode search | 2. Product Search | 3. Find deals for selected. | 4. All diffs | 5. Store Selector | 6. Clear user data. | 7. View selections | Q. quit");
            option = scanner.nextLine();
            switch (option){
                case "1" -> {
                    System.out.print("Enter barcode: ");
                    barcode = scanner.nextLong();
                    engine.printPrices(barcode);
                }
                case "2" -> {
                    String product = engine.productSelector(scanner);
                    if(product.equals("Invalid selection")){
                        System.out.println(product);
                    }else {
                        engine.printPrices(engine.barcodeMap.getBarcodeForProductName(product));
                    }
                }
                case "3" -> {
                    List<Long> keys = engine.barcodeMap.findDealForSelectedStores(engine.selectedStores);
                    for (Long key : keys){
                        engine.printPrices(key);
                    }
                }
                case "4" -> {
                    List<Long> keys = engine.barcodeMap.findPriceVariations();
                    for(Long key : keys){
                        engine.printPrices(key);
                    }
                }
                case "5" -> {
                    engine.storeSelector(scanner);
                }
                case "6" -> {
                    engine.selectedStores.clear();
                }
                case "7" -> {
                    engine.viewSelected();
                }
                case "q" ->{
                    run = false;
                }
            }

        }
        engine.saveUserData();

    }

    //Only use when setting up for the first time
    public void firstTime(){
        ParserLidl parsLidl = new ParserLidl();
        parsLidl.run(this.barcodeMap);
        save();
    }

    public void storeSelector(Scanner scanner){
        List<StoreInfo> allStores = new ArrayList<>();
        for (StoreInfo storeInfo : barcodeMap.getStoreSet()){
            allStores.add(storeInfo);
            System.out.println(allStores.size()-1 + ". " + storeInfo.getChain().toString() + " | " + storeInfo.getAddress());
        }
        while (true) {
            System.out.println("Enter store number to add or anything else to exit: ");
            String input = scanner.nextLine();
            try {
                int index = Integer.parseInt(input);
                if (index < 0 | index > allStores.size() - 1) {
                    return;
                }
                this.selectedStores.add(allStores.get(index));
            } catch (NumberFormatException e) {
                return;
            }
        }
    }

    private String productSelector(Scanner scanner){
        System.out.println("Enter product: ");
        String input = scanner.nextLine();
        List<String> foundProducts = this.barcodeMap.searchProduct(input);
        if(foundProducts.isEmpty()){
            return "Invalid selection";
        }
        for(int i = 0; i < foundProducts.size(); i++){
            System.out.println(i + ". " + foundProducts.get(i));
        }
        System.out.println("Select product: ");
        input = scanner.nextLine();
        int index;
        try{
            index = Integer.parseInt(input);
            if(index < 0 | index > foundProducts.size()){
                return "Invalid selection";
            }
            return foundProducts.get(index);
        }catch (NumberFormatException e){
            return "Invalid selection";
        }
    }

    private void saveUserData(){
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(this.userData));
            oos.writeObject(this.selectedStores);
        }catch (IOException e){
            System.err.println("Can't create userData: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void loadUserData(){
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(this.userData));
            List<StoreInfo> data = ((List<StoreInfo>) ois.readObject());
            if (data != null) {
                this.selectedStores = data;
            }
        }catch (IOException e){
            System.err.println("User data does not exist or is corrupted: " + e.getMessage());
            System.out.println("If this is the first time running this program ignore this error.");
        } catch (ClassNotFoundException e) {
            System.err.println("User data is corrupted: " + e.getMessage());
        }
    }

    private void viewSelected() {
        for (StoreInfo store : this.selectedStores){
            System.out.print(store.getChain() + " : " + store.getAddress() + " || ");
        }
        System.out.println();
    }



}

