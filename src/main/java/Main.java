import Parser.ParserLidl;

public class Main {
    public static void main(String[] args) {

        //Downloader downloader = new Downloader();
        //downloader.download();
        ParserLidl lidl = new ParserLidl();
        lidl.parse();
    }
}

//TODO: add timed tasks, add timestamp to StoreNameLinks
//TODO: add automatic cleanup
//TODO: check Kaufland links lol
//TODO: make a separate download check list