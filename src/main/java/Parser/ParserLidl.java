package Parser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import FileFetcher.Store;

public class ParserLidl extends Parser{

    public ParserLidl(File storeDir, Store store){
        super(storeDir.listFiles(), store);
    }

    //TODO: change this!
    public ParserLidl(Store store){
        this( new File("G:\\Dev\\Prices\\dumpster\\LIDL"), store);
    }

    @Override
    protected String parseAddress(File file, StringBuilder sb) {
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
        return (sb.isEmpty()) ? null : sb.toString();
    }

    @Override
    protected List<ParsedValues> parseData(String data, StringBuilder sb){
        sb.setLength(0);
        CroCharMap croMap = new CroCharMap();
        boolean quotes = false;
        char newLine = 0x0a;
        char delimiter = 0x2c;
        int start = data.indexOf(0x0a) + 1;
        int c = 0;
        List<ParsedValues> parsedValues = new ArrayList<>();
        ParsedValuesTempContainer tempPvContainer = new ParsedValuesTempContainer();
        tempPvContainer.storeInfo = storeInfo;

        for(int i = start; i < data.length(); i ++){

            char cursor = data.charAt(i);
            if(cursor == '"'){
                quotes = !quotes;
            }
            if(quotes){
                continue;
            }

            if(cursor == delimiter) {

                sb.setLength(0);
                switch (c) {
                    case 0 -> tempPvContainer.productName = croMap.replaceString(sb.append(data, start, i));
                    case 2 -> tempPvContainer.unit_quantity = data.substring(start, i);
                    case 3 -> tempPvContainer.unit  = data.substring(start, i);
                    case 4 -> tempPvContainer.brand = croMap.replaceString(sb.append(data, start, i));
                    case 5 -> {
                        String test = data.substring(start, i );
                        tempPvContainer.price = (test.isEmpty()) ? null : Double.parseDouble(test);
                    }
                    case 9 -> {
                        String test = data.substring(start, i );
                        tempPvContainer.barcode = (test.isEmpty()) ? null : Long.parseLong(test);
                    }
                }
                c++;
                start = i+1;
            }
            if(cursor == newLine | i == data.length()-1){ // Second condition includes EOF line
                c = 0;
                start = i+1;

                ParsedValues pv = tempPvContainer.createParsedValues();
                tempPvContainer.resetContainer();
                if(!pv.isValidInput()){
                    continue;
                }
                parsedValues.add(pv);
            }
        }
        return parsedValues;
    }

}
