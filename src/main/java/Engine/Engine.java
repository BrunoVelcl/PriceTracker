package Engine;

import FileFetcher.Store;
import Parser.ParserLidl;
import Parser.ProductInfo;
import Parser.StoreInfo;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Engine {
    private BarcodeMap barcodeMap = new BarcodeMap();
    private final File defaultFile = new File("save.bin");

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
        long barcode;
        Engine engine = new Engine();
        engine.load();
        while (true){
            System.out.print("Enter barcode (1-quit): ");
            barcode = scanner.nextLong();
            if (barcode ==  1){
                break;
            }else {
                engine.printPrices(barcode);
            }
        }
        engine.save();
        System.out.println("Data saved!");
    }

    //Only use when setting up for the first time
    public void firstTime(){
        ParserLidl parsLidl = new ParserLidl();
        parsLidl.run(this.barcodeMap);
        save();
    }
}

