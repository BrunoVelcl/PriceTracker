package Parser;

import FileFetcher.Store;

import java.io.Serializable;

public class StoreInfo implements Serializable {
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
}
