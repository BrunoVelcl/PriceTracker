package DataFetcher;

import DataFetcher.entities.Chain;
import DataFetcher.entities.ChainWebInfo;
import DataFetcher.entities.DownloadLink;
import DataFetcher.repositories.implementations.ChainWebInfoRepoImpl;
import DataFetcher.repositories.implementations.DownloadLinkRepoImpl;
import Text.Text;

import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DataFetcher {

    private static final String SCRAPED_FILES_DIR = "logs/";
    private static final String TEMP_DOWNLOADED_FILES = "temp/";

    public void fetch() {

//        StringBuilder sb = new StringBuilder();
//        LinkScraper linkScraper = new LinkScraper(sb);
//        var scrapedLinks = linkScraper.getLinksLidl(ChainWebInfoRepoImpl.chainURLs[Chain.LIDL.getIndex()]);
//        scrapedLinks.forEach( downloadLink -> System.out.println("Got link: " + downloadLink.getFilename()));

        try (ExecutorService executor = Executors.newFixedThreadPool(Chain.values().length)) {

            Arrays.stream(Chain.values()).forEach(chain -> {

                executor.submit(() -> {
                    StringBuilder sb = new StringBuilder();
                    LinkScraper linkScraper = new LinkScraper(sb);
                    List<DownloadLink> scrapedLinks = linkScraper.getLinks(ChainWebInfoRepoImpl.chainURLs[chain.getIndex()]);

                    Path path = Path.of(SCRAPED_FILES_DIR + chain.toString());
                    DownloadLinkRepoImpl repo = new DownloadLinkRepoImpl(path);
                    repo.loadFromFile();

                    List<DownloadLink> newLinks = repo.linkSorter(scrapedLinks);
                    List<DownloadLink> downloadedLinks = downloadFiles(newLinks, chain);
                    repo.appendToFile(downloadedLinks);

                });
            });
        }
    }

    public static List<DownloadLink> downloadFiles(List<DownloadLink> newLinks, Chain chain){
        List<DownloadLink> downloadedLinks = new ArrayList<>();
        HttpClient client = HttpClient.newHttpClient();
            newLinks.forEach(downloadLink -> {
                HttpRequest request = HttpRequest.newBuilder().uri(URI.create(downloadLink.getLink())).build();
                try {
                   client.send(request, HttpResponse.BodyHandlers.ofFile(Paths.get(TEMP_DOWNLOADED_FILES, chain.toString(), downloadLink.getFilename())));
                }catch (Exception e){
                    System.err.printf(Text.ErrorMessagess.DOWNLOAD_FAILED, downloadLink.getLink());
                    System.err.println(e.getMessage());
                }
                downloadedLinks.add(downloadLink);
                if(downloadLink.getFilename().endsWith(Text.Constants.ZIP_EXTENSION)){
                    Path path  = Paths.get(TEMP_DOWNLOADED_FILES, chain.toString());
                    Unzipper.unzipAllInDir(path);
                }
            });
            client.close();
            System.out.printf(Text.Messages.DOWNLOAD_COMPLETE, chain.toString());

        return downloadedLinks;
    };
}
