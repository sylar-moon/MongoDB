package my.group.collections;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import my.group.DTO.Good;
import my.group.DTO.GoodFactory;
import my.group.utilities.MyCSVReader;
import my.group.utilities.MyLogger;
import org.bson.Document;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class CollectionCreator {

    private final Logger logger = new MyLogger().getLogger();
    GoodFactory factory = new GoodFactory();
    MyCSVReader csvReader = new MyCSVReader();

    public void createAndFillStoresFromCSV(MongoDatabase database) {
            MongoCollection<Document> collection = database.getCollection("store");
            ArrayList<Document>list = new ArrayList<>();
           List<String[]> stores = csvReader.getListAllLinesFromCSV("store.csv");
            //В цикле считуем все строки из csv файла и добавляем в базу данных
        for (String[] strings : stores) {
               list.add(new Document("city",strings[0]).append("address",strings[1]));
        }
            collection.insertMany(list);

    }

    public void createGoodCollection(MongoDatabase database, int sizeGoods) {
        MongoCollection<Document> goodCollection = database.getCollection("good");
        List<String[]> types = csvReader.getListAllLinesFromCSV("type.csv");
        Supplier<Stream<Good>> supplier = () -> new GoodFactory().createRandomGood(types);
        ArrayList<Document>list = new ArrayList<>(Math.min(sizeGoods, 1000));
        for (int i = 0; i < sizeGoods; i++) {
            Good good = getGood(supplier);
            list.add(new Document("name",good.getGoodName()).append("typeID",good.getTypeId()));
            if(list.size()>1000){
                goodCollection.insertMany(list);
                list.clear();
            }
        }
        goodCollection.insertMany(list);
    }


    private Good getGood(Supplier<Stream<Good>> supplier) {
        Optional<Good> optionalPerson = supplier.get().findAny();
        return optionalPerson.orElse(null);
    }
}
