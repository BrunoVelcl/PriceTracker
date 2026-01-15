package com.brunovelcl.pricetracker.DataFetcher.repositories.interfaces;

import com.brunovelcl.pricetracker.DataFetcher.entities.DownloadLink;

import java.util.List;

public interface DownloadLinkRepo {
    long loadFromFile();
    boolean appendToFile(List<DownloadLink> newLinks);
    List<DownloadLink> linkSorter(List<DownloadLink> scrapedLinks);
}
