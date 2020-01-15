package ch.kalunight.uhclg.worker;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import org.bukkit.World;
import com.google.common.base.Stopwatch;
import ch.kalunight.uhclg.GameData;
import ch.kalunight.uhclg.model.GameStatus;
import ch.kalunight.uhclg.model.TimeStatus;

public class GameWorker implements Runnable {

  private static final int dayDuration = 24000;
  private static final Duration DAY1_DURATION = Duration.ofMinutes(20);
  private static final Duration DAY2_DURATION = Duration.ofMinutes(10);
  
  private static final int START_OF_DAY = 0;
  
  private static final int START_OF_NIGHT = 12000;
  
  private static TimeStatus timeStatus;
  
  private static Stopwatch chrono;
  
  private static World world;
  
  @Override
  public void run() {
    if(GameData.getGameStatus().equals(GameStatus.IN_GAME)) {
      
      
    }
  }
  
  public static void setupGameWorker() {
    world.setTime(START_OF_DAY);
    timeStatus = TimeStatus.DAY;
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

}
