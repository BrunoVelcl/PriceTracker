package com.brunovelcl.pricetracker.DataFetcher.entities;

public enum Chain {
    LIDL((byte)0),
    KAUFLAND((byte)1),
    SPAR((byte)2),
    STUDENAC((byte)3),
    PLODINE((byte)4);

    private final byte index;

    Chain(byte index) {
        this.index = index;
    }

    public byte getIndex() {
        return index;
    }

    public static Chain fromIndex(byte index){
        for( Chain chain: Chain.values()){
            if(chain.getIndex() == index){
                return chain;
            }
        }
        return null;
    }
}
