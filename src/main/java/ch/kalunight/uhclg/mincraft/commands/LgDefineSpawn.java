package ch.kalunight.uhclg.mincraft.commands;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ch.kalunight.uhclg.GameData;
import ch.kalunight.uhclg.ZoePluginMaster;
import ch.kalunight.uhclg.model.GameStatus;
import ch.kalunight.uhclg.worker.GameWorker;

public class LgDefineSpawn implements CommandExecutor {

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    
    Player player = null;
    for(Player playerToCheck : ZoePluginMaster.getMinecraftServer().getOnlinePlayers()) {
      if(playerToCheck.getName().equals(sender.getName())) {
        player = playerToCheck;
      }
    }
    
    if(player == null) {
      return true;
    }
    
    if(!GameData.getGameStatus().equals(GameStatus.WAIT_LOBBY_CREATION)) {
      player.sendMessage("Le lobby à déjà été définit !");
      return true;
    }
    
    generateLobby(player.getLocation());
    
    for(Player playerToCheck : ZoePluginMaster.getMinecraftServer().getOnlinePlayers()) {
      playerToCheck.setGameMode(GameMode.ADVENTURE);
    }
    
    GameData.setGameStatus(GameStatus.IN_LOBBY);
    
    return true;
  }

  private void generateLobby(Location playerLocation) {
    World world = ZoePluginMaster.getMinecraftServer().getWorld("world");
    
    GameWorker.setWorld(world);

    int spawnSize = 25;
    int spawnHigh = 200;

    for(int j = 0; j < spawnSize; j++) {
      for(int i = 0; i < spawnSize; i++) {
        world.getBlockAt(playerLocation.getBlockX() + i, spawnHigh, playerLocation.getBlockY() + j).setType(Material.GLASS);
      }
    }

    world.setSpawnLocation(playerLocation.getBlockX() + (spawnSize / 2), spawnHigh + 1, playerLocation.getBlockY() + (spawnSize / 2));

    GameData.setLobbyLocation(playerLocation);
  }
}
