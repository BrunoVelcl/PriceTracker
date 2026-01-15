package com.brunovelcl.pricetracker.DataParser.parsers;

import com.brunovelcl.pricetracker.DataFetcher.entities.Chain;
import com.brunovelcl.pricetracker.DataParser.entities.ParsedValues;
import com.brunovelcl.pricetracker.DataParser.entities.ParsedValuesBuilder;
import com.brunovelcl.pricetracker.DataParser.entities.ParsedValuesContainer;
import com.brunovelcl.pricetracker.DataParser.entities.Store;
import com.brunovelcl.pricetracker.Text.CroCharMap;
import com.brunovelcl.pricetracker.Text.Text;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;


public class KauflandParser extends Parser{

    private final StringBuilder sb;

    public KauflandParser() {
        this.sb = new StringBuilder();
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
        String data = null;
        try {
            data = Files.readString(filePath);
        } catch (IOException e) {
            System.out.println(Text.ErrorMessages.STORE_FILE_OPEN_FAIL);
        }
        if(data == null){
            System.err.println(Text.ErrorMessages.DATA_FOR_PARSING_NOT_FOUND);
            return;
        }
        this.sb.setLength(0);
        CroCharMap croMap = new CroCharMap();
        boolean quotes = false;
        int returnPointForCursor = 0;   //used for keeping track of where cursor encountered -> "
        char newLine = 0x0a;
        char delimiter = 0x09;
        int start = data.indexOf(newLine) + 1;
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
                    case 3 -> builder.unit_quantity(data.substring(start, i));
                    case 4 -> builder.unit(data.substring(start, i));
                    case 2 -> builder.brand(croMap.replaceString(sb.append(data, start, i)));
                    case 5 -> {
                        comaToDot(sb.append(data, start, i));
                        String test = sb.toString();
                        builder.price((test.isEmpty()) ? null : Double.parseDouble(test));
                    }
                    case 13 -> {
                        String test = data.substring(start, i );
                        //test block start
                        try{
                            Long.parseLong(test);
                        } catch (Exception e) {
                            System.err.println("FILE: " + filePath);
                            System.err.println("Product: " + builder.toString());
                            e.printStackTrace();

                        }
                        builder.barcode((test.isEmpty()) ? null : Long.parseLong(test));
                    }
                }
                c++;
                start = i+1;
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
