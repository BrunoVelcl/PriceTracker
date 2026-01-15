package com.brunovelcl.pricetracker.DataFetcher.entities;

public class DownloadLink {
    private final String filename;
    private final String link;

    public DownloadLink(String filename, String link) {
        this.filename = filename;
        this.link = link;
    }

    public String getFilename() {
        return filename;
    }

    public String getLink() {
        return link;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        DownloadLink that = (DownloadLink) o;
        return filename.equals(that.filename) && link.equals(that.link);
    }

    @Override
    public int hashCode() {
        int result = filename.hashCode();
        result = 31 * result + link.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return this.filename +  "," + this.link;
    }
}
