package my.group.collection;

import com.mongodb.WriteConcern;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.*;
import my.group.good.Good;
import my.group.good.GoodFactory;
import my.group.utilities.MyCSVReader;
import my.group.utilities.RPS;
import my.group.validator.MyValidator;
import org.bson.Document;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class CollectionCreator {

    Random random = new Random();
    public final MyCSVReader csvReader = new MyCSVReader();

    private final MyValidator validator = new MyValidator();
    public static final String COLLECTION_STORE = "store";
    public static final String COLLECTION_TYPE = "type";
    public static final int BUFFER_SIZE = 100000;
    public static final int MAX_SIZE_GOOD_IN_STORE = 500;

    /**
     * Creates a collection of stores, reads store addresses from csv. file
     * and writes them to the collection
     * @param database mongoDB database
     * @return list of added stores id
     */
    public List<Object> createAndFillStoresFromCSV(MongoDatabase database) {
        MongoCollection<Document> collection = database.getCollection(COLLECTION_STORE);
        List<InsertOneModel<Document>> list = new ArrayList<>();
        List<Object> idAddStores = new ArrayList<>();

        List<String[]> stores = csvReader.getListAllLinesFromCSV("store.csv");
        // In a loop, we read all the lines from the csv file and add them to the database
        for (String[] strings : stores) {
            Document doc = new Document("city", strings[0]).append("address", strings[1]);
            InsertOneModel<Document> insertOneModel = new InsertOneModel<>(doc);
            list.add(insertOneModel);
        }
        collection.bulkWrite(list);

        for (InsertOneModel<Document> insertOneModel : list) {
            idAddStores.add(insertOneModel.getDocument().get("_id"));
        }
        return idAddStores;
    }

    /**
     * Creates a collection of goods, generates goods using GoodFactory and writes them to the collection
     * @param database mongoDB database
     * @param sizeGoods number of products to generate
     * @param types list of product types with csv. file
     * @return RPS with the time when items were added and the number of items added
     */
    public RPS createGoodCollection(MongoDatabase database, int sizeGoods, List<String[]> types) {
        RPS rps = new RPS();
        rps.startWatch();
        MongoCollection<Document> goodCollection = database.getCollection("good");
        Supplier<Stream<Good>> supplier = () -> new GoodFactory().createRandomGood(types);
        int bufferSize = Math.min(sizeGoods, BUFFER_SIZE);
        List<WriteModel<Document>> writes = new ArrayList<>(bufferSize);

        BulkWriteOptions bulkWriteOptions = new BulkWriteOptions().ordered(true);
        for (int i = 0; i < sizeGoods; i++) {
            Good good = getGood(supplier);
            if (validator.validateGood(good)) {
                rps.incrementCount();
                Document insertDoc = new Document("name", good.getGoodName()).append(COLLECTION_TYPE, good.getType());
                writes.add(new InsertOneModel<>(insertDoc));
                if (writes.size() >= bufferSize) {
                    goodCollection.withWriteConcern(WriteConcern.UNACKNOWLEDGED).bulkWrite(writes, bulkWriteOptions);
                    writes.clear();
                }
            }
        }

        if (!writes.isEmpty()) {
            goodCollection.withWriteConcern(WriteConcern.UNACKNOWLEDGED).bulkWrite(writes, bulkWriteOptions);
        }
        rps.stopWatch();
        return rps;
    }


    /**
     * Creates a collection with the id of the stores and the goods that are in them,
     * fills it with a random number of goods
     * @param database mongoDB database
     * @param idStores list with id of all stores in the database
     * @return RPS with good addition rate and goods quantity added
     */
//    public RPS deliverGoodsToStores(MongoDatabase database, List<Object> idStores) {
//        RPS rps = new RPS();
//        rps.startWatch();
//        MongoCollection<Document> goodCollection = database.getCollection("good");
//        MongoCollection<Document> storeGoodCollection = database.getCollection("storeGood");
//
//        goodCollection.createIndex(Indexes.ascending("name"));
//        goodCollection.createIndex(Indexes.ascending(COLLECTION_TYPE));
//
//        int bufferSize = Math.min((int) goodCollection.countDocuments(), BUFFER_SIZE);
//        List<WriteModel<Document>> writes = new ArrayList<>(bufferSize);
//        BulkWriteOptions bulkWriteOptions = new BulkWriteOptions().ordered(true);
//
//        for (Object idStore : idStores) {
//
//
//                for (Document goodDoc : goodCollection.find()) {
//                    rps.incrementCount();
//
//                    Document doc = new Document(COLLECTION_STORE, idStore)
//                            .append("nameGood", goodDoc.getString("name"))
//                            .append("typeGood", goodDoc.getString(COLLECTION_TYPE))
//                            .append("sizeGood", random.nextInt(500) + 1);
//                    writes.add(new InsertOneModel<>(doc));
//                    if (writes.size() > bufferSize) {
//                        storeGoodCollection.withWriteConcern(WriteConcern.UNACKNOWLEDGED).bulkWrite(writes, bulkWriteOptions);
//                        writes.clear();
//                    }
//                }
//
//                if (!writes.isEmpty()) {
//                    storeGoodCollection.withWriteConcern(WriteConcern.UNACKNOWLEDGED).bulkWrite(writes, bulkWriteOptions);
//                }
//            }
//
//        rps.stopWatch();
//        return rps;
//    }




    public RPS deliverGoodsToStores(MongoDatabase database, List<Object> idStores) {
        RPS rps = new RPS();
        rps.startWatch();
        MongoCollection<Document> goodCollection = database.getCollection("good");
        MongoCollection<Document> storeGoodCollection = database.getCollection("storeGood");

        goodCollection.createIndex(Indexes.ascending("name"));
        goodCollection.createIndex(Indexes.ascending(COLLECTION_TYPE));
        int bufferSize = Math.min((int) goodCollection.countDocuments(), BUFFER_SIZE);

        // Thread pool to run parallel tasks for each of the passed stores.
        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        //In the loop, we add to the storeGood collection for each store all the goods with their random quantity
        for (Object idStore : idStores) {

            BulkWriteOptions bulkWriteOptions = new BulkWriteOptions().ordered(true);
            List<WriteModel<Document>> writes = new ArrayList<>(bufferSize);
            // Start an asynchronous task that will run in the background.
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                for (Document goodDoc : goodCollection.find()) {
                    rps.incrementCount();

                    Document doc = new Document(COLLECTION_STORE, idStore)
                            .append("nameGood", goodDoc.getString("name"))
                            .append("typeGood", goodDoc.getString(COLLECTION_TYPE))
                            .append("sizeGood", random.nextInt(MAX_SIZE_GOOD_IN_STORE) + 1);
                    writes.add(new InsertOneModel<>(doc));
                    if (writes.size() > bufferSize) {
                        storeGoodCollection.withWriteConcern(WriteConcern.UNACKNOWLEDGED).bulkWrite(writes, bulkWriteOptions);
                        writes.clear();
                    }
                }

                if (!writes.isEmpty()) {
                    storeGoodCollection.withWriteConcern(WriteConcern.UNACKNOWLEDGED).bulkWrite(writes, bulkWriteOptions);
                }
            }, executorService);

            futures.add(future);
        }

        // Wait for all tasks in parallel threads to complete
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        // Terminate all threads
        executorService.shutdown();
        rps.stopWatch();
        return rps;
    }


    private Good getGood(Supplier<Stream<Good>> supplier) {
        Optional<Good> optionalPerson = supplier.get().findAny();
        return optionalPerson.orElse(null);
    }
}
