package com.brunovelcl.pricetracker.DataParser.repositories;

import com.brunovelcl.pricetracker.DataFetcher.entities.Chain;
import com.brunovelcl.pricetracker.DataParser.entities.Store;
import com.brunovelcl.pricetracker.Text.Text;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import static com.brunovelcl.pricetracker.Text.Text.Constants.COMA_DELIMITER;

public class StoreRepoImpl {

    private static final Path FILEPATH = Paths.get("data", "store");

    private final List<Store> stores;

    public StoreRepoImpl() {
        this.stores = new ArrayList<>();
    }

    public List<Store> getStores() {
        return stores;
    }

    public void loadFromFile() {
        try(BufferedReader br = Files.newBufferedReader(FILEPATH)){
            String line = br.readLine();
            while (line != null){
                String[] data = line.split(COMA_DELIMITER);
                this.stores.add(new Store(Short.parseShort(data[0]), data[1], Chain.fromIndex(Byte.parseByte(data[2]))));
                line = br.readLine();
            }
        } catch (Exception e) {
            System.err.println(Text.ErrorMessages.STORE_FILE_OPEN_FAIL);
        }
    }

    public void appendStoreToFile(Store store){
        store.setId((short) this.stores.size());
        try {
            Files.writeString(FILEPATH, store.toString(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.err.println(Text.ErrorMessages.STORE_FILE_OPEN_FAIL);
        }
    }

    public static StoreRepoImpl load(){
        StoreRepoImpl newStoreRepo = new StoreRepoImpl();
        newStoreRepo.loadFromFile();
        return newStoreRepo;
    }
}
