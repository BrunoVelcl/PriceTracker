package Database;

import FileFetcher.Store;
import Parser.ParsedValues;
import Parser.StoreInfo;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class CSV {

    private final char delimiter = ';';
    private final char newline = '\n';
    private final StringBuilder sb = new StringBuilder();

    private void writeToFile(File csvFile){
        try {
            Files.writeString(csvFile.toPath(), sb, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
        }catch (IOException e){
            System.err.println("Problem with csv file: " + e.getMessage());
        }
    }

    public void createCsvForPrices(File csvFile, List<ParsedValues> pvList){
        sb.setLength(0);
        for (ParsedValues pv : pvList){
            sb.append(pv.getPrice())
                    .append(this.delimiter)
                    .append(pv.getStoreInfo().getAddress().hashCode())
                    .append(this.delimiter)
                    .append(pv.getBarcode())
                    .append(this.newline);
        }

        writeToFile(csvFile);
    }

    public void createCsvForStores(File csvFile, HashMap<StoreInfo, Integer> stores){
        sb.setLength(0);

        for(StoreInfo storeInfo : stores.keySet()){
            sb.append(storeInfo.hashCode())
                    .append(this.delimiter)
                    .append(storeInfo.getAddress())
                    .append(this.delimiter)
                    .append(storeInfo.getChain().ordinal())
                    .append(this.newline);
        }
        writeToFile(csvFile);
    }

    public void createCsvForProducts(File csvFile, List<ParsedValues> pvList){
        sb.setLength(0);
        Set<Long> found = new HashSet<>();
        for(ParsedValues pv : pvList){
            if(!found.contains(pv.getBarcode())){
                found.add(pv.getBarcode());
                sb.append(pv.getBarcode())
                        .append(delimiter)
                        .append(pv.getProductName())
                        .append(delimiter)
                        .append(pv.getBrand())
                        .append(delimiter)
                        .append(pv.getUnit_quantity())
                        .append(delimiter)
                        .append(pv.getUnit())
                        .append(newline);
            }
        }
        writeToFile(csvFile);
    }
}
