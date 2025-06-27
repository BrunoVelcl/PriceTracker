package Parser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;





public class ParserLidl extends Parser{

    public ParserLidl(File storeDir){
        this.fileList = storeDir.listFiles();
    }
    //TODO: change this!
    public ParserLidl(){
        this( new File("\\dumpster\\Lidl"));
    }
    public void run(List<StoreNameLinks>) throws SQLException {

        //Establish db connection
        try{
            this.connection = DriverManager.getConnection(this.server, this.userName, this.password);
            System.out.println("Database connection established...");
        }catch(SQLException e){
            System.err.println("Unable to establish PostgreSQL connection.");
            return;
        }

        //Find all files in dir
        for(File file : this.fileList){
            String data;
            try {
                data = Files.readString(file.toPath());
            }catch (IOException e){
                System.err.println("Couldn't find file to parse: " + file.getAbsolutePath());
                continue;
            }

            // Address extraction
            this.storeAddress = parseAddress(file, sb);
            if(this.storeAddress == null){
                System.err.println("Couldn't parse address for file: " + file.getAbsolutePath());
                continue;
            }
            if(!Queries.storeInDatabase(this.storeAddress, this.connection)){
                Queries.insertStore(this.storeAddress, "1", connection);
            }
            // storeInfo[id, chain_id]
            this.storeInfo = Queries.findStoreByAddress(this.storeAddress, connection);
            this.parsedData = parse(data, this.sb);

            for(String[] line : parsedData){

            }
        }
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

}
