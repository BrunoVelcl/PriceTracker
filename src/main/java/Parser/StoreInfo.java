package Parser;

import FileFetcher.Store;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Objects;

public class StoreInfo implements Comparator<StoreInfo>, Serializable {
    private final String address;
    private final Store chain;
    private final int id;

    public StoreInfo(String address, Store chain, int id) {
        this.address = address;
        this.chain = chain;
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public Store getChain() {
        return chain;
    }

    public int getId() { return id; }

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
        return Objects.hash(this.chain, this.address, this.id);
    }

}
