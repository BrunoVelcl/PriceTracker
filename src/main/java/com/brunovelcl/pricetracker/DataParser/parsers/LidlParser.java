package com.brunovelcl.pricetracker.DataParser.parsers;

import com.brunovelcl.pricetracker.DataFetcher.entities.Chain;
import com.brunovelcl.pricetracker.DataParser.entities.ParsedValues;
import com.brunovelcl.pricetracker.DataParser.entities.ParsedValuesBuilder;
import com.brunovelcl.pricetracker.DataParser.entities.ParsedValuesContainer;
import com.brunovelcl.pricetracker.DataParser.entities.Store;
import com.brunovelcl.pricetracker.Text.Text;
import com.brunovelcl.pricetracker.Text.CroCharMap;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class LidlParser extends Parser{

    StringBuilder sb;

    public LidlParser() {
        this.sb = new StringBuilder();
    }

    @Override
    protected Store parseStore(File file, Chain chain) {
        sb.setLength(0);
        sb.append(file.getName());

        int addressStart = 17;
        sb.delete(0, addressStart - 1);
        int lineCnt = 0;
        for(int i = sb.length()-1; i > 0; i--){
            if (sb.charAt(i) == '_'){
                lineCnt++;
                sb.replace(i, i + 1, " ");
            }
            if(lineCnt == 2){
                sb.delete(i,sb.length());
            }
        }
        return (this.sb.isEmpty()) ? null : new Store(sb.toString(), chain);
    }

    @Override
    protected void parseData(ParsedValuesContainer parsedValues, Path filePath, Store store) {
        String data = null;
        try {
            data = Files.readString(filePath, StandardCharsets.ISO_8859_1);
        } catch (IOException e) {
            System.out.println(Text.ErrorMessages.STORE_FILE_OPEN_FAIL);
        }
        if(data == null){
            System.err.println(Text.ErrorMessages.DATA_FOR_PARSING_NOT_FOUND);
            return;
        }
        sb.setLength(0);
        CroCharMap croMap = new CroCharMap();
        boolean quotes = false;
        int returnPointForCursor = 0;   //used for keeping track of where cursor encountered -> "
        char newLine = 0x0a;
        char delimiter = 0x2c;
        int start = data.indexOf(0x0a) + 1;
        int c = 0;

        ParsedValuesBuilder builder = new ParsedValuesBuilder();

        for(int i = start; i < data.length(); i ++){

            char cursor = data.charAt(i);
            if(cursor == '"'){
                returnPointForCursor = i;
                quotes = !quotes;
            }
            if(quotes){
                if(cursor == newLine){
                    quotes = false;
                    i = returnPointForCursor;
                }
                continue;
            }

            if(cursor == delimiter) {

                sb.setLength(0);
                switch (c) {
                    case 0 -> builder.productName(croMap.replaceString(sb.append(data, start, i)));
                    case 2 -> builder.unit_quantity(data.substring(start, i));
                    case 3 -> builder.unit(data.substring(start, i));
                    case 4 -> builder.brand(croMap.replaceString(sb.append(data, start, i)));
                    case 5 -> {
                        String test = data.substring(start, i );
                        builder.price((test.isEmpty()) ? null : Double.parseDouble(test));
                    }
                    case 9 -> {
                        String test = data.substring(start, i );
                        builder.barcode((test.isEmpty()) ? null : Long.parseLong(test));
                    }
                }
                c++;
                start = i+1;
                continue;
            }
            if(cursor == newLine | i == data.length()-1){ // Second condition includes EOF line
                c = 0;
                start = i+1;
                builder.store(store);
                ParsedValues newParsedValue = builder.consume();
                if(newParsedValue != null) {
                    parsedValues.add(newParsedValue);
                }
            }
        }
    }
}
