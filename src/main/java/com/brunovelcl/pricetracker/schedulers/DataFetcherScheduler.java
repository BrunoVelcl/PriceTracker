package com.brunovelcl.pricetracker.schedulers;

import com.brunovelcl.pricetracker.DataFetcher.DataFetcher;
import com.brunovelcl.pricetracker.DataFetcher.entities.Chain;
import com.brunovelcl.pricetracker.ProductManager.ProductManager;
import com.brunovelcl.pricetracker.database.services.interfaces.ChainsService;
import com.brunovelcl.pricetracker.schedulers.entities.ChainInfo;
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
    private final StringBuilder stringBuilder;
    private final ChainsService chainsService;
    private final List<ChainInfo> chainInfoList;
    private int attempt;

    public DataFetcherScheduler(DataFetcher dataFetcher, ProductManager pm, ChainsService chainsService) {
        this.dataFetcher = dataFetcher;
        this.pm = pm;
        this.stringBuilder = new StringBuilder();
        this.chainsService = chainsService;
        this.chainInfoList = ChainInfo.mapFromChainList(chainsService.getAllChains());
        this.attempt = 0;
    }

    @Scheduled(fixedDelay = 5,timeUnit = TimeUnit.MINUTES)
    public void update(){
        if(isScrapingWindow(LocalTime.now())){
            if(this.attempt != 0){
                this.attempt = 0;
            }
            return;
        }

        if(this.attempt == 0) this.reset();

        //TODO: this is a temp testing version
        if(dataFetcher.fetch(this.chainInfoList)){
            pm.loadFromParsedValues();
            pm.save(this.stringBuilder);
        }

    }

    private boolean isScrapingWindow(LocalTime now){
        return now.isAfter(SCRAPING_WINDOW_START) && now.isBefore(SCRAPING_WINDOW_END);
    }

    private void reset() {
        this.chainInfoList.forEach(chainInfo -> {chainInfo.setUpdatedToday(false);});
        this.attempt = 1;
    }

}
