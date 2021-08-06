package data;

import java.sql.*;
import static java.lang.System.exit;

public class Database {
    public static Connection connection = null;
    PropertiesFileReading fileReading = new PropertiesFileReading();
    private String CONNECTION_STRING = fileReading.getCONNECTION_STRING();
    private String USERNAME = fileReading.getUSERNAME();
    private String PASSWORD = fileReading.getPASSWORD();

    public void connect() {
        if(connection != null) {
            return;
        } else {
            try {
                connection = DriverManager.getConnection(CONNECTION_STRING, USERNAME, PASSWORD);
            } catch(SQLException e) {
                e.printStackTrace();
                exit(-1);
            }
        }
    }
}
