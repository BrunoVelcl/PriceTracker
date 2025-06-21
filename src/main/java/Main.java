public class Main {
    public static void main(String[] args) {

        LinkFetcher k = new LinkFetcher();
        k.getLinksSpar();
        k.writeLinksToDisk();
        Downloader download = new Downloader();
        download.download();

       //Unzipper.unzipAllInDir();


    }
}
