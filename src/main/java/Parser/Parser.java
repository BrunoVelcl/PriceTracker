package Parser;

import java.io.File;
import java.sql.Connection;
import java.util.List;
import java.util.Scanner;

public abstract class Parser {
    // Database info
    protected Connection connection;
    protected final String server = "jdbc:postgresql://localhost:5432/postgres";
    protected final String userName = "postgres";
    protected final String password = "1410"; // TODO: what? is something wrong?
    protected File[] fileList;


}
