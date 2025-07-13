import Database.CSV;
import Database.Updatedb;
import Engine.Engine;
import FileFetcher.Downloader;
import Parser.ParsedValues;
import java.io.File;
import java.sql.SQLException;
import java.util.List;

public class Main {

    public static String PRICESCSV = "G:\\Dev\\Prices\\prices.csv";

    public static void main(String[] args) {

        Downloader downloader = new Downloader();
        if(downloader.download()){
            Engine engine = new Engine();
        System.out.println("\u001b[33mLoading...\u001b[37m");
            engine.load();
            List<ParsedValues> changes = engine.updateData();
        System.out.println("Stores number: " + engine.getBarcodeMap().getStores().size());
            if(!changes.isEmpty()){
                System.out.println("\u001b[93mGENERATING CSV FILES\u001b[37m");
                CSV generator = new CSV();
                generator.createCsvForPrices(new File(PRICESCSV), changes);
                System.out.println("\u001b[92mCSV GENERATED\u001b[37m");
                Updatedb updatedb = null;
                try {
                    updatedb = new Updatedb();
                } catch (SQLException e) {
                    System.err.println("Can't create UpdateDatabase: " + e.getMessage());
                }
                System.out.println("\u001b[93mUPDATING DATABASE\u001b[37m");
                assert updatedb != null;
                updatedb.updateAll(engine.getBarcodeMap(), changes, new File(PRICESCSV));

            }
        }


        Engine engine = new Engine();
        engine.load();
        engine.run();

    }
}

//TODO: add timed tasks, add timestamp to FileFetcher.StoreNameLinks
//TODO: add automatic cleanup for file fetcher
//TODO: check Kaufland links lol
