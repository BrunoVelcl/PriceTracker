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


public class ParserLidl extends Parser{

    ExecutorService executor =  Executors.newFixedThreadPool(14);


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
            executor.submit(() -> {
                try {
                    Connection connection = DriverManager.getConnection(this.server, this.userName, this.password);
                    System.out.println("Database connection established...");
                    processLoop(file, connection);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        executor.shutdown();


    }

    private List<String[]> parse(String data, StringBuilder sb){
        sb.setLength(0);
        CroCharMap croMap = new CroCharMap();
        boolean quotes = false;
        char newLine = 0x0a;
        char delimiter = 0x2c;
        int start = data.indexOf(0x0a) + 1;
        int c = 0;
        String[] temp = new String[6];
        List<String[]> parsed = new ArrayList<>();
        for(int i = start; i < data.length(); i ++){

            //Dealing with quotes
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
                    case 0 -> temp[2] = croMap.replaceString(sb.append(data, start, i));
                    case 2 -> temp[4] = data.substring(start, i);
                    case 3 -> temp[5] = data.substring(start, i);
                    case 4 -> temp[3] = croMap.replaceString(sb.append(data, start, i));
                    case 5 -> temp[1] = data.substring(start, i);
                    case 9 -> temp[0] = data.substring(start, i);
                }
                c++;
                start = i+1;
            }
            if(cursor == newLine){
                c = 0;
                start = i+1;
                parsed.add(temp);
                temp = new String[6];
            }
        }
        return parsed;
    }

    public static String parseAddress(File file, StringBuilder sb) {
        sb.setLength(0);
        sb.append(file.getName());

        int addressStart = 17;
        sb.delete(0, addressStart - 1);
        int lineCnt = 0;
        for (int i = 0; i < sb.length(); i++) {
            if (lineCnt == 4) {
                sb.delete(i-1, sb.length());
                return sb.toString();
            }
            if (sb.charAt(i) == '_') {
                lineCnt++;
                sb.replace(i, i + 1, " ");
            }
        }
        return null;
    }

    private void processLoop(File file, Connection connection)throws SQLException{
        //Find all files in dir
        StringBuilder sb = new StringBuilder();
        String data;
        try {
            data = Files.readString(file.toPath(), StandardCharsets.ISO_8859_1);
        }catch (IOException e){
            System.err.println("Couldn't find file to parse: " + file.getAbsolutePath());
            return;
        }

        // Address extraction
        String storeAddress = parseAddress(file,sb);
        if(storeAddress == null){
            System.err.println("Couldn't parse address for file: " + file.getAbsolutePath());
            return;
        }
        if(!Queries.storeInDatabase(storeAddress, connection)){
            Queries.insertStore(storeAddress, "1", connection);
        }

        // storeInfo[id, chain_id]
        this.storeInfo = Queries.findStoreByAddress(storeAddress, connection);
        this.parsedData = parse(data, sb);

        // Loop through all lines
        for(String[] line : parsedData){
            //Check if there is a barcode and discard if not
            if(line[0].isEmpty()){
                continue;
            }
            //Check if price is set,
            if(line[1].isEmpty()){
                line[1] = "-1";
            }
            // Check if product exists and add it if it doesn't
            if(!Queries.productInDatabase(line[0], connection)){
                Queries.insertProduct(line[0], line[2], line[3], line[4], line[5], connection);
            }
            //Check if price is up to date and add a new entry if not
            if(!Queries.priceIsUpToDate(line[0],line[1],connection)){
                Queries.insertPrice(line[1], line[0],storeInfo[0], connection);
            }
        }
        if(!file.delete()){
            System.err.println("Couldn't delete file: " + file.getAbsolutePath());
        };

    }

}
