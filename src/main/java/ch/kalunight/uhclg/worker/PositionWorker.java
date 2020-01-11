package ch.kalunight.uhclg.worker;

import org.bukkit.Server;
import org.bukkit.entity.Player;

import ch.kalunight.uhclg.GameData;
import ch.kalunight.uhclg.ZoePluginMaster;
import ch.kalunight.uhclg.model.GameStatus;

public class PositionWorker implements Runnable {

  @Override
  public void run() {
    Server server = ZoePluginMaster.getMinecraftServer();
    
    if(GameData.getGameStatus().equals(GameStatus.IN_LOBBY)) {
      
      for(Player player : server.getOnlinePlayers()) {
        if(player.getLocation().getY() < server.getWorld("world").getSpawnLocation().getY()) {
          player.teleport(server.getWorld("world").getSpawnLocation());
        }
      }
    }
    
  }

}
