package my.group.collection;

import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Indexes;
import my.group.utilities.MyLogger;
import my.group.utilities.RPS;
import org.bson.Document;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class DocumentFinder {
    private final Logger logger = new MyLogger().getLogger();
    private final RPS rps = new RPS();

    public RPS findAddressStore(MongoDatabase database, String typeGood) {
        rps.startWatch();
        MongoCollection<Document> collectionStoreGood = database.getCollection("storeGood");
        MongoCollection<Document> collectionStore = database.getCollection("store");
        collectionStoreGood.createIndex(Indexes.ascending("typeGood"));
        collectionStoreGood.createIndex(Indexes.ascending("sizeGood"));
        List<Document> pipeline = new ArrayList<>(Arrays.asList(
                new Document("$match", new Document("typeGood", typeGood)),
                new Document("$sort", new Document("sizeGood", -1)),
                new Document("$limit", 1)
        ));
        AggregateIterable<Document> result = collectionStoreGood.aggregate(pipeline);
        for (Document document : result) {
            Object idStore = document.get("store");
            FindIterable<Document> doc = collectionStore.find(new Document("_id", idStore));
            Object city = Objects.requireNonNull(doc.first()).get("city");
            Object address = Objects.requireNonNull(doc.first()).get("address");
            logger.info("store {}, {}", city, address);
        }
        rps.stopWatch();
        return rps;
    }
}
