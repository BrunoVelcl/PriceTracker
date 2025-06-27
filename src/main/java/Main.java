import FileFetcher.Downloader;
import FileFetcher.StoreNameLinks;
import Parser.ParserLidl;

import java.sql.SQLException;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        //Downloader downloader = new Downloader();
        //downloader.download();
        ParserLidl lidl = new ParserLidl();
        try {
            lidl.run();
        }catch (SQLException e){
            System.err.println("SQL FAILURE IN CODE");
        }

    }
}

//TODO: add timed tasks, add timestamp to FileFetcher.StoreNameLinks
//TODO: add automatic cleanup
//TODO: check Kaufland links lol
//TODO: make a separate download check list