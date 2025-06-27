package Parser;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Queries {

    // Update database with new price
    public static void insertPrice(String price, String product, String store, Connection connection) throws SQLException {
        String query = "INSERT INTO prices (price, product_ID ,store_id) VALUES (?,?,?)";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setString(1,price);
        ps.setString(2,product);
        ps.setString(3, store);
        ps.executeUpdate();
    }

    // Insert new product into database
    public static void insertProduct(String barcode, String name, String brand, String unit_quantity, String unit, Connection connection) throws SQLException{
        String query = "INSERT INTO products (id, name, brand, unit_quantity, unit) VALUES (?,?,?,?,?)";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setString(1,barcode);
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
        ps.setString(2, chain);
        ps.executeUpdate();
    }

    // Returns [name, brand, unit_quantity, unit] or []
    public static String[] findProductByBarcode(String barcode, Connection connection)throws SQLException{
        String query = "SELECT * FROM products WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setString(1,barcode);
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
    public static boolean productInDatabase(String barcode, Connection connection)throws SQLException{
        String query = "SELECT * FROM products WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setString(1,barcode);
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
            return new String[]{rs.getString("id"), rs.getString("chain.id")};
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
        return rs.next();
    }

}
