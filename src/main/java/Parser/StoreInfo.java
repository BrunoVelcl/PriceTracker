package Parser;

import FileFetcher.Store;

import java.io.Serializable;
import java.util.Comparator;

public class StoreInfo implements Comparator<StoreInfo>, Serializable {
    private final String address;
    private final Store chain;

    public StoreInfo(String address, Store chain) {
        this.address = address;
        this.chain = chain;
    }

    public String getAddress() {
        return address;
    }

    public Store getChain() {
        return chain;
    }

    @Override
    public int compare(StoreInfo o1, StoreInfo o2) {
        return o1.getAddress().compareTo(o2.getAddress());

    }
}
