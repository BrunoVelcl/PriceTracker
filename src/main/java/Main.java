import Database.Updatedb;
import Engine.BarcodeMap;
import Engine.Engine;
import FileFetcher.Downloader;
import Parser.ParsedValues;
import Parser.ParserLidl;
import Engine.PricePoint;
import Parser.StoreInfo;

import java.sql.SQLException;
import java.util.List;

public class Main {
    public static void main(String[] args) {

//        Downloader downloader = new Downloader();
//        if(downloader.download()){
//            Engine engine = new Engine();
//            engine.load();
//            List<ParsedValues> changes = engine.updateData();
//            if(!changes.isEmpty()){
//                Updatedb db = new Updatedb();
//                System.out.println("\u001b[93mWRITING TO DATABASE");
//                try {
//                    db.updateDb(changes);
//                }catch (SQLException e){
//                    System.err.println("SQL EX: " + e.getMessage() + " || " + e.getSQLState());
//                }
//            }
//        }

        Engine engine = new Engine();
//        List<ParsedValues> changes = engine.updateData();
//        Updatedb db = new Updatedb();
//        System.out.println("\u001b[93mWRITING TO DATABASE");
//        try {
//            db.firstTimeChainEntry();
//            db.updateDb(changes);
//        }catch (SQLException e){
//            System.err.println("SQL EX: " + e.getMessage() + " || " + e.getSQLState());
//        }
//        System.out.println("\u001b[92mGREAT SUCCSESS!");
        engine.updateData();
        engine.run();

    }
}

//TODO: add timed tasks, add timestamp to FileFetcher.StoreNameLinks
//TODO: add automatic cleanup for file fetcher
//TODO: check Kaufland links lol
//TODO: make a separate download check list