package com.brunovelcl.pricetracker.schedulers;

import com.brunovelcl.pricetracker.DataFetcher.DataFetcher;
import com.brunovelcl.pricetracker.DataFetcher.entities.Chain;
import com.brunovelcl.pricetracker.ProductManager.ProductManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class DataFetcherScheduler {

    private static final LocalTime SCRAPING_WINDOW_START = LocalTime.of(7, 0, 0);
    private static final LocalTime SCRAPING_WINDOW_END = LocalTime.of(16, 0, 0);

    private final DataFetcher dataFetcher;
    private final ProductManager pm;
    private final boolean[] scrapedTracker;
    private int attempt;

    public DataFetcherScheduler(DataFetcher dataFetcher, ProductManager pm) {
        this.dataFetcher = dataFetcher;
        this.pm = pm;
        this.scrapedTracker = new boolean[Chain.values().length];
        this.attempt = 0;
    }

    @Scheduled(fixedDelay = 5,timeUnit = TimeUnit.MINUTES)
    public void update(){
        if(!isScrapingWindow(LocalTime.now())){
            if(this.attempt != 0){
                this.attempt = 0;
            }
            return;
        }

        if(this.attempt == 0) this.reset();

        if(dataFetcher.fetch(this.scrapedTracker)){
            pm.loadFromParsedValues();
        }

    }

    private boolean isScrapingWindow(LocalTime now){
        return now.isAfter(SCRAPING_WINDOW_START) && now.isBefore(SCRAPING_WINDOW_END);
    }

    private void reset() {
        Arrays.fill(this.scrapedTracker, false);
        this.attempt = 1;
    }

}
