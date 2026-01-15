package com.brunovelcl.pricetracker.ProductManager.SaveFIleManager;

import com.brunovelcl.pricetracker.DataFetcher.entities.Chain;
import com.brunovelcl.pricetracker.DataParser.entities.ParsedValues;
import com.brunovelcl.pricetracker.DataParser.entities.ParsedValuesBuilder;
import com.brunovelcl.pricetracker.DataParser.entities.ParsedValuesContainer;
import com.brunovelcl.pricetracker.DataParser.entities.Store;
import com.brunovelcl.pricetracker.DataParser.repositories.StoreRepoImpl;
import com.brunovelcl.pricetracker.ProductManager.Entities.Builders.ProductBuilder;
import com.brunovelcl.pricetracker.ProductManager.Entities.PricePoint;
import com.brunovelcl.pricetracker.ProductManager.Entities.Product;
import com.brunovelcl.pricetracker.Text.Text;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.brunovelcl.pricetracker.Text.Text.Constants.LEVEL_1_DELIMITER;
import static com.brunovelcl.pricetracker.Text.Text.Constants.LEVEL_2_DELIMITER;
import static com.brunovelcl.pricetracker.Text.Text.Constants.LEVEL_3_DELIMITER;

public class SaveFileManager {

    private static final String BACKUP_SAVE_FILE_NAME = "backupSaveFile";
    private static final Path BACKUP_SAVE_FILE = Paths.get(Text.Directories.DATA, BACKUP_SAVE_FILE_NAME);

    public static void saveBackup(ConcurrentHashMap<Long, Product> productHashMap, StringBuilder sb){
        try(BufferedWriter bw = Files.newBufferedWriter(BACKUP_SAVE_FILE, StandardOpenOption.TRUNCATE_EXISTING)) {
            productHashMap.forEach((key, value) -> {
                sb.setLength(0);
                sb.append(value.getBarcode()).append(LEVEL_1_DELIMITER)
                        .append(value.getName()).append(LEVEL_1_DELIMITER)
                        .append(value.getBrand()).append(LEVEL_1_DELIMITER)
                        .append(value.getUnit_quantity()).append(LEVEL_1_DELIMITER)
                        .append(value.getUnit()).append(LEVEL_1_DELIMITER);

                List<PricePoint> prices = value.getPrices();
                for(PricePoint pp : prices){
                   sb.append(LEVEL_2_DELIMITER).append(pp.getPrice());
                   List<Store> stores = pp.getStores();
                   for(Store store: stores){
                       sb.append(LEVEL_3_DELIMITER).append(store.getId());
                   }
                }
                try {
                    bw.write(sb.toString());
                    bw.newLine();
                } catch (IOException e) {
                    System.err.printf(Text.ErrorMessages.IO_EXCEPTION_BUFF_WRITER, sb);
                }
            });

        }catch (Exception e){
            System.err.printf(Text.ErrorMessages.SAVE_FILE_WRITE_ERROR, BACKUP_SAVE_FILE_NAME);
        }
    }

    public static ConcurrentHashMap<Long, Product> loadBackup(){
        ConcurrentHashMap<Long, Product> productsMap = new ConcurrentHashMap<>();
        try(BufferedReader br = Files.newBufferedReader(BACKUP_SAVE_FILE)) {
            StoreRepoImpl stores = StoreRepoImpl.load();
            String line = br.readLine();
            ProductBuilder pb = new ProductBuilder();
            while (line != null){
                String[] levelOneSeparation =  line.split(LEVEL_1_DELIMITER);
                pb.barcode(Long.parseLong(levelOneSeparation[0]));
                pb.name(levelOneSeparation[1]);
                pb.brand(levelOneSeparation[2]);
                pb.unitQuantity(levelOneSeparation[3]);
                pb.unit(levelOneSeparation[4]);

                String[] levelTwoSeparation = levelOneSeparation[5].split(LEVEL_2_DELIMITER);
                for(int i = 1; i < levelTwoSeparation.length; i++){
                    String[] levelThreeSeparation = levelTwoSeparation[i].split(LEVEL_3_DELIMITER);
                    Double pricePointValue = Double.parseDouble(levelThreeSeparation[0]);
                    PricePoint pricePoint = new PricePoint(pricePointValue);
                    for (int j = 1; j < levelThreeSeparation.length; j++) {
                        pricePoint.getStores().add(stores.getStores().get(Short.parseShort(levelThreeSeparation[j])));
                    }
                    pb.addPricePoint(pricePoint);
                }

                productsMap.put(pb.getBarcode(), pb.consume());

                line = br.readLine();
            }
        } catch (IOException e) {
            System.err.printf(Text.ErrorMessages.SAVE_FILE_READ_ERROR, BACKUP_SAVE_FILE_NAME);
        }
        return productsMap;
    }

    public static void saveParsedValues(ParsedValuesContainer parsedValuesContainer, Chain chain) {
        Path path = Paths.get(Text.Directories.TEMP, chain.toString(), chain.toString());
        try(BufferedWriter bw = Files.newBufferedWriter(path, StandardOpenOption.TRUNCATE_EXISTING)){
            parsedValuesContainer.getBrandedProducts().forEach((k,v) -> {
                try {
                    bw.write(k.toLine());
                    bw.newLine();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }catch (Exception e){
            System.err.printf(Text.ErrorMessages.PARSED_VALUES_WRITE_FAIL, chain);
        }
    }

    public static Set<ParsedValues> loadParsedValues(){
        StoreRepoImpl storeRepo = StoreRepoImpl.load();
        Set<ParsedValues> parsedValuesList = new HashSet<>();
        ParsedValuesBuilder pvBuilder = new ParsedValuesBuilder();
        Arrays.stream(Chain.values()).forEach( chain -> {
            Path path = Paths.get(Text.Directories.TEMP, chain.name(), chain.name());
            try(BufferedReader br = Files.newBufferedReader(path)){
                while (true){
                String row = br.readLine();
                if(row == null) break;
                String[] line = row.split(LEVEL_1_DELIMITER);
                pvBuilder.barcode(Long.parseLong(line[0]));
                pvBuilder.productName(line[1]);
                pvBuilder.brand(line[2]);
                pvBuilder.unit_quantity(line[3]);
                pvBuilder.unit(line[4]);
                pvBuilder.store(storeRepo.getStores().get(Short.parseShort(line[5])));
                pvBuilder.price(Double.parseDouble(line[6]));
                parsedValuesList.add(pvBuilder.consume());
                }
            } catch (Exception e) {
                System.err.printf(Text.ErrorMessages.SAVE_FILE_READ_ERROR, path);
            }
        });
        return parsedValuesList;
    }
}
