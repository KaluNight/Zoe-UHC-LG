package ch.kalunight.uhclg.worker;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public class SpectatorWorker implements Runnable {

  private Player player;
  
  public SpectatorWorker(Player player) {
    this.player = player;
  }
  
  @Override
  public void run() {
    player.setGameMode(GameMode.SPECTATOR);
  }

}
