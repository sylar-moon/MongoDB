package my.group.utilities;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MyCSVReaderTest {
    MyCSVReader reader = new MyCSVReader();
    @Test
    void getListAllLinesFromCSV() {
        List<String[]> test = new ArrayList<>(Arrays.asList(new String[]{"Test1", "Testing1"},
                new String[]{"Test2", "Testing2"},
                new String[]{"Test3", "Testing3"}));
        List<String[]> result = reader.getListAllLinesFromCSV("src/test/java/my/group/utilities/test.csv");
        for (int i = 0; i < result.size(); i++) {
            assertArrayEquals(test.get(i), result.get(i));
        }
    }
}