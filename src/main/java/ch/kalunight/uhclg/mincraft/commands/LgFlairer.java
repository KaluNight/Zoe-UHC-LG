package ch.kalunight.uhclg.mincraft.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import ch.kalunight.uhclg.GameData;
import ch.kalunight.uhclg.model.GameStatus;
import ch.kalunight.uhclg.model.PlayerData;
import ch.kalunight.uhclg.model.Role;
import ch.kalunight.uhclg.model.RoleClan;
import ch.kalunight.uhclg.worker.GameWorker;

public class LgFlairer implements CommandExecutor {
  
  private static final int RENARD_DISTANCE = 15;
  
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
    
    if(playerSender == null || !playerSender.getRole().equals(Role.RENARD)) {
      sender.sendMessage("Vous n'êtes pas un renard !");
      return true;
    }
    
    if(args.length != 1) {
      sender.sendMessage("Vous devez mentionner un joueur !");
      return false;
    }
    
    String modelName = args[0];
    PlayerData tracked = null;
    for(PlayerData player : GameData.getPlayersInGame()) {
      if(player.getAccount().getPlayer().getName().equals(modelName)) {
        tracked = player;
      }
    }
    
    if(tracked == null) {
      sender.sendMessage("Ce joueur n'existe pas !");
      return true;
    }
    
    if(tracked.getAccount().getPlayer().getLocation().distance(playerSender.getAccount().getPlayer().getLocation())
        > RENARD_DISTANCE) {
      sender.sendMessage("Vous n'êtes pas suffisament proche de ce joueur !");
      return true;
    }
    
    if(tracked.getRole().equals(Role.GRAND_MERE_LOUP) && !GameData.isGrandMereLoupReveal()) {
      sender.sendMessage(tracked.getAccount().getPlayer().getDisplayName() + " est innocent.");
    }else if(tracked.getRole().equals(Role.LOUP_GAROU_AMNESIQUE) && !GameData.isLoupAmnesiqueFound()) {
      sender.sendMessage(tracked.getAccount().getPlayer().getDisplayName() + " est innocent.");
    }else if(tracked.getRole().getRoleEnum().getClan().equals(RoleClan.VILLAGE) 
        || tracked.getRole().getRoleEnum().getClan().equals(RoleClan.SPECIAL)) {
      sender.sendMessage(tracked.getAccount().getPlayer().getDisplayName() + " est innocent.");
    }else {
      sender.sendMessage(tracked.getAccount().getPlayer().getDisplayName() + " est coupable !");
    }
    
    return true;
  }

}
