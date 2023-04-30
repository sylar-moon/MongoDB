package my.group.collection;

import static org.mockito.Mockito.*;

import com.mongodb.ServerAddress;
import com.mongodb.ServerCursor;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;

import com.mongodb.client.MongoCursor;
import org.bson.Document;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class DocumentFinderTest {
//    @Test
//    void testFindStoreGoodDocMaxQuantityType() {
//        List<Document> storeGoodDocs = Arrays.asList(
//                new Document("_id", 1)
//                        .append("store", "store1")
//                        .append("typeGood", "type1")
//                        .append("sizeGood", 10),
//                new Document("_id", 2)
//                        .append("store", "store1")
//                        .append("typeGood", "type2")
//                        .append("sizeGood", 20),
//                new Document("_id", 3)
//                        .append("store", "store2")
//                        .append("typeGood", "type1")
//                        .append("sizeGood", 30),
//                new Document("_id", 4)
//                        .append("store", "store2")
//                        .append("typeGood", "type2")
//                        .append("sizeGood", 40)
//        );
//
//        String typeGood = "type1";
//
//        MongoCollection<Document> collection = mock(MongoCollection.class);
//        AggregateIterable<Document> aggregateIterable = mock(AggregateIterable.class);
//        when(aggregateIterable.iterator()).thenReturn(getMongoCursor(storeGoodDocs));
//        when(collection.aggregate(anyList())).thenReturn(aggregateIterable);
//
//        DocumentFinder documentFinder = new DocumentFinder();
//
//        AggregateIterable<Document> result = documentFinder.findStoreGoodDocMaxQuantityType(typeGood, collection);
//
////
////        for (Document document : result) {
////            System.out.println(document);
////        }
//
//        if (result.iterator().hasNext()) {
//            assertEquals(30, result.first().get("sizeGood"));
//            assertEquals("store2", result.first().get("store"));
//            assertEquals(typeGood, result.first().get("typeGood"));
//        } else {
//            fail("Result is null or empty");
//        }
//    }
//
//
//
//
//
//    MongoCursor<Document> getMongoCursor(List<Document> documents) {
//
//        return new MongoCursor<Document>() {
//            private final Iterator<Document> iterator = documents.iterator();
//
//            @Override
//            public boolean hasNext() {
//                return iterator.hasNext();
//            }
//
//            @Override
//            public Document next() {
//                return iterator.next();
//            }
//
//            @Override
//            public int available() {
//                return 0;
//            }
//
//            @Override
//            public Document tryNext() {
//                return null;
//            }
//
//            @Override
//            public void close() {
//            }
//
//            @Override
//            public ServerCursor getServerCursor() {
//                return null;
//            }
//
//            @Override
//            public ServerAddress getServerAddress() {
//                return null;
//            }
//        };
//    }
//
}