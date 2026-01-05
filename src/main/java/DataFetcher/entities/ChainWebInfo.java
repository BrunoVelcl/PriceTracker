package DataFetcher.entities;

public class ChainWebInfo {
    Chain chain;
    String baseUrl;
    String priceDataUrl;

    public ChainWebInfo(Chain chain, String baseUrl, String priceDataUrl) {
        this.chain = chain;
        this.baseUrl = baseUrl;
        this.priceDataUrl = priceDataUrl;
    }

    public Chain getChain() {
        return chain;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getPriceDataUrl() {
        return priceDataUrl;
    }
}
