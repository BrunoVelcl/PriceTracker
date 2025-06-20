public class Main {
    public static void main(String[] args) {
        //LinkFetcher k = new LinkFetcher();
        //k.getLinksPlodine();
        Downloader download = new Downloader();
        download.download(null);

        Unzipper.unzipAllInDir();


    }
}
