package com.brunovelcl.pricetracker.DataFetcher;

import com.brunovelcl.pricetracker.DataFetcher.entities.Chain;

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
    private final PythonDownloader pythonDownloader;

    public DataFetcher(ScrapedLinksService sls, PythonDownloader pythonDownloader) {
        this.sls = sls;
        this.pythonDownloader = pythonDownloader;
    }

    public boolean fetch(List<ChainInfo> chainInfoList) {

        AtomicBoolean updateHappened = new AtomicBoolean(false);

        try (ExecutorService executor = Executors.newFixedThreadPool(Chain.values().length)) {

            chainInfoList.forEach(chain -> {

                Integer chainId = chain.getChain().getId();
                String chainName = chain.getChain().getName();

                if (chain.isUpdatedToday()) return;
                executor.submit(() -> {
                    StringBuilder sb = new StringBuilder();
                    LinkScraper linkScraper = new LinkScraper(sb);
                    List<ScrapedLink> scrapedLinks = linkScraper.getLinks(chain);
                    if (scrapedLinks == null) {
                        System.out.printf(Text.Messages.SCRAPING_FAILED, chainName);
                        return;
                    }

                    scrapedLinks.forEach(sls::addNew);
                    System.out.printf(Text.Messages.FINISHED_SCRAPING, chainName);
                    List<ScrapedLink> newLinks = sls.findByProcessedFalse(chainId);
                    if (newLinks.isEmpty()) {
                        System.out.printf(Text.Messages.NO_NEW_DATA, chainName);
                        return;
                    }

                    List<Long> failedDownloadIdList = pythonDownloader.download(newLinks);
                    sls.processedSuccessfully(newLinks, failedDownloadIdList);


//                    ParsedValuesContainer parsedValues = Parser.run(chain.getName());
//                    if (parsedValues == null || parsedValues.isEmpty()) {
//                        System.err.printf(Text.ErrorMessages.PARSING_RETURNED_NOTHING, chain);
//                        return;
//                    }


//                    SaveFileManager.saveParsedValues(parsedValues, chain);
                    chain.setUpdatedToday(true);
                    updateHappened.set(true);
                    System.out.printf(Text.Messages.COMPLETED, chainName);
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

}
