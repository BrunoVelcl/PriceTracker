package com.brunovelcl.pricetracker.DataParser.entities;


import com.brunovelcl.pricetracker.DataFetcher.entities.Chain;

import static com.brunovelcl.pricetracker.Text.Text.Constants.NEWLINE;
import static com.brunovelcl.pricetracker.Text.Text.Constants.COMA_DELIMITER;

public class Store {

    private Short id;
    private final String address;
    private final Chain chain;

    public Store(short id, String address, Chain chain) {
        this.id = id;
        this.address = address;
        this.chain = chain;
    }

    public Store(String address, Chain chain) {
        this.address = address;
        this.chain = chain;
    }

    public Short getId() {
        return id;
    }

    public void setId(Short id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public Chain getChain() {
        return chain;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        Store store = (Store) o;
        return address.equals(store.address) && chain == store.chain;
    }

    @Override
    public int hashCode() {
        int result = address.hashCode();
        result = 31 * result + chain.hashCode();
        return result;
    }

    @Override
    public String toString(){
        return this.id + COMA_DELIMITER + this.address + COMA_DELIMITER + this.chain.getIndex() + NEWLINE;
    }
}
