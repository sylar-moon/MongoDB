package my.group;

import com.mongodb.client.*;
import my.group.collections.CollectionCreator;
import my.group.collections.CollectionUpdater;
import my.group.utilities.*;
import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.conversions.Bson;
import org.slf4j.Logger;

import java.io.IOException;

public class App {
    private static final Logger LOGGER = new MyLogger().getLogger();
    private static final MyProperties PROPERTIES = new MyProperties();
    private static final RPS RPS = new RPS();
    private static final CollectionCreator CREATOR = new CollectionCreator();
    private static final CollectionUpdater UPDATER = new CollectionUpdater();
    public static void main(String[] args) {
        readPropertyFile();
        String endPoint = PROPERTIES.getProperty("endPoint");
//        int sizeGoods = Integer.parseInt(PROPERTIES.getProperty("sizeGoods"));
            try (MongoClient mongoClient = MongoClients.create(endPoint)) {
                RPS.startWatch();
                MongoDatabase database = mongoClient.getDatabase("epicentr");
                CREATOR.createAndFillStoresFromCSV(database);
//                CREATOR.createGoodCollection(database,sizeGoods);
                UPDATER.deliverGoodsToStores(database);
                LOGGER.info(String.valueOf(RPS.getTimeSecond()));

            }



    }

    private static Bson eq(String title, String back_to_the_future) {
    return new Bson() {
        @Override
        public <TDocument> BsonDocument toBsonDocument(Class<TDocument> aClass, CodecRegistry codecRegistry) {
            return new BsonDocument();
        }
    };
    }


    private static void readPropertyFile() {
        try {
            PROPERTIES.readPropertyFile();
        } catch (IOException e) {
            LOGGER.error("Properties file not found");
        }
    }
}
