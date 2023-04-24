package my.group.collection;

import com.mongodb.bulk.BulkWriteInsert;
import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.BulkWriteOptions;
import com.mongodb.client.model.InsertOneModel;
import com.mongodb.client.model.WriteModel;
import my.group.DTO.Good;
import my.group.DTO.GoodFactory;
import my.group.utilities.MyCSVReader;
import my.group.utilities.MyLogger;
import my.group.utilities.RPS;
import org.bson.BsonObjectId;
import org.bson.BsonValue;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.slf4j.Logger;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class CollectionCreator {

    Random random = new Random();
    private final Logger logger = new MyLogger().getLogger();
    MyCSVReader csvReader = new MyCSVReader();
    private RPS rps = new RPS();


    public void createAndFillStoresFromCSV(MongoDatabase database) {
            MongoCollection<Document> collection = database.getCollection("store");
        List<WriteModel<Document>> list = new ArrayList<>();

        List<String[]> stores = csvReader.getListAllLinesFromCSV("store.csv");
            //В цикле считуем все строки из csv файла и добавляем в базу данных
        for (String[] strings : stores) {
            Document doc = new Document("city",strings[0]).append("address",strings[1]);
               list.add(new InsertOneModel<>(doc));
        }
        collection.bulkWrite(list);
    }

    public void createGoodCollection(MongoDatabase database, int sizeGoods,List<String[]> types) {

        MongoCollection<Document> goodCollection = database.getCollection("good");
        Supplier<Stream<Good>> supplier = () -> new GoodFactory().createRandomGood(types);
        List<WriteModel<Document>> writes = new ArrayList<>(Math.min(sizeGoods, 100000));
        int bufferSize = 100000;
        BulkWriteOptions bulkWriteOptions = new BulkWriteOptions().ordered(false);
        for (int i = 0; i < sizeGoods; i++) {
            Good good = getGood(supplier);
            Document insertDoc = new Document("name",good.getGoodName()).append("type",good.getType());
            writes.add(new InsertOneModel<>(insertDoc));
            if(writes.size() >= bufferSize){
                goodCollection.bulkWrite(writes, bulkWriteOptions);
                writes.clear();
            }
        }
        if(!writes.isEmpty()){
            goodCollection.bulkWrite(writes, bulkWriteOptions);
        }
    }




    public void deliverGoodsToStores(MongoDatabase database) {
        MongoCollection<Document> goodCollection = database.getCollection("good");
        MongoCollection<Document> storeCollection = database.getCollection("store");
        MongoCollection<Document> storeGoodCollection = database.getCollection("storeGood");
        rps.startWatch();
        int bufferSize = 100000;

        List<WriteModel<Document>> writes = new ArrayList<>(bufferSize);
        BulkWriteOptions bulkWriteOptions = new BulkWriteOptions().ordered(false);
        for (Document storeDoc : storeCollection.find()) {
            for (Document goodDoc : goodCollection.find()) {
                rps.incrementCount();
                Document doc = new Document("store", storeDoc.getObjectId("_id"))
                        .append("typeGood", goodDoc.getString("type")).append("sizeGood", random.nextInt(500) + 1);
                rps.incrementCount();
                writes.add(new InsertOneModel<>(doc));
                if (writes.size() > 10000) {
                    storeGoodCollection.bulkWrite(writes, bulkWriteOptions);
                    writes.clear();
                }
            }
        }
        if (!writes.isEmpty()) {
            storeGoodCollection.bulkWrite(writes);
        }
        logger.info("Time to add products to stores {} seconds", rps.getTimeSecond());
        logger.info("RPS to add products to stores {}", rps.getRPS());
    }





    private Good getGood(Supplier<Stream<Good>> supplier) {
        Optional<Good> optionalPerson = supplier.get().findAny();
        return optionalPerson.orElse(null);
    }
}
