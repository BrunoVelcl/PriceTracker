package DataParser.parsers;

import DataFetcher.entities.Chain;
import DataParser.entities.ParsedValues;
import DataParser.entities.ParsedValuesContainer;
import DataParser.entities.Store;
import DataParser.repositories.StoreRepoImpl;
import Text.ANSI;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public abstract class Parser {

    private static final String DATA_DIR = "temp\\";


    private StoreRepoImpl storeRepo;

    public Parser() {
        this.storeRepo = StoreRepoImpl.load();
    }

    protected abstract void parseData(ParsedValuesContainer parsedValues, Path filePath, Store store);

    protected abstract Store parseStore(File file);


    private ParsedValuesContainer runParser(Chain chain) {
        ParsedValuesContainer parsedValues = new ParsedValuesContainer();
        File dir = new File(DATA_DIR, chain.toString());
        File[] files = dir.listFiles();
        if (files == null) return parsedValues;

        for (File file : files) {
            long start = System.nanoTime();
            Store parsedStore = parseStore(file);
            int savedStoreIdx = this.storeRepo.getStores().indexOf(parsedStore);
            if (savedStoreIdx == -1) {
                this.storeRepo.appendStoreToFile(parsedStore);
                this.storeRepo = StoreRepoImpl.load();
                parseData(parsedValues, Path.of(file.toURI()), parsedStore);
            } else {
                Store currentStore = this.storeRepo.getStores().get(savedStoreIdx);
                parseData(parsedValues, Path.of(file.toURI()), currentStore);
            }
            long end = System.nanoTime();
            System.out.println(ANSI.Color.basicString("Parsing time: " + (end - start), ANSI.BasicColor.CYAN));
            System.out.println(ANSI.Color.basicString("parsedValues size= " + parsedValues.getBrandedProducts().size(), ANSI.BasicColor.CYAN));
            System.out.println(ANSI.Color.basicString("File parsed: " + file.getName(), ANSI.BasicColor.MAGENTA));
        }
        System.out.println("PARSING COMPLETE");
        return parsedValues;
    }

    public static ParsedValuesContainer run(Chain chain) {
        Parser parser = null;
        switch (chain) {
            case LIDL -> parser = new LidlParser();
            case KAUFLAND -> parser = new KauflandParser();
            case PLODINE, SPAR -> parser = new PlodineSparParser();
            case STUDENAC -> {
                System.err.println("studenacParser not implemented");
                return null;
            }
        }
        return (parser != null) ? parser.runParser(chain) : null;
    }


}
