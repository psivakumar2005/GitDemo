package utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;


public class jiraconfig {

    public Properties jiraconfigs() throws IOException {
        Properties properties = new Properties();
        FileInputStream file = new FileInputStream(System.getProperty("user.dir") + "\\src\\main\\java\\properties\\jira.properties");
        properties.load(file);
        return properties;
    }

    public String load(String s) throws IOException {
        return jiraconfigs().getProperty(s);
    }


}
