package com.brunovelcl.pricetracker.schedulers;

import com.brunovelcl.pricetracker.DataFetcher.DataFetcher;
import com.brunovelcl.pricetracker.database.services.interfaces.ChainsService;
import com.brunovelcl.pricetracker.schedulers.entities.ChainInfo;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class DataFetcherScheduler {

    private final DataFetcher dataFetcher;
    private final List<ChainInfo> chainInfoList;

    public DataFetcherScheduler(DataFetcher dataFetcher, ChainsService chainsService) {
        this.dataFetcher = dataFetcher;
        this.chainInfoList = ChainInfo.mapFromChainList(chainsService.getAllChains());
    }

    @Scheduled(cron = "* */5 7-15 * * MON-FRI")
    public void update(){

        if(dataFetcher.fetch(this.chainInfoList)){
            System.out.println("DATA FETCHER UPDATED DATA"); //Temp sout
        }
    }

}
