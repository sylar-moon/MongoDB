package my.group.collection;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.*;
import my.group.good.Good;
import my.group.good.GoodFactory;
import my.group.utilities.MyCSVReader;
import my.group.utilities.MyLogger;
import my.group.utilities.RPS;
import my.group.validator.MyValidator;
import org.bson.Document;
import org.slf4j.Logger;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class CollectionCreator {

    Random random = new Random();
    private final Logger logger = new MyLogger().getLogger();
    public final MyCSVReader csvReader = new MyCSVReader();
    private RPS rps ;
    private final MyValidator validator = new MyValidator();
    public static final String COLLECTION_STORE = "store";
    public static final String COLLECTION_TYPE = "type";
    public static final int BUFFER_SIZE = 100000;


    public List<Object> createAndFillStoresFromCSV(MongoDatabase database) {
        MongoCollection<Document> collection = database.getCollection(COLLECTION_STORE);
        List<InsertOneModel<Document>> list = new ArrayList<>();

        List<String[]> stores = csvReader.getListAllLinesFromCSV("store.csv");
        //В цикле считуем все строки из csv файла и добавляем в базу данных
        for (String[] strings : stores) {
            Document doc = new Document("city", strings[0]).append("address", strings[1]);
            list.add(new InsertOneModel<>(doc));
        }
        collection.bulkWrite(list);
        List<Object> idAddStores = new ArrayList<>();
        for (InsertOneModel<Document> insertOneModel : list) {
            idAddStores.add(insertOneModel.getDocument().get("_id"));
        }
        return idAddStores;
    }

    public RPS createGoodCollection(MongoDatabase database, int sizeGoods, List<String[]> types) {
        rps = new RPS();
        rps.startWatch();
        MongoCollection<Document> goodCollection = database.getCollection("good");
        Supplier<Stream<Good>> supplier = () -> new GoodFactory().createRandomGood(types);
        int bufferSize = Math.min(sizeGoods, BUFFER_SIZE);
        List<WriteModel<Document>> writes = new ArrayList<>(bufferSize);
        BulkWriteOptions bulkWriteOptions = new BulkWriteOptions().ordered(false);
        for (int i = 0; i < sizeGoods; i++) {
            Good good = getGood(supplier);
            if (validator.validateGood(good)) {
                rps.incrementCount();
                Document insertDoc = new Document("name", good.getGoodName()).append(COLLECTION_TYPE, good.getType());
                writes.add(new InsertOneModel<>(insertDoc));
                if (writes.size() >= bufferSize) {
                    goodCollection.bulkWrite(writes, bulkWriteOptions);
                    writes.clear();
                }
            }
        }

        if (!writes.isEmpty()) {
            goodCollection.bulkWrite(writes,bulkWriteOptions);
        }
        rps.stopWatch();
        return rps;
    }


    public RPS deliverGoodsToStores(MongoDatabase database, List<Object> idStores) {
        rps = new RPS();
        rps.startWatch();
        MongoCollection<Document> goodCollection = database.getCollection("good");
        MongoCollection<Document> storeGoodCollection = database.getCollection("storeGood");
        int sizeDocs = (int)goodCollection.countDocuments()* idStores.size();
        int bufferSize = Math.min(sizeDocs, BUFFER_SIZE);
        List<WriteModel<Document>> writes = new ArrayList<>(bufferSize);
        BulkWriteOptions bulkWriteOptions = new BulkWriteOptions().ordered(false);
        for (Object idStore : idStores) {

            for (Document goodDoc : goodCollection.find()) {
                rps.incrementCount();
                Document doc = new Document(COLLECTION_STORE, idStore)
                        .append("nameGood", goodDoc.getString("name"))
                        .append("typeGood", goodDoc.getString(COLLECTION_TYPE))
                        .append("sizeGood", random.nextInt(500) + 1);

                writes.add(new InsertOneModel<>(doc));
                if (writes.size() > bufferSize) {

                    storeGoodCollection.bulkWrite(writes, bulkWriteOptions);
                    writes.clear();

                }

            }
        }

        if (!writes.isEmpty()) {
            storeGoodCollection.bulkWrite(writes,bulkWriteOptions);
        }
        logger.info("Time to add products to stores {} seconds", rps.getTimeSecond());
        logger.info("RPS to add products to stores {}", rps.getRPS());
        rps.stopWatch();
        return rps;
    }


    private Good getGood(Supplier<Stream<Good>> supplier) {
        Optional<Good> optionalPerson = supplier.get().findAny();
        return optionalPerson.orElse(null);
    }
}
