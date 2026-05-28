package com.brunovelcl.pricetracker.schedulers.entities;

import com.brunovelcl.pricetracker.database.entities.Chain;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ChainInfo {
    private final String name;
    private final String baseLink;
    private final String downloadLink;
    private boolean updatedToday;

    public ChainInfo(String name, String baseLink, String downloadLink) {
        this.name = name;
        this.baseLink = baseLink;
        this.downloadLink = downloadLink;
        this.updatedToday = false;
    }

    public static ChainInfo mapFromChain(Chain chain) {
        return new ChainInfo(chain.getName(), chain.getWebAddress(), chain.getPriceCatalogWebAddress());
    }

    public static List<ChainInfo> mapFromChainList(List<Chain> chains){
        final List<ChainInfo> chainInfoList = new ArrayList<>();
        chains.forEach( chain -> {chainInfoList.add(mapFromChain(chain));});
        return chainInfoList;
    }
}
