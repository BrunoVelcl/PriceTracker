import Text.TextColor;
import DataFetcher.DataFetcher;
import FileFetcher.Store;
import Parser.ParsedValues;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {

    public static String PRICESCSV = "G:\\Dev\\Prices\\prices.csv";
    public static String NEW_LINE = System.lineSeparator();

    public static void main(String[] args) {

//        Downloader downloader = new Downloader();
//        if(downloader.download()){
//            Engine engine = new Engine();
//            engine.load();
//            List<ParsedValues> changes = engine.updateData();
//        System.out.println("Stores number: " + engine.getBarcodeMap().getStores().size());
//            if(!changes.isEmpty()){
//                System.out.println(TextColor.yellow("GENERATING CSV"));
//                CSV generator = new CSV();
//                generator.createCsvForPrices(new File(PRICESCSV), changes);
//                System.out.println(TextColor.green("CSV GENERATED"));
//                Updatedb updatedb = null;
//                try {
//                    updatedb = new Updatedb();
//                } catch (SQLException e) {
//                    System.err.println("Can't create UpdateDatabase: " + e.getMessage());
//                }
//                System.out.println(TextColor.yellow("UPDATING DATABASE"));
//                assert updatedb != null;
//                updatedb.updateAll(engine.getBarcodeMap(), changes, new File(PRICESCSV));
//                printChangesByChain(changes);
//            }
//        }


//        Engine.run();


        DataFetcher dataFetcher = new DataFetcher();
        dataFetcher.fetch();
    }

    public static void printChangesByChain(List<ParsedValues> changes){
        Map<Store, Integer> count = new HashMap<>();
        for(ParsedValues pv : changes){
            Store chain = pv.getStoreInfo().getChain();
            if(count.containsKey(chain)){
                count.put(chain, count.get(chain)+1);
            }else {
                count.put(chain, 1);
            }
        }
        for(Store key : count.keySet()){
            System.out.printf("%s: %d%s", TextColor.blue(key.toString()), count.get(key), NEW_LINE);
        }
    }
}

//TODO: add timed tasks, add timestamp to FileFetcher.StoreNameLinks
//TODO: add automatic cleanup for file fetcher
//TODO: check Kaufland links lol
