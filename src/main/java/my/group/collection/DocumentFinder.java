package my.group.collection;

import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import my.group.utilities.MyLogger;
import org.bson.Document;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DocumentFinder {
    private final Logger logger = new MyLogger().getLogger();


    public void findAddressStore(MongoDatabase database, String typeGood) {
        MongoCollection<Document> collectionStoreGood = database.getCollection("storeGood");
        MongoCollection<Document> collectionStore = database.getCollection("store");
        List<Document> pipeline = new ArrayList<>(Arrays.asList(
           new Document("$match",new Document("typeGood",typeGood)),
                new Document("$sort",new Document("sizeGood",-1)),
                new Document("$limit", 1)
        ));
        AggregateIterable<Document> result = collectionStoreGood.aggregate(pipeline);
        for (Document document : result) {
            logger.info(document.toString());
            Object idStore = document.get("store");
            FindIterable<Document> doc =  collectionStore.find(new Document("_id",idStore));
            logger.info("store {}, {}",doc.first().get("city"), doc.first().get("address"));
        }
    }
}
