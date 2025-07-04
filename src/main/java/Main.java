import Engine.BarcodeMap;
import Engine.Engine;
import Parser.ParserLidl;
import Engine.PricePoint;
import Parser.StoreInfo;

import java.util.List;

public class Main {
    public static void main(String[] args) {

        //Downloader downloader = new Downloader();
        //downloader.download();

        Engine engine = new Engine();
        engine.run();


    }
}

//TODO: add timed tasks, add timestamp to FileFetcher.StoreNameLinks
//TODO: add automatic cleanup
//TODO: check Kaufland links lol
//TODO: make a separate download check list