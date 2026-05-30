package com.brunovelcl.pricetracker.schedulers.entities;

import com.brunovelcl.pricetracker.database.entities.Chain;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ChainInfo {
    private Chain chain;
    private boolean updatedToday;

    public ChainInfo(Chain chain) {
        this.chain = chain;
        this.updatedToday = false;
    }

    public static List<ChainInfo> mapFromChainList(List<Chain> chains){
        final List<ChainInfo> chainInfoList = new ArrayList<>();
        chains.forEach( chain -> {chainInfoList.add(new ChainInfo(chain));});
        return chainInfoList;
    }
}
