package my.group.collections;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateManyModel;
import com.mongodb.client.model.WriteModel;
import my.group.utilities.MyLogger;
import org.bson.Document;
import org.slf4j.Logger;

import java.util.*;

public class CollectionUpdater {
    private final Logger logger = new MyLogger().getLogger();
    Random random = new Random();

//    public void deliverGoodsToStores(MongoDatabase database) {
//        MongoCollection<Document> goodCollection = database.getCollection("good");
//        MongoCollection<Document> storeCollection = database.getCollection("store");
//
//        storeCollection.updateMany(
//                new Document(), // фильтр для выборки документов, которые нужно обновить
//                new Document("$set", new Document("goods", new ArrayList<>())) // новое поле и его значение
//        ).getModifiedCount();
//
//        MongoCursor<Document> cursor = goodCollection.find().iterator();
//        try {
//            while (cursor.hasNext()) {
//                Document doc = cursor.next();
//                storeCollection.updateMany(
//                new Document(), // фильтр для выборки документов, которые нужно обновить
//                new Document("$push", new Document("goods", Arrays.asList(doc,new Document("sizeGood",String.valueOf(random.nextInt(300)+1)))))) // новое поле и его значение
//        .getModifiedCount();
//            }
//        } finally {
//            cursor.close();
//        }
//
//    }

    public void deliverGoodsToStores(MongoDatabase database) {
        MongoCollection<Document> goodCollection = database.getCollection("good");
        MongoCollection<Document> storeCollection = database.getCollection("store");

        // фильтр для выборки документов, которые нужно обновить
        Document filter = new Document();

        // очищаем массив goods для выбранных документов
        Document update = new Document("$set", new Document("goods", new ArrayList<>()));
        storeCollection.updateMany(filter, update);

        // добавляем документы из коллекции good в массив goods для выбранных документов
        List<WriteModel<Document>> writes = new ArrayList<>();
        for (Document goodDoc : goodCollection.find()) {
            Document updateDoc = new Document("$push", new Document("goods", Arrays.asList(
                    goodDoc,
                    new Document("sizeGood", String.valueOf(random.nextInt(300) + 1))
            )));
            writes.add(new UpdateManyModel<>(filter, updateDoc));
        }
        storeCollection.bulkWrite(writes);
    }



}

