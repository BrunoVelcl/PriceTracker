package DataParser.parsers;

import DataFetcher.entities.Chain;
import DataParser.entities.ParsedValuesContainer;
import DataParser.entities.Store;
import DataParser.repositories.StoreRepoImpl;
import Text.Text;


import java.io.File;
import java.nio.file.Path;

public abstract class Parser {

    private static final String DATA_DIR = "temp\\";

    private StoreRepoImpl storeRepo;

    public Parser() {
        this.storeRepo = StoreRepoImpl.load();
    }

    protected abstract void parseData(ParsedValuesContainer parsedValues, Path filePath, Store store);

    protected abstract Store parseStore(File file, Chain chain);


    private ParsedValuesContainer runParser(Chain chain) {
        ParsedValuesContainer parsedValues = new ParsedValuesContainer();
        File dir = new File(DATA_DIR, chain.toString());
        File[] files = dir.listFiles();
        if (files == null) return parsedValues;

        for (File file : files) {
            if(file.getName().equals(chain.toString())) continue;
            Store parsedStore = parseStore(file, chain);
            int savedStoreIdx = this.storeRepo.getStores().indexOf(parsedStore);
            if (savedStoreIdx == -1) {
                this.storeRepo.appendStoreToFile(parsedStore);
                this.storeRepo = StoreRepoImpl.load();
                parseData(parsedValues, Path.of(file.toURI()), parsedStore);
            } else {
                Store currentStore = this.storeRepo.getStores().get(savedStoreIdx);
                parseData(parsedValues, Path.of(file.toURI()), currentStore);
            }
            if(!file.delete()){
                System.err.printf(Text.Text.ErrorMessages.FAILED_TO_DELETE_FILE, file);
            }
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
