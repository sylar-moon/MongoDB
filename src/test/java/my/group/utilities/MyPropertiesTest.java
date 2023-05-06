package my.group.utilities;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MyPropertiesTest {
    MyProperties properties = new MyProperties();
    @Test
    void getProperty() {
        properties.setPropertiesPath("src/test/java/my/group/utilities/test.properties");
        properties.readPropertyFile();
        String result= properties.getProperty("test");
        assertEquals("testing", result);
    }
}