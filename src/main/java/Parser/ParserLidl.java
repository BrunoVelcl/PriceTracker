package Parser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class ParserLidl extends Parser{

    public ParserLidl(File storeDir){
        this.fileList = storeDir.listFiles();
    }
    //TODO: change this!
    public ParserLidl(){
        this( new File("\\dumpster\\Lidl"));
    }
    public void parse(){

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
            // TODO: logic in here
        }
    }

    private void extract(String data){

    }


}
