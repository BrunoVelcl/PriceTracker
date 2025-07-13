package Database;
import FileFetcher.Store;
import Parser.ParsedValues;
import Parser.StoreInfo;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class Queries {

    /**Inserts new product into database*/
    public static void insertProduct(ParsedValues pv, Connection connection) throws SQLException{
        String query = "INSERT INTO products (id, name, brand, unit_quantity, unit) VALUES (?,?,?,?,?) ON CONFLICT (id) DO NOTHING";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setLong(1, pv.getBarcode());
        ps.setString(2, pv.getProductName());
        ps.setString(3, pv.getBrand());
        ps.setString(4, pv.getUnit_quantity());
        ps.setString(5, pv.getUnit());
        ps.executeUpdate();

    }

    /**Inserts new store into database*/
    public static void insertStore(int id, StoreInfo storeInfo, Connection connection) throws SQLException{
        String query = "INSERT INTO stores (id, address, chain_id) VALUES (?,?,?)";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setInt(1,id);
        ps.setString(2,storeInfo.getAddress());
        ps.setInt(3, storeInfo.getChain().ordinal());
        ps.executeUpdate();
    }

    /**Returns a Set of all barcodes in the database*/
    public static Set<Long> returnAllBarcodes(Connection con)throws SQLException{
        Set<Long> barcodes = new HashSet<>();
        String query = "SELECT id FROM products";
        PreparedStatement pr = con.prepareStatement(query);
        ResultSet rs = pr.executeQuery();
        while (rs.next()){
            barcodes.add(rs.getLong("id"));
        }
        return barcodes;
    }

    /**Return max id value from stores*/
    public static int storeMaxId(Connection connection)throws SQLException{
        String query = "SELECT MAX(id) FROM stores";
        PreparedStatement ps = connection.prepareStatement(query);
        ResultSet rs = ps.executeQuery();
        rs.next();
        return rs.getInt("max");
    }

    /**Inserts new chain*/
    public static void insertNewChain(Store store, String url, Connection connection) throws SQLException{
        String query = "INSERT INTO chains (id, name, url) VALUES (?,?,?)";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setShort(1, (short) store.ordinal());
        ps.setString(2, store.toString());
        ps.setString(3, url);
        ps.executeUpdate();
    }

}
