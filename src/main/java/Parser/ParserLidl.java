package Parser;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import FileFetcher.StoreNameLinks;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class ParserLidl extends Parser{
    private final Integer chain_id = 1;
    ExecutorService executor =  Executors.newFixedThreadPool(14);
    String storeAddress;
    Integer storeID;

    public ParserLidl(File storeDir){
        this.fileList = storeDir.listFiles();
    }
    //TODO: change this!
    public ParserLidl(){
        this( new File("G:\\Dev\\Prices\\dumpster\\LIDL"));
    }
    public void run() throws SQLException {

        //Find all files in dir
        for(File file : this.fileList){
            storeID = null;
            // Address extraction
            storeAddress = parseAddress(file ,new StringBuilder());
            if(storeAddress == null){
                System.err.println("Couldn't parse address for file: " + file.getAbsolutePath());
                return;
            }

            executor.submit(() -> {
                try {
                    Connection connection = DriverManager.getConnection(this.server, this.userName, this.password);
                    System.out.println("Database connection established...");
                    //Check if store exists in database and add if it doesn't
                    if(!Queries.storeInDatabase(storeAddress, connection)){
                        Queries.insertStore(storeAddress, "1", connection);
                    }
                    if(storeID == null) {
                        try {
                            storeID = Integer.parseInt(Queries.findStoreByAddress(storeAddress, connection)[0]);
                        } catch (NumberFormatException e) {
                            System.err.println("Couldn't parse store ID for file: " + file.getAbsolutePath());
                        }
                    }
                    processLoop(file, connection);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        executor.shutdown();
        try{
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            System.err.println("Multithreading failed.");
        }


    }

    private List<ParsedValues> parse(String data, StringBuilder sb){
        sb.setLength(0);
        CroCharMap croMap = new CroCharMap();
        boolean quotes = false;
        char newLine = 0x0a;
        char delimiter = 0x2c;
        int start = data.indexOf(0x0a) + 1;
        int c = 0;
        ParsedValues temp;
        List<ParsedValues> parsedValues = new ArrayList<>();
        Long barcode = null;
        Double price = null;
        String productName = null;
        String brand = null;
        String unit_quantity = null;
        String unit = null;

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
                    case 0 -> productName = croMap.replaceString(sb.append(data, start, i));
                    case 2 -> unit_quantity = data.substring(start, i);
                    case 3 -> unit  = data.substring(start, i);
                    case 4 -> brand = croMap.replaceString(sb.append(data, start, i));
                    case 5 -> {
                        String test = data.substring(start, i );
                        price = (test.isEmpty()) ? null : Double.parseDouble(test);
                    }
                    case 9 -> {
                        String test = data.substring(start, i );
                        barcode = (test.isEmpty()) ? null : Long.parseLong(test);
                    }
                }
                c++;
                start = i+1;
            }
            if(cursor == newLine){
                c = 0;
                start = i+1;

                ParsedValues pv = new ParsedValues(barcode, price, productName,brand,unit_quantity, unit, storeAddress, storeID, chain_id);
                if(!pv.isValidInput()){
                    continue;
                }
                parsedValues.add(pv);
            }
        }
        return parsedValues;
    }

    public static String parseAddress(File file, StringBuilder sb) {
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

    private void processLoop(File file, Connection connection)throws SQLException{
        // Read file
        StringBuilder sb = new StringBuilder();
        String data;
        try {
            data = Files.readString(file.toPath(), StandardCharsets.ISO_8859_1);
        }catch (IOException e){
            System.err.println("Couldn't find file to parse: " + file.getAbsolutePath());
            return;
        }

        List<ParsedValues> parsedData = parse(data, sb);

        // Check if products exists and add it if it doesn't
        Queries.insertNewProducts(parsedData , connection);

        //Check if price is up to date and add a new entry if not

        Queries.insertPrice(parsedData, connection);

        if(!file.delete()){
            System.err.println("Couldn't delete file: " + file.getAbsolutePath());
        };
    }

}
