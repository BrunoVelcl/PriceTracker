package com.brunovelcl.pricetracker.DataParser.parsers;

import com.brunovelcl.pricetracker.DataParser.entities.ParsedValuesContainer;
import com.brunovelcl.pricetracker.Text.Text;
import com.brunovelcl.pricetracker.database.entities.Chain;
import com.brunovelcl.pricetracker.database.entities.Stores;
import com.brunovelcl.pricetracker.database.services.interfaces.StoresService;
import lombok.NoArgsConstructor;


import java.io.File;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Optional;

@NoArgsConstructor
public abstract class Parser {

    private static final String DATA_DIR = Text.Directories.TEMP;

    private StoresService storesService;

    public Parser(StoresService storesService) {
        this.storesService = storesService;
    }

    protected abstract void parseData(ParsedValuesContainer parsedValues, Path filePath, Stores store);

    protected abstract String parseStoreName(File file);


    private ParsedValuesContainer runParser(Chain chain) {
        ParsedValuesContainer parsedValues = new ParsedValuesContainer();
        File dir = new File(DATA_DIR, chain.getName());
        File[] files = dir.listFiles();
        if (files == null) return parsedValues;

        for (File file : files) {
            if(file.getName().equals(chain.getName())) continue;
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

    public static ParsedValuesContainer run(Chain chain) {
        Parser parser = null;
        switch (chain.getName()) {
            case "LIDL" -> parser = new LidlParser();
            case "KAUFLAND" -> parser = new KauflandParser();
            case "PLODINE, SPAR" -> parser = new PlodineSparParser();
            case "STUDENAC" -> {
                System.err.println("studenacParser not implemented");
                return null;
            }
        }
        return (parser != null) ? parser.runParser(chain) : null;
    }


}
