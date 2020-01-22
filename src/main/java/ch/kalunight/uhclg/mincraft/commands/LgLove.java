package ch.kalunight.uhclg.mincraft.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import ch.kalunight.uhclg.GameData;
import ch.kalunight.uhclg.model.GameStatus;
import ch.kalunight.uhclg.model.PlayerData;
import ch.kalunight.uhclg.model.Role;
import ch.kalunight.uhclg.worker.GameWorker;

public class LgLove implements CommandExecutor {

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
    
    if(playerSender == null || !playerSender.getRole().equals(Role.CUPIDON)) {
      sender.sendMessage("Vous n'êtes pas cupidon !");
      return true;
    }
    
    if(playerSender.getRole().equals(Role.CUPIDON) && GameData.isCupidonUseHisPower()) {
      sender.sendMessage("Vous avez déjà utilisé votre pouvoir !");
      return true;
    }
    
    if(args.length != 2) {
      sender.sendMessage("Vous devez mentionner deux joueurs !");
      return false;
    }
    
    String modelName = args[0];
    PlayerData loverOne = null;
    for(PlayerData player : GameData.getPlayersInGame()) {
      if(player.getAccount().getPlayer().getName().equals(modelName)) {
        loverOne = player;
      }
    }
    
    if(loverOne == null) {
      sender.sendMessage("Le joueur 1 n'existe pas !");
      return true;
    }
    
    modelName = args[1];
    PlayerData loverTwo = null;
    for(PlayerData player : GameData.getPlayersInGame()) {
      if(player.getAccount().getPlayer().getName().equals(modelName)) {
        loverTwo = player;
      }
    }
    
    if(loverTwo == null) {
      sender.sendMessage("Le joueur 2 n'existe pas !");
      return true;
    }
    
    
    return false;
  }

}
