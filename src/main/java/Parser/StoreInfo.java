package Parser;

import FileFetcher.Store;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Objects;

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



    @Override
    public String toString() {
        return this.chain.toString() + ", " + this.address;
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof StoreInfo other)){
            return false;
        }
       if(this == obj){
           return true;
       }

        return this.chain.equals(other.chain) & this.address.equals(other.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.chain, this.address);
    }
}
