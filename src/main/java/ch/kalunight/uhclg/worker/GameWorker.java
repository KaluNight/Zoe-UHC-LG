package ch.kalunight.uhclg.worker;

import java.util.concurrent.TimeUnit;
import org.bukkit.World;
import com.google.common.base.Stopwatch;
import ch.kalunight.uhclg.GameData;
import ch.kalunight.uhclg.model.GameStatus;

public class GameWorker implements Runnable {

  private static Stopwatch chrono;
  
  private static World world;
  
  @Override
  public void run() {
    if(GameData.getGameStatus().equals(GameStatus.IN_GAME)) {
      
    }
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
