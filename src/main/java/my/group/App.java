package my.group;

import com.mongodb.client.*;
import my.group.collection.CollectionCreator;
import my.group.collection.DocumentFinder;
import my.group.utilities.*;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.List;

public class App {
    private static final Logger LOGGER = new MyLogger().getLogger();
    private static final MyProperties PROPERTIES = new MyProperties();
    private static final RPS RPS = new RPS();
    private static final CollectionCreator CREATOR = new CollectionCreator();
    public static final DocumentFinder FINDER = new DocumentFinder();
    public static final  MyCSVReader CSV_READER = new MyCSVReader();
    public static void main(String[] args) {
        List<String[]> types = CSV_READER.getListAllLinesFromCSV("type.csv");
        String typeGood = args.length != 0 ? args[0] : types.get(0)[0];
        readPropertyFile();
        String endPoint = PROPERTIES.getProperty("endPoint");
        int sizeGoods = Integer.parseInt(PROPERTIES.getProperty("sizeGoods"));
            try (MongoClient mongoClient = MongoClients.create(endPoint)) {
                RPS.startWatch();
                MongoDatabase database = mongoClient.getDatabase("epicentr");
                CREATOR.createAndFillStoresFromCSV(database);
//                CREATOR.createGoodCollection(database,sizeGoods,types);
//                CREATOR.deliverGoodsToStores(database);
//                FINDER.findAddressStore(database,typeGood);
                LOGGER.info("Time all program {} seconds",RPS.getTimeSecond());
            }
    }


    private static void readPropertyFile() {
        try {
            PROPERTIES.readPropertyFile();
        } catch (IOException e) {
            LOGGER.error("Properties file not found");
        }
    }
}
