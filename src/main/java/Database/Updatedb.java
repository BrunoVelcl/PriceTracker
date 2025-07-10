package Database;

import Engine.BarcodeMap;
import FileFetcher.Store;
import Parser.CroCharMap;
import Parser.ParsedValues;
import Parser.StoreInfo;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Updatedb {
    protected final String server = "jdbc:postgresql://localhost:5432/postgres";
    protected final String userName = "postgres";
    protected final String password = "1410"; // TODO: what? is something wrong?

    public void updateDb(List<ParsedValues> pvList)throws SQLException {
        int threadCount = 14;

        // Split the list into number of threads
        int listSize = (pvList.size() / threadCount);
        List<List<ParsedValues>> multyList = new ArrayList<>();
        for(int i = 0; i < threadCount; i++){
            multyList.add(new ArrayList<>());
            for(int j = 0; j <= listSize; j++){
                multyList.get(i).add(pvList.removeLast());
                if(pvList.isEmpty()){
                    break;
                }
            }
        }

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        for(List<ParsedValues> thread : multyList) {
            final List<ParsedValues> copy = thread; // We need this for multithreading to work otherwise
            executor.submit(() -> {
                try {
                    processThread(copy);
                } catch (SQLException e) {
                    System.err.println("SQL error: " + e.getMessage() + " || " + e.getSQLState());
                }
            });

        }
        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        }catch (InterruptedException e){
            System.err.println("Multithreading error: " + e.getMessage());
        }
    }

    private void processThread(List<ParsedValues> pvList) throws SQLException {

        //We need a separate connection for every function instance since we are multithreading
        Connection con;
        try {
            con = DriverManager.getConnection(this.server, this.userName, this.password);
        }catch (SQLException e){
            System.err.println("Couldn't connect to database: " + e.getMessage() + " ||| " + e.getSQLState());
            return;
        }

        // Race condition loop
        for(int i = 0; i < pvList.size(); i++){
            if(!Queries.storeInDatabase(pvList.get(i).getStoreInfo().getAddress(), con)){
                Queries.insertStore(pvList.get(i).getStoreInfo(), con);
            }
            if(!Queries.productInDatabase(pvList.get(i), con)){
                try {
                    Queries.insertProduct(pvList.get(i), con);
                }catch (SQLException e){
                    System.err.println("Found race condition: " + e.getMessage() + " || " +e.getSQLState());
                    System.out.println(pvList.get(i).getProductName());
                    System.out.println(pvList.get(i).getStoreInfo().getAddress());
                    System.out.println(pvList.get(i).getStoreInfo().getChain());
                    System.out.println("Retrying...");
                    i--;
                }
            }
            Queries.insertPrice(pvList.get(i),con);
        }

//        for(ParsedValues pv : pvList){
//            if(!Queries.storeInDatabase(pv.getStoreInfo().getAddress(), con)){
//                Queries.insertStore(pv.getStoreInfo(), con);
//            }
//
//            if(!Queries.productInDatabase(pv, con)){
//                try {
//                    Queries.insertProduct(pv, con);
//                }catch (SQLException e){
//                    System.err.println("Found race condition: " + e.getMessage() + " || " +e.getSQLState());
//                }
//            }
//
//            Queries.insertPrice(pv,con);
//        }
    }

    // Only called when initializing the database
    public void firstTimeChainEntry() throws SQLException{

        Connection con;
        try {
            con = DriverManager.getConnection(this.server, this.userName, this.password);
        }catch (SQLException e){
            System.err.println("Couldn't connect to database: " + e.getMessage() + " ||| " + e.getSQLState());
            return;
        }

        Queries.insertNewChain(Store.LIDL,"https://www.lidl.hr/", con );
        Queries.insertNewChain(Store.KAUFLAND,"https://www.kaufland.hr/", con );
        Queries.insertNewChain(Store.PLODINE,"https://www.plodine.hr", con );
        Queries.insertNewChain(Store.SPAR,"https://www.spar.hr/", con );
        Queries.insertNewChain(Store.STUDENAC,"https://www.studenac.hr/", con );
    }

}
