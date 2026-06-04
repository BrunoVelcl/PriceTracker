package com.brunovelcl.pricetracker.DataParser.parsers;

import com.brunovelcl.pricetracker.DataParser.entities.ParsedValues;
import com.brunovelcl.pricetracker.DataParser.entities.ParsedValuesBuilder;
import com.brunovelcl.pricetracker.DataParser.entities.ParsedValuesContainer;
import com.brunovelcl.pricetracker.Text.Text;
import com.brunovelcl.pricetracker.database.entities.Stores;
import com.brunovelcl.pricetracker.database.services.interfaces.StoresService;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class LidlParser extends Parser {

    private final StringBuilder sb;

    public LidlParser(StoresService storesService) {
        this.storesService = storesService;
        this.sb = new StringBuilder();
    }

    @Override
    protected String parseStoreName(File file) {
        sb.setLength(0);
        sb.append(file.getName());

        int addressStart = 17;
        sb.delete(0, addressStart - 1);
        int lineCnt = 0;
        for (int i = sb.length() - 1; i > 0; i--) {
            if (sb.charAt(i) == '_') {
                lineCnt++;
                sb.replace(i, i + 1, " ");
            }
            if (lineCnt == 2) {
                sb.delete(i, sb.length());
            }
        }
        return (this.sb.isEmpty()) ? null : sb.toString();
    }

    @Override
    protected void parseData(ParsedValuesContainer parsedValues, Path filePath, Stores store) {
        ParsedValuesBuilder builder = new ParsedValuesBuilder();
        try (BufferedReader br = Files.newBufferedReader(filePath, StandardCharsets.ISO_8859_1);
             CSVParser parser = CSVParser.parse(br, CSVFormat.DEFAULT)
        ) {
            for (CSVRecord record : parser) {
                if (record.getRecordNumber() == 1) continue;
                try {
                    builder.productName(record.get(0));
                    builder.unit_quantity(record.get(2));
                    builder.unit(record.get(3));
                    builder.brand(record.get(4));
                    builder.store(store);
                    if (record.get(9).isEmpty()) {
                        builder.price(null);
                    } else{
                        builder.barcode(Long.valueOf(record.get(9)));
                    }
                    //If there is no regular price take te promo price from the next column
                    if(record.get(5).isEmpty()){
                        builder.price(BigDecimal.valueOf(Double.parseDouble(record.get(6))));
                    }else {
                        builder.price(BigDecimal.valueOf(Double.parseDouble(record.get(5))));
                    }
                    ParsedValues newParsedValue = builder.consume();
                    if (newParsedValue != null) {
                        parsedValues.add(newParsedValue);
                    }
                } catch (Exception e) {
                    System.err.printf(Text.ErrorMessages.FAILED_TO_PARSE_LINE, record);
                }
            }
        } catch (Exception e) {
            System.out.println(Text.ErrorMessages.STORE_FILE_OPEN_FAIL);
        }
    }
}
