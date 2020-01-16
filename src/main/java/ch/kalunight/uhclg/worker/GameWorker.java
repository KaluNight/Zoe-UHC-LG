package ch.kalunight.uhclg.worker;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import org.bukkit.World;
import com.google.common.base.Stopwatch;
import ch.kalunight.uhclg.GameData;
import ch.kalunight.uhclg.model.GameStatus;
import ch.kalunight.uhclg.model.TimeStatus;

public class GameWorker implements Runnable {

  private static final int DAY_DURATION = 24000;
  private static final Duration DAY1_DURATION = Duration.ofMinutes(20);
  private static final Duration DAY2_DURATION = Duration.ofMinutes(10);

  private static final int START_OF_DAY = 0;

  private static final int START_OF_NIGHT = 12000;

  private static TimeStatus timeStatus;

  private static long secondsTime;

  private static long actualTime;

  private static Stopwatch chrono;

  private static World world;

  @Override
  public void run() {
    if(GameData.getGameStatus().equals(GameStatus.IN_GAME)) {
      defineWorldTime();
      
      
      
    }
  }

  private void defineWorldTime() {
    setActualTime(actualTime + secondsTime);

    if(actualTime > DAY_DURATION) {
      setSecondsTime(DAY_DURATION / DAY2_DURATION.getSeconds() / 2);
      setActualTime(0);
      setTimeStatus(TimeStatus.DAY);
      world.setTime(secondsTime);
    }else {
      if(actualTime > START_OF_NIGHT) {
        setTimeStatus(TimeStatus.NIGHT);
      }else {
        setTimeStatus(TimeStatus.DAY);
      }
      world.setTime(actualTime);
    }
  }

  public static void setupGameWorker() {
    world.setTime(START_OF_DAY);
    setTimeStatus(TimeStatus.DAY);

    secondsTime = DAY_DURATION / DAY1_DURATION.getSeconds() / 2;
    actualTime = 0;

    world.setTime(actualTime);
  }

  public static String getChronoString() {
    long seconds = chrono.elapsed(TimeUnit.SECONDS);
    long absSeconds = Math.abs(seconds);
    return String.format(
        "%02d:%02d",
        (absSeconds % 3600) / 60,
        absSeconds % 60);
  }

  public static Stopwatch getGameStartTime() {
    return chrono;
  }

  public static void setGameStartTime(Stopwatch gameStartTime) {
    GameWorker.chrono = gameStartTime;
  }

  public static World getWorld() {
    return world;
  }

  public static void setWorld(World world) {
    GameWorker.world = world;
  }

  public static TimeStatus getTimeStatus() {
    return timeStatus;
  }

  public static void setTimeStatus(TimeStatus timeStatus) {
    GameWorker.timeStatus = timeStatus;
  }
  
  public static long getSecondsTime() {
    return secondsTime;
  }

  public static void setSecondsTime(long secondsTime) {
    GameWorker.secondsTime = secondsTime;
  }

  public static long getActualTime() {
    return actualTime;
  }

  public static void setActualTime(long actualTime) {
    GameWorker.actualTime = actualTime;
  }
  
}
