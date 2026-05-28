package com.brunovelcl.pricetracker.database.services.interfaces;

import com.brunovelcl.pricetracker.DataFetcher.entities.DownloadLink;
import com.brunovelcl.pricetracker.database.entities.ScrapedLink;

import java.util.List;

public interface ScrapedLinksService {
    void addNew(ScrapedLink scrapedLink);
    List<ScrapedLink> findByProcessedFalse();
    boolean processedSuccessfully(ScrapedLink scrapedLink);
}
