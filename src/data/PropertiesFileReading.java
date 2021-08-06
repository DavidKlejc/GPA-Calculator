package data;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class PropertiesFileReading {

    private String filePath = "./src/data/config.properties";
    private String CONNECTION_STRING;
    private String USERNAME;
    private String PASSWORD;

    Properties props = new Properties();
    FileInputStream in;

    {
        try {
            in = new FileInputStream(filePath);
            props.load(in);
            CONNECTION_STRING = props.getProperty("CONNECTION_STRING");
            USERNAME = props.getProperty("USERNAME");
            PASSWORD = props.getProperty("PASSWORD");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getCONNECTION_STRING() {
        return CONNECTION_STRING;
    }

    public String getUSERNAME() {
        return USERNAME;
    }

    public String getPASSWORD() {
        return PASSWORD;
    }
}
