package com.brunovelcl.pricetracker.DataParser.parsers;

import com.brunovelcl.pricetracker.DataFetcher.entities.Chain;
import com.brunovelcl.pricetracker.DataParser.entities.ParsedValues;
import com.brunovelcl.pricetracker.DataParser.entities.ParsedValuesBuilder;
import com.brunovelcl.pricetracker.DataParser.entities.ParsedValuesContainer;
import com.brunovelcl.pricetracker.DataParser.entities.Store;
import com.brunovelcl.pricetracker.Text.Text;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class KauflandParser extends Parser{


    private final DecimalFormat decimalFormat;
    private final StringBuilder sb;

    public KauflandParser() {

        this.sb = new StringBuilder();
        DecimalFormatSymbols dfs = new DecimalFormatSymbols();
        dfs.setDecimalSeparator(',');
        DecimalFormat decimalFormat = new DecimalFormat();
        decimalFormat.setDecimalFormatSymbols(dfs);
        this.decimalFormat = decimalFormat;
    }

    private static void cleanDoubleSpace(StringBuilder sb){
        for (int i = 0; i < sb.length()-1; i++){
            if(sb.charAt(i) == ' ' & sb.charAt(i+1) == ' '){
                sb.deleteCharAt(i);
            }
        }
    }

    private static void comaToDot(StringBuilder sb){
        for(int i = 0; i < sb.length()-1; i++){
            if(sb.charAt(i) == ','){
                sb.setCharAt(i,'.');
            }
        }
    }

    @Override
    protected Store parseStore(File file, Chain chain) {
        this.sb.setLength(0);
        this.sb.append(file.getName());

        int addressStart = 13;
        this.sb.delete(0, addressStart - 1);
        boolean foundChar = false;
        for(int i = this.sb.length()-5; i > 0; i--){
            if (this.sb.charAt(i) == '_'){
                this.sb.replace(i, i + 1, " ");
            }
            if(!foundChar & Character.isLetter(sb.charAt(i))){
                this.sb.delete(i+1,sb.length());
                foundChar = true;
            }
            cleanDoubleSpace(this.sb);
        }
        return (this.sb.isEmpty()) ? null : new Store(sb.toString(), chain);

    }

    @Override
    protected void parseData(ParsedValuesContainer parsedValues, Path filePath, Store store) {
        CSVFormat kauflandFormat = CSVFormat.DEFAULT
                .builder()
                .setDelimiter((char)0x09)
                .setRecordSeparator((char)0xa)
                .get();
        ParsedValuesBuilder builder = new ParsedValuesBuilder();
        try (BufferedReader br = Files.newBufferedReader(filePath);
            CSVParser parser = kauflandFormat.parse(br)
        ){
            for(CSVRecord record : parser){
                if(record.getRecordNumber() == 1) continue;
                try {
                    builder.productName(record.get(0));
                    builder.unit_quantity(record.get(3));
                    builder.unit(record.get(4));
                    builder.brand(record.get(2));
                    builder.store(store);
                    builder.barcode(Long.valueOf(record.get(13)));
                    builder.price(this.decimalFormat.parse(record.get(5).trim()).doubleValue());
                    ParsedValues newParsedValue = builder.consume();
                    if(newParsedValue != null) {
                        parsedValues.add(newParsedValue);
                    }
                } catch (Exception e) {
                    System.err.printf(Text.ErrorMessages.FAILED_TO_PARSE_LINE, record.toString());
                }
            }
        } catch (IOException e) {
            System.out.println(Text.ErrorMessages.STORE_FILE_OPEN_FAIL);
        }
    }

}
