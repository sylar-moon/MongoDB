package my.group.utilities;

import org.slf4j.Logger;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class MyProperties  {
    private final Properties properties;
    private static final String PROPERTIES_PATH = "config.properties";
    private final Logger logger = new MyLogger().getLogger();


    public MyProperties()  {
        properties=new Properties();
    }

    public void readPropertyFile(){
        try (FileInputStream inputStream = new FileInputStream(PROPERTIES_PATH)) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            properties.load(reader);
            reader.close();
        }catch (IOException e){
            logger.error("Properties file is not found");
        }
    }

    public String getProperty(String key)  {
        return properties.getProperty(key);
    }
}

