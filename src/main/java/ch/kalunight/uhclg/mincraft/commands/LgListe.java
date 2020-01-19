package ch.kalunight.uhclg.mincraft.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import ch.kalunight.uhclg.GameData;
import ch.kalunight.uhclg.model.GameStatus;
import ch.kalunight.uhclg.model.PlayerData;
import ch.kalunight.uhclg.model.RoleClan;
import ch.kalunight.uhclg.worker.GameWorker;

public class LgListe implements CommandExecutor {

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    
    if(!GameData.getGameStatus().equals(GameStatus.IN_GAME)) {
      sender.sendMessage("La partie n'a pas commencé !");
      return true;
    }
    
    if(GameWorker.getActualDayNumber() < 2) {
      sender.sendMessage("Vous ne connaissez pas encore votre rôle !");
      return true;
    }
    
    PlayerData playerSender = null;
    for(PlayerData player : GameData.getPlayersInGame()) {
      if(player.getAccount().getPlayer().getName().equals(sender.getName())) {
        playerSender = player;
      }
    }
    
    if(playerSender == null || !playerSender.getRole().getClan().equals(RoleClan.WOLFS)) {
      sender.sendMessage("Vous n'êtes pas un loup garou !");
      return true;
    }
    
    sender.sendMessage(getListWolfs());
    
    return true;
  }
  
  public static String getListWolfs() {
    StringBuilder wolfsList = new StringBuilder("Liste des loups :\n");
    
    for(PlayerData player : GameData.getPlayersInGame()) {
      if(player.getRole().getClan().equals(RoleClan.WOLFS)) {
        wolfsList.append(player.getAccount().getPlayer().getName() + "\n");
      }
    }
    
    return wolfsList.toString();
  }
}
