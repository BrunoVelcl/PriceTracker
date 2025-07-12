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
import org.postgresql.PGConnection;
import org.postgresql.copy.CopyManager;
import org.postgresql.core.BaseConnection;

public class Updatedb {
    protected final String server = "jdbc:postgresql://localhost:5432/postgres";
    protected final String userName = "postgres";
    protected final String password = "1410"; // TODO: what? is something wrong?
    protected final Connection con;
    protected final Statement statement;

    public Updatedb() throws SQLException{
            this.con = DriverManager.getConnection(this.server, this.userName, this.password);
            this.statement = this.con.createStatement();
    }

    public void updateAll(List<ParsedValues> pvList, File pricesCsv){
        try {
            updatePricesAndStores(pvList);
            importPrices(pricesCsv);
        } catch (SQLException e) {
            System.err.println("SQL Error: " + e.getMessage() + e.getSQLState());
        }
    }

    public void updatePricesAndStores(List<ParsedValues> pvList)throws SQLException {
        for(ParsedValues pv : pvList){
            if(!Queries.storeInDatabase(pv.getStoreInfo().getAddress(), con)){
                Queries.insertStore(pv.getStoreInfo(), con);
            }
            if(!Queries.productInDatabase(pv, con)){
                Queries.insertProduct(pv, con);
            }
        }
    }

    public void importPrices(File csv)throws SQLException{
        String query = "COPY prices(price, store_id, product_id) FROM " + "'" + csv.toString() + "'" + "WITH (DELIMITER ';')";
        statement.execute(query);
    }

    /**Run this when setting up the db*/
    public void firstTimeChainEntry() throws SQLException{
        Queries.insertNewChain(Store.LIDL,"https://www.lidl.hr/", this.con );
        Queries.insertNewChain(Store.KAUFLAND,"https://www.kaufland.hr/", this.con );
        Queries.insertNewChain(Store.PLODINE,"https://www.plodine.hr", this.con );
        Queries.insertNewChain(Store.SPAR,"https://www.spar.hr/", this.con );
        Queries.insertNewChain(Store.STUDENAC,"https://www.studenac.hr/", this.con );
    }



}
