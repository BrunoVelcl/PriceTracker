package com.brunovelcl.pricetracker.DataParser.parsers;

import com.brunovelcl.pricetracker.DataParser.entities.ParsedValuesContainer;
import com.brunovelcl.pricetracker.Text.Text;
import com.brunovelcl.pricetracker.database.entities.Chain;
import com.brunovelcl.pricetracker.database.entities.Stores;
import com.brunovelcl.pricetracker.database.services.interfaces.StoresService;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;


import java.io.File;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Optional;

public abstract class Parser {

    private static final String DATA_DIR = Text.Directories.TEMP;

    protected StoresService storesService;

    protected abstract void parseData(ParsedValuesContainer parsedValues, Path filePath, Stores store);

    protected abstract String parseStoreName(File file);


    public ParsedValuesContainer run(Chain chain) {
        ParsedValuesContainer parsedValues = new ParsedValuesContainer();
        File dir = new File(DATA_DIR, chain.getName());
        File[] files = dir.listFiles();
        if (files == null) return parsedValues;

        for (File file : files) {
            if(file.getName().equals(chain.getName()) || file.getName().endsWith(".txt")) continue;
            String parsedStoreName = parseStoreName(file);

            Optional<Stores> existingStore = this.storesService.findByAddress(parsedStoreName);
            Stores currentStore = existingStore.orElseGet(() -> this.storesService.save(new Stores(parsedStoreName, chain, Instant.now())));
            parseData(parsedValues, Path.of(file.toURI()), currentStore);

            if(!file.delete()){
                System.err.printf(Text.ErrorMessages.FAILED_TO_DELETE_FILE, file);
            }
        }
        System.out.println("PARSING COMPLETE");
        return parsedValues;
    }

}
