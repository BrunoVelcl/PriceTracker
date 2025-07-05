package Parser;

import Engine.BarcodeMap;
import FileFetcher.Store;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;


public abstract class Parser {

    protected File[] fileList;
    protected final Store chain;
    StoreInfo storeInfo;

    protected Parser(File[] fileList , Store chain) {
        this.fileList = fileList;
        this.chain = chain;
    }

    protected abstract List<ParsedValues> parseData(String data, StringBuilder sb);
    protected abstract String parseAddress(File file, StringBuilder stringBuilder);

    public void run(BarcodeMap mappedProducts) {
        //Find all files in dir
        for(File file : this.fileList){
            // Address extraction
            String storeAddress = parseAddress(file ,new StringBuilder());
            if(storeAddress == null){
                System.err.println("Couldn't parse address for file: " + file.getAbsolutePath());
                return;
            }
            storeInfo = new StoreInfo(storeAddress, chain);
            mappedProducts.storeUpdate(storeInfo);
            updateLoop(file, mappedProducts);
        }
    }

    protected void updateLoop(File file, BarcodeMap barcodeMap){
        StringBuilder sb = new StringBuilder();
        String data;
        try {
            data = Files.readString(file.toPath(), StandardCharsets.ISO_8859_1);
        }catch (IOException e){
            System.err.println("Couldn't find file to parse: " + file.getAbsolutePath());
            return;
        }

        List<ParsedValues> parsedData = parseData(data, sb);

        for (ParsedValues pv: parsedData){
            barcodeMap.update(pv);
        }

        //if(!file.delete()){
        //    System.err.println("Couldn't delete file: " + file.getAbsolutePath());
        //}
    }
}


