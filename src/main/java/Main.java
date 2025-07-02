import Engine.BarcodeMap;
import Parser.ParserLidl;
import Engine.PricePoint;
import Parser.StoreInfo;

import java.util.List;

public class Main {
    public static void main(String[] args) {

        //Downloader downloader = new Downloader();
        //downloader.download();

        BarcodeMap engine = new BarcodeMap();
        ParserLidl lidl = new ParserLidl();

        lidl.run(engine);


        long x = 3830000625777L;
        List<PricePoint> result = engine.getPricesForBarcode(x);

        for(PricePoint price : result){
            System.out.println(price.getProductInfo().getProductName());
            System.out.println(price.getProductInfo().getBrand());
            System.out.println(price.getPrice());
            System.out.print("STORES: " );
            for(StoreInfo store : price.getStores()){
                System.out.print(store.getAddress() + "  ");
            }
            System.out.println();
            System.out.println("***************************");

        }

    }
}

//TODO: add timed tasks, add timestamp to FileFetcher.StoreNameLinks
//TODO: add automatic cleanup
//TODO: check Kaufland links lol
//TODO: make a separate download check list