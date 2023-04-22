package my.group.utilities;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class MyCSVReader {

    public List<String[]> getListAllLinesFromCSV(String pathToCSV) {
        try (CSVReader reader = new CSVReader(new FileReader(pathToCSV))){
            return reader.readAll();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CsvException e) {
            e.printStackTrace();
        }
        return null;
    }
}
