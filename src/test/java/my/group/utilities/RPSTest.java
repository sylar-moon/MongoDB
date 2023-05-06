package my.group.utilities;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class RPSTest {
static RPS rps = new RPS();
@BeforeAll
static void initial() throws InterruptedException {
    rps.startWatch();
    rps.incrementCount();
    rps.incrementCount();
    Thread.sleep(1000);
    rps.stopWatch();
    rps.setSaveTime(100);

}
    @Test
    void getRPS() {
        assertEquals(rps.getRPS(),2.0);
    }

    @Test
    void getTimeSecond() {
        assertEquals(rps.getTimeSecond(),1);

    }

    @Test
    void getTimeMilliSecond() {

        long rounded = (Math.round(rps.getTimeMilliSecond() / 1000.0) * 1000);
        assertEquals(rounded,1000);

    }

    @Test
    void getCount() {
        assertEquals(rps.getCount(),2);

    }

    @Test
    void getSaveTime() {
    assertEquals(rps.getSaveTime(),100);
    }
}