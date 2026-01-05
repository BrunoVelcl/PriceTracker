package DataFetcher;

import DataFetcher.entities.Chain;
import DataFetcher.entities.DownloadLink;
import DataFetcher.repositories.implementations.ChainWebInfoRepoImpl;
import DataFetcher.repositories.implementations.DownloadLinkRepoImpl;
import DataFetcher.repositories.interfaces.DownloadLinkRepo;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

public class DataFetcher {

    private static final String SCRAPED_FILES_DIR = "logs/";

    public void fetch(){
        Arrays.stream(Chain.values()).forEach(chain -> {
            StringBuilder sb = new StringBuilder();
            LinkScraper linkScraper = new LinkScraper(sb);
            List<DownloadLink> scrapedLinks = linkScraper.getLinks(ChainWebInfoRepoImpl.chainURLs[chain.getIndex()]);

            Path path = Path.of(SCRAPED_FILES_DIR + chain.toString());
            DownloadLinkRepo repo = new DownloadLinkRepoImpl(path);
            repo.loadFromFile();
            List<DownloadLink> newLinks = repo.linkSorter(scrapedLinks);
            repo.appendToFile(newLinks);
        });
    }
}
