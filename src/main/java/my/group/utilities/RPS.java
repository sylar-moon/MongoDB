package my.group.utilities;

import org.apache.commons.lang3.time.StopWatch;

import java.util.concurrent.TimeUnit;

public class RPS {
    private final StopWatch watch = new StopWatch();
    private int count = 0;
    private long saveTime;
    public void startWatch() {
        watch.start();
    }

    public double getRPS() {
        return count / (double) getTimeSecond();
    }

    public long getTimeSecond() {
        return watch.getTime(TimeUnit.SECONDS);
    }

    public long getTimeMilliSecond() {
        return watch.getTime(TimeUnit.MILLISECONDS);
    }

    public int getCount() {
        return count;
    }

    public void stopWatch() {
        watch.stop();
    }

    public void incrementCount() {
        count++;
    }

    public void setSaveTime (long time){
        saveTime=time;
    }

    public long getSaveTime() {
        return saveTime;
    }

}
