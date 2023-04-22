package my.group.DTO;

import org.apache.commons.lang3.RandomStringUtils;
import org.bson.Document;

import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

public class GoodFactory {
    private  final Random random = new Random();



    public Stream<Good> createRandomGood(List<String[]>types) {
        int indexRandomType = random.nextInt(types.size());
        Document randomType = new Document("nameType", types.get(indexRandomType)[0]);
        String randomName = RandomStringUtils.random(random.nextInt(20), true, false);
        return Stream.generate(() -> new Good(randomName, randomType));
    }
}
