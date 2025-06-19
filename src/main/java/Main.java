public class Main {
    public static void main(String[] args) {
        LinkFetcher k = new LinkFetcher();
        k.getLinksKaufland();
        Downloader download = new Downloader();
        download.download(null);

    }
}
