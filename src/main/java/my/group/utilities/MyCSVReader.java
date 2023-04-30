package my.group.utilities;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.slf4j.Logger;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class MyCSVReader {
    private final Logger logger = new MyLogger().getLogger();

    public List<String[]> getListAllLinesFromCSV(String pathToCSV) {
        try (CSVReader reader = new CSVReader(new FileReader(pathToCSV))) {
            return reader.readAll();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            logger.error("Unable to read file located at this path: {}", pathToCSV, e);
            System.exit(0);
        } catch (CsvException e) {
            logger.error("CSVReader cannot read this file: {}", pathToCSV, e);
            System.exit(0);
        }
        return Collections.emptyList();
    }
}
