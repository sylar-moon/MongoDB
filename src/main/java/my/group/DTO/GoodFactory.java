package my.group.DTO;

import org.apache.commons.lang3.RandomStringUtils;

import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

public class GoodFactory {
    private  final Random random = new Random();

    public Stream<Good> createRandomGood(List<String[]>types) {
        int indexRandomType = random.nextInt(types.size());
        String randomName = RandomStringUtils.random(random.nextInt(20), true, false);
        return Stream.generate(() -> new Good(randomName, types.get(indexRandomType)[0]));
    }
}
