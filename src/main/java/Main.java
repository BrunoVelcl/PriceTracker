import FileFetcher.Downloader;
import FileFetcher.StoreNameLinks;
import Parser.BarcodeMap;
import Parser.ParserLidl;
import Parser.PricePoint;
import Parser.StoreInfo;

import java.sql.SQLException;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        //Downloader downloader = new Downloader();
        //downloader.download();

        BarcodeMap engine = new BarcodeMap();
        ParserLidl lidl = new ParserLidl();
        try {
            lidl.run(engine);
        }catch (SQLException e){
            System.err.println("SQL FAILURE IN CODE");
        }
        long x = 3830000625777L;
        List<PricePoint> result = engine.getPricesForBarcode(x);

        for(PricePoint price : result){
            System.out.println(price.getProductInfo().getProductName());
            System.out.println(price.getProductInfo().getBrand());
            System.out.println(price.getPrice());
            System.out.print("STORES: " + price.getStores());

        }

    }
}

//TODO: add timed tasks, add timestamp to FileFetcher.StoreNameLinks
//TODO: add automatic cleanup
//TODO: check Kaufland links lol
//TODO: make a separate download check list