package com.brunovelcl.pricetracker.DataFetcher.repositories.implementations;

import com.brunovelcl.pricetracker.DataFetcher.entities.DownloadLink;
import com.brunovelcl.pricetracker.DataFetcher.repositories.interfaces.DownloadLinkRepo;
import com.brunovelcl.pricetracker.Text.Text;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DownloadLinkRepoImpl implements DownloadLinkRepo {

    private final Set<DownloadLink> downloadLinks;
    private final Path filepath;

    public DownloadLinkRepoImpl(Path filepath) {
        this.downloadLinks = new HashSet<>();
        this.filepath = filepath;
    }

    //Load from file system
    public long loadFromFile(){
        try(BufferedReader br = Files.newBufferedReader(this.filepath)) {
            String line = br.readLine();
            while (line != null){
                if (line.isBlank()) break;
                String[] data = line.split(Text.Constants.COMA_DELIMITER);
                this.downloadLinks.add(new DownloadLink(data[0], data[1]));
                line = br.readLine();
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        return this.downloadLinks.size();
    }

    //Append to file system
    public boolean appendToFile(List<DownloadLink> newLinks){
        try(BufferedWriter bw = Files.newBufferedWriter(this.filepath, StandardOpenOption.APPEND)){
            for(DownloadLink link : newLinks){
                bw.write(link.toString());
                bw.newLine();
            }
            return true;
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    //Return new links
    public List<DownloadLink> linkSorter(List<DownloadLink> scrapedLinks){
        List<DownloadLink> newLinks = new ArrayList<>();
        for(DownloadLink link : scrapedLinks){
            if(!this.downloadLinks.contains(link)){
                newLinks.add(link);
            }
        }
        return newLinks;
    }

    //TODO: This getter is only for inital setup convinience it dosent need to exist, delete it

    public Set<DownloadLink> getDownloadLinks() {
        return downloadLinks;
    }
}
