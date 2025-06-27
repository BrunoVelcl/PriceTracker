package Parser;

import java.io.File;
import java.sql.Connection;
import java.util.List;
import java.util.Scanner;

public abstract class Parser {
    // Database info

    protected final String server = "jdbc:postgresql://localhost:5432/postgres";
    protected final String userName = "postgres";
    protected final String password = "1410"; // TODO: what? is something wrong?
    protected File[] fileList;
    // Parsed data should always be [barcode, price, name, brand, unit_quantity, unit]
    protected List<String[]> parsedData;
    protected String[] storeInfo;

}
