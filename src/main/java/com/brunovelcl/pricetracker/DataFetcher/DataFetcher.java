package com.brunovelcl.pricetracker.DataFetcher;

import com.brunovelcl.pricetracker.DataFetcher.entities.Chain;
import com.brunovelcl.pricetracker.DataFetcher.entities.DownloadLink;
import com.brunovelcl.pricetracker.DataFetcher.repositories.implementations.ChainWebInfoRepoImpl;
import com.brunovelcl.pricetracker.DataFetcher.repositories.implementations.DownloadLinkRepoImpl;
import com.brunovelcl.pricetracker.DataParser.entities.ParsedValuesContainer;
import com.brunovelcl.pricetracker.DataParser.parsers.Parser;
import com.brunovelcl.pricetracker.ProductManager.SaveFIleManager.SaveFileManager;
import com.brunovelcl.pricetracker.Text.Text;
import com.brunovelcl.pricetracker.database.entities.ScrapedLink;
import com.brunovelcl.pricetracker.database.services.interfaces.ScrapedLinksService;
import com.brunovelcl.pricetracker.schedulers.entities.ChainInfo;
import org.springframework.stereotype.Service;

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
import java.util.concurrent.atomic.AtomicBoolean;

import static com.brunovelcl.pricetracker.Text.Text.Directories.TEMP;

@Service
public class DataFetcher {

    private static final long TIMEOUT_WAIT = 4L;

    private final ScrapedLinksService sls;

    public DataFetcher(ScrapedLinksService sls) {
        this.sls = sls;
    }

    public boolean fetch(List<ChainInfo> chainInfoList) {

        AtomicBoolean updateHappened = new AtomicBoolean(false);

        try (ExecutorService executor = Executors.newFixedThreadPool(Chain.values().length)) {

            chainInfoList.forEach(chain -> {
                if (chain.isUpdatedToday()) return;
                executor.submit(() -> {
                    StringBuilder sb = new StringBuilder();
                    LinkScraper linkScraper = new LinkScraper(sb);
                    List<ScrapedLink> scrapedLinks = linkScraper.getLinks(chain);
                    if (scrapedLinks == null) {
                        System.out.printf(Text.Messages.SCRAPING_FAILED, chain.getName());
                        return;
                    }

//                    Path path = Path.of(Text.Directories.LOGS + chain.getName());
//                    DownloadLinkRepoImpl linkRepo = new DownloadLinkRepoImpl(path);
//                    linkRepo.loadFromFile();
//
//                    List<DownloadLink> newLinks = linkRepo.linkSorter(scrapedLinks);


                    scrapedLinks.forEach(sls::addNew);
                    System.out.printf(Text.Messages.FINISHED_SCRAPING, chain);
                    List<ScrapedLink> newLinks = sls.findByProcessedFalse();
                    if (newLinks.isEmpty()) {
                        System.out.printf(Text.Messages.NO_NEW_DATA, chain);
                        return;
                    }

                    List<ScrapedLink> downloadedLinks = downloadFiles(newLinks, chain);
                    downloadedLinks.forEach(sls::processedSuccessfully);
//                    linkRepo.appendToFile(downloadedLinks);

                    ParsedValuesContainer parsedValues = Parser.run(chain.getName());
                    if (parsedValues == null || parsedValues.isEmpty()) {
                        System.err.printf(Text.ErrorMessages.PARSING_RETURNED_NOTHING, chain);
                        return;
                    }
//                    SaveFileManager.saveParsedValues(parsedValues, chain);
                    chain.setUpdatedToday(true);
                    updateHappened.set(true);
                    System.out.printf(Text.Messages.COMPLETED, chain.getName());
                });
            });
            executor.shutdown();
            try {
                if(!executor.awaitTermination(TIMEOUT_WAIT, TimeUnit.MINUTES)){
                    System.err.println(Text.ErrorMessages.DATA_GATHERING_TIMEOUT_REACHED);
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        return updateHappened.get();
    }

    public static List<ScrapedLink> downloadFiles(List<ScrapedLink> newLinks, ChainInfo chainInfo) {
        List<ScrapedLink> scrapedLinks = new ArrayList<>();
        HttpClient client = HttpClient.newHttpClient();
        newLinks.forEach(scrapedLink -> {
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(scrapedLink.getLink())).build();
            try {
                client.send(request, HttpResponse.BodyHandlers.ofFile(Paths.get(TEMP, chainInfo.getName(), scrapedLink.getFilename())));
            } catch (Exception e) {
                System.err.printf(Text.ErrorMessages.DOWNLOAD_FAILED, scrapedLink.getLink());
                System.err.println(e.getMessage());
            }
            scrapedLinks.add(scrapedLink);
            if (scrapedLink.getFilename().endsWith(Text.Constants.ZIP_EXTENSION)) {
                Path path = Paths.get(TEMP, chainInfo.getName());
                Unzipper.unzipAllInDir(path);
            }
        });
        client.close();
        System.out.printf(Text.Messages.DOWNLOAD_COMPLETE, chainInfo.getName());

        return scrapedLinks;
    }

}
