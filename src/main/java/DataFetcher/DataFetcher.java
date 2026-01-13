package DataFetcher;

import DataFetcher.entities.Chain;
import DataFetcher.entities.DownloadLink;
import DataFetcher.repositories.implementations.ChainWebInfoRepoImpl;
import DataFetcher.repositories.implementations.DownloadLinkRepoImpl;
import DataParser.entities.ParsedValuesContainer;
import DataParser.parsers.Parser;
import DataParser.entities.ParsedValues;
import ProductManager.Entities.UpdateCounter;
import ProductManager.ProductManager;
import Text.Text;

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
import java.util.concurrent.TimeUnit;

public class DataFetcher {

    private static final String SCRAPED_FILES_DIR = "logs/";
    private static final String TEMP_DOWNLOADED_FILES = "temp/";

    public void fetch() {
        ProductManager pm = new ProductManager();

        try (ExecutorService executor = Executors.newFixedThreadPool(Chain.values().length)) {

            //Arrays.stream(Chain.values()).forEach(chain -> {

                Chain chain = Chain.LIDL;

                executor.submit(() -> {
                    StringBuilder sb = new StringBuilder();
//                    LinkScraper linkScraper = new LinkScraper(sb);
//                    List<DownloadLink> scrapedLinks = linkScraper.getLinks(ChainWebInfoRepoImpl.chainURLs[chain.getIndex()]);
//                    if(scrapedLinks == null){
//                        System.out.printf(Text.Messages.SCRAPING_FAILED, chain);
//                        return;
//                    }
//
//                    Path path = Path.of(SCRAPED_FILES_DIR + chain.toString());
//                    DownloadLinkRepoImpl linkRepo = new DownloadLinkRepoImpl(path);
//                    linkRepo.loadFromFile();
//
//                    List<DownloadLink> newLinks = linkRepo.linkSorter(scrapedLinks);
//                    if(newLinks.isEmpty()){
//                        System.out.printf(Text.Messages.NO_NEW_DATA, chain);
//                        return;
//                    }
//                    System.out.printf(Text.Messages.FINISHED_SCRAPING, chain);
//                    List<DownloadLink> downloadedLinks = downloadFiles(newLinks, chain);
//                    linkRepo.appendToFile(downloadedLinks);

                    ParsedValuesContainer parsedValues = Parser.run(chain);
                    if (parsedValues == null || parsedValues.isEmpty()) {
                        System.err.printf(Text.ErrorMessagess.PARSING_RETURNED_NOTHING, chain);
                        return;
                    }
                    pm.update(parsedValues);
                    System.out.printf(Text.Messages.COMPLETED, chain);
        //        });
            });
            executor.shutdown();
//            try {
//                executor.awaitTermination(Long.MAX_VALUE, TimeUnit.HOURS);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
        }

        pm.save(new StringBuilder());

    }

    public static List<DownloadLink> downloadFiles(List<DownloadLink> newLinks, Chain chain) {
        List<DownloadLink> downloadedLinks = new ArrayList<>();
        HttpClient client = HttpClient.newHttpClient();
        newLinks.forEach(downloadLink -> {
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(downloadLink.getLink())).build();
            try {
                client.send(request, HttpResponse.BodyHandlers.ofFile(Paths.get(TEMP_DOWNLOADED_FILES, chain.toString(), downloadLink.getFilename())));
            } catch (Exception e) {
                System.err.printf(Text.ErrorMessagess.DOWNLOAD_FAILED, downloadLink.getLink());
                System.err.println(e.getMessage());
            }
            downloadedLinks.add(downloadLink);
            if (downloadLink.getFilename().endsWith(Text.Constants.ZIP_EXTENSION)) {
                Path path = Paths.get(TEMP_DOWNLOADED_FILES, chain.toString());
                Unzipper.unzipAllInDir(path);
            }
        });
        client.close();
        System.out.printf(Text.Messages.DOWNLOAD_COMPLETE, chain.toString());

        return downloadedLinks;
    }

}
