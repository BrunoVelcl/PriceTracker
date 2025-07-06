package Parser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import FileFetcher.Store;

public class ParserKaufland extends Parser{

    public ParserKaufland(File storeDir, Store store){
        super(storeDir.listFiles(), store);
    }

    //TODO: change this!
    public ParserKaufland(Store store){
        this( new File("G:\\Dev\\Prices\\dumpster\\" + store), store);
    }

    @Override
    protected String parseAddress(File file, StringBuilder sb) {
        sb.setLength(0);
        sb.append(file.getName());

        int addressStart = 13;
        sb.delete(0, addressStart - 1);
        boolean foundChar = false;
        for(int i = sb.length()-5; i > 0; i--){
            if (sb.charAt(i) == '_'){
                sb.replace(i, i + 1, " ");
            }
            if(!foundChar & Character.isLetter(sb.charAt(i))){
                sb.delete(i+1,sb.length());
                foundChar = true;
            }
            cleanDoubleSpace(sb);
        }
        return (sb.isEmpty()) ? null : sb.toString();
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
    protected List<ParsedValues> parseData(String data, StringBuilder sb){
        sb.setLength(0);
        CroCharMap croMap = new CroCharMap();
        boolean quotes = false;
        int returnPointForCursor = 0;   //used for keeping track of where cursor encountered -> "
        char newLine = 0x0a;
        char delimiter = 0x09;
        int start = data.indexOf(newLine) + 1;
        int c = 0;
        List<ParsedValues> parsedValues = new ArrayList<>();
        ParsedValuesTempContainer tempPvContainer = new ParsedValuesTempContainer();
        tempPvContainer.storeInfo = storeInfo;

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
                    case 0 -> tempPvContainer.productName = croMap.replaceString(sb.append(data, start, i));
                    case 3 -> tempPvContainer.unit_quantity = data.substring(start, i);
                    case 4 -> tempPvContainer.unit  = data.substring(start, i);
                    case 2 -> tempPvContainer.brand = croMap.replaceString(sb.append(data, start, i));
                    case 5 -> {
                        comaToDot(sb.append(data, start, i));
                        String test = sb.toString();
                        tempPvContainer.price = (test.isEmpty()) ? null : Double.parseDouble(test);
                    }
                    case 13 -> {
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
