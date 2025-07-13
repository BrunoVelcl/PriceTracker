package Database;

import Parser.ParsedValues;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.List;


public class CSV {

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
        final char delimiter = ';';
        final char newline = '\n';
        for (ParsedValues pv : pvList){
            sb.append(pv.getPrice())
                    .append(delimiter)
                    .append(pv.getStoreInfo().getId())
                    .append(delimiter)
                    .append(pv.getBarcode())
                    .append(newline);
        }

        writeToFile(csvFile);
    }

}
