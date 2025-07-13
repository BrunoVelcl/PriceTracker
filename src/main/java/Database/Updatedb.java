package Database;

import Engine.BarcodeMap;
import FileFetcher.Store;
import Parser.ParsedValues;
import Parser.StoreInfo;
import java.io.File;
import java.sql.*;
import java.util.List;
import java.util.Set;


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

    public void updateAll(BarcodeMap barcodeMap, List<ParsedValues> pvList, File pricesCsv){
        try {
            updateStores(barcodeMap);
            updateProducts(pvList);
            importPrices(pricesCsv);
        } catch (SQLException e) {
            System.err.println("SQL Error: " + e.getMessage() + e.getSQLState());
        }
    }

    public void updateProducts(List<ParsedValues> pvList)throws SQLException {
        Set<Long> productsInDatabase = Queries.returnAllBarcodes(this.con);
        for(ParsedValues pv : pvList){
            if(!productsInDatabase.contains(pv.getBarcode())){
                Queries.insertProduct(pv, this.con);
            }
        }
    }

    public void updateStores(BarcodeMap barcodeMap) throws SQLException{
        List<StoreInfo> stores = barcodeMap.getStores();
        int storesLastIndex = stores.size();
        int dbCount = Queries.storeMaxId(this.con);
        if(dbCount != 0){
            dbCount++;
        }
        while (storesLastIndex > dbCount){
            Queries.insertStore(stores.get(dbCount).getId(), stores.get(dbCount), con);
            dbCount++;
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
