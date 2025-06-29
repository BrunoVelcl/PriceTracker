package Parser;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class Queries {

    // Update database with new price
    public static void insertPrice(List<ParsedValues> parsedValues, Connection connection) throws SQLException {
        String query = "INSERT INTO prices (price, product_id, store_id) SELECT ?,?,? " +
                "WHERE (SELECT price FROM prices WHERE product_id = ? AND store_id = ? ORDER BY updated DESC LIMIT 1) IS DISTINCT FROM ?";
        PreparedStatement ps = connection.prepareStatement(query);
        for(ParsedValues pv : parsedValues){
            ps.setDouble(1, pv.getPrice());
            ps.setLong(2, pv.getBarcode());
            ps.setInt(3, 1);
            ps.setInt(4, 1);
            ps.setLong(5, pv.getBarcode());
            ps.setDouble(6, pv.getPrice());
            ps.addBatch();
        }

        ps.executeBatch();
    }
    // Insert new product into database
    public static void insertProduct(String barcode, String name, String brand, String unit_quantity, String unit, Connection connection) throws SQLException{
        String query = "INSERT INTO products (id, name, brand, unit_quantity, unit) VALUES (?,?,?,?,?)";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setLong(1,Long.parseLong(barcode));
        ps.setString(2, name);
        ps.setString(3, brand);
        ps.setString(4, unit_quantity);
        ps.setString(5, unit);
        ps.executeUpdate();

    }

    // Insert new store into database
    public static void insertStore(String address, String chain, Connection connection) throws SQLException{
        String query = "INSERT INTO stores (address, chain_id) VALUES (?,?)";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setString(1,address);
        ps.setInt(2, Integer.parseInt(chain));
        ps.executeUpdate();
    }

    // Returns [name, brand, unit_quantity, unit] or []
    public static String[] findProductByBarcode(String barcode, Connection connection)throws SQLException{
        String query = "SELECT * FROM products WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setLong(1,Long.parseLong(barcode));
        ResultSet rs = ps.executeQuery();
        if(rs.next()) {
            return new String[]{rs.getString("name"),
                    rs.getString("brand"),
                    rs.getString("unit_quantity"),
                    rs.getString("unit")
            };
        }else {
            return new String[0];
        }
    }

    // Returns true is product already exists in db
    public static boolean productInDatabase(ParsedValues pv, Connection connection)throws SQLException{
        String query = "SELECT * FROM products WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setLong(1,pv.getBarcode());
        ResultSet rs = ps.executeQuery();
        return rs.next();
    }

    // Returns [id, chain_id] or []
    public static String[] findStoreByAddress(String address, Connection connection) throws SQLException{
        String query = "SELECT * FROM stores WHERE address = ?";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setString(1,address);
        ResultSet rs = ps.executeQuery();
        if(rs.next()){
            return new String[]{rs.getString("id"), rs.getString("chain_id")};
        }else {
            return new String[0];
        }
    }

    // Returns true is store already exists in db
    public static boolean storeInDatabase(String address, Connection connection) throws SQLException{
        String query = "SELECT * FROM stores WHERE address = ?";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setString(1,address);
        ResultSet rs = ps.executeQuery();
        return rs.next() && address.equals(rs.getString("address"));

    }

    // Returns true if price is up to date and false if it changed
    public static boolean priceIsUpToDate(String barcode, String price, Connection connection) throws SQLException{
        String query = "SELECT price FROM prices WHERE product_id = ? ORDER BY updated DESC LIMIT  1";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setLong(1, Long.parseLong(barcode));
        ResultSet rs = ps.executeQuery();
        if(rs.next()){
            return rs.getString("price").equals(price);
        }
        return false;
    };

    public static void insertNewProducts(List<ParsedValues> parsedValues, Connection connection) throws SQLException{
        String query = "INSERT INTO products (id, name, brand, unit_quantity, unit) VALUES (?,?,?,?,?)" +
                "ON CONFLICT (id) DO NOTHING";
        PreparedStatement ps = connection.prepareStatement(query);
        for(ParsedValues pv : parsedValues){
            ps.setLong(1, pv.getBarcode());
            ps.setString(2, pv.getProductName());
            ps.setString(3, pv.getBrand());
            ps.setString(4, pv.getUnit_quantity());
            ps.setString(5, pv.getUnit());
            ps.addBatch();
        }
        ps.executeBatch();

    }

}
