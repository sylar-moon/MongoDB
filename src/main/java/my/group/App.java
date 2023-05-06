package my.group;

import com.mongodb.client.*;
import my.group.collection.CollectionCreator;
import my.group.collection.DocumentFinder;
import my.group.utilities.*;
import org.slf4j.Logger;


import java.util.List;

/**
 * The program creates a database in MongoDB and fills it with stores and goods,
 * after which it finds a store with the largest number of goods of a certain type,
 * the type of goods is set in the program launch parameters
 */
public class App {
    private static final Logger LOGGER = new MyLogger().getLogger();
    private static final MyProperties PROPERTIES = new MyProperties();
    private static final RPS RPS = new RPS();
    private static final CollectionCreator CREATOR = new CollectionCreator();
    public static final DocumentFinder FINDER = new DocumentFinder();
    public static final MyCSVReader CSV_READER = new MyCSVReader();
    public static final String NAME_DATA_BASE = "epicentr";

    public static void main(String[] args) {
        List<String[]> types = CSV_READER.getListAllLinesFromCSV("type.csv");
        String typeGood = args.length != 0 ? args[0] : types.get(0)[0];
        PROPERTIES.readPropertyFile();
        String endPoint = PROPERTIES.getProperty("endPoint");
        int sizeGoods = Integer.parseInt(PROPERTIES.getProperty("sizeGoods"));

        try (MongoClient mongoClient = MongoClients.create(endPoint)) {
            RPS.startWatch();
            dropDataBase(mongoClient);
            MongoDatabase database = mongoClient.getDatabase(NAME_DATA_BASE);
            List<Object> idStores = CREATOR.createAndFillStoresFromCSV(database);
            RPS.setSaveTime(RPS.getTimeSecond());
            RPS goodRPS = CREATOR.createGoodCollection(database, sizeGoods, types);
            RPS deliverGoodsRPS = CREATOR.deliverGoodsToStores(database, idStores);
            RPS findAddressRPS = FINDER.findAddressStore(database, typeGood);

            LOGGER.info("The speed of creating and filling a collection of stores = {} sec.", RPS.getSaveTime());
            LOGGER.info("The speed of creating and filling a collection of goods = {} sec.", goodRPS.getTimeSecond());
            LOGGER.info("RPS of filling a collection of goods = {}", goodRPS.getRPS());
            LOGGER.info("The number of added goods = {}", goodRPS.getCount());
            LOGGER.info("The speed of deliver goods to stores= {} sec.", deliverGoodsRPS.getTimeSecond());
            LOGGER.info("RPS of deliver goods to stores= {}", deliverGoodsRPS.getRPS());
            LOGGER.info("Count of deliver goods to stores= {}", deliverGoodsRPS.getCount());
            LOGGER.info("The speed of find Address store= {} sec.", findAddressRPS.getTimeSecond());

            RPS.stopWatch();
            LOGGER.info("Time all program {} seconds", RPS.getTimeSecond());
        }
    }

    private static void dropDataBase(MongoClient mongoClient) {
        MongoIterable<String> databases = mongoClient.listDatabaseNames();
        for (String database : databases) {
           if(database.equals(NAME_DATA_BASE)){
               mongoClient.getDatabase(NAME_DATA_BASE).drop();
           }
        }
        }
}
