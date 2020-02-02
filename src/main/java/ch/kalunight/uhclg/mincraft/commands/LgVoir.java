package ch.kalunight.uhclg.mincraft.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import ch.kalunight.uhclg.GameData;
import ch.kalunight.uhclg.model.GameStatus;
import ch.kalunight.uhclg.model.PlayerData;
import ch.kalunight.uhclg.model.role.GrandMereLoup;
import ch.kalunight.uhclg.model.role.LoupGarouAmnesique;
import ch.kalunight.uhclg.model.role.Voyante;
import ch.kalunight.uhclg.worker.GameWorker;

public class LgVoir implements CommandExecutor {

  private static boolean roleChecked = false;
  
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
    
    if(playerSender == null || !(playerSender.getRole() instanceof Voyante)) {
      sender.sendMessage("Vous n'êtes pas la voyante !");
      return true;
    }
    
    if(args.length != 1) {
      sender.sendMessage("Vous devez mentionner quelqu'un pour pour pouvoir voir son rôle !");
      return false;
    }
    
    PlayerData playerToCheck = null;
    for(PlayerData player : GameData.getPlayersInGame()) {
      if(player.getAccount().getPlayer().getName().equals(args[0])) {
        playerToCheck = player;
      }
    }
    
    if(playerToCheck == null) {
      sender.sendMessage("Le nom du joueur à vérifier n'est pas valide !");
      return true;
    }
    
    if(!playerToCheck.isAlive()) {
      sender.sendMessage("Ce joueur n'est pas en vie !");
      return true;
    }
    
    if(roleChecked) {
      sender.sendMessage("Vous avez déjà vu quelqu'un ce jour !");
      return true;
    }
    
    setRoleChecked(true);
    
    if(playerToCheck.getRole() instanceof GrandMereLoup) {
      if(((GrandMereLoup) playerSender.getRole()).isFound()) {
        sender.sendMessage("Le rôle de " + playerToCheck.getAccount().getPlayer().getName() + " est \"" + playerToCheck.getRole().getName() + "\" !");
      }else {
        sender.sendMessage("Le rôle de " + playerToCheck.getAccount().getPlayer().getName() + " est \"Villageois\" !");
      }
    }else if(playerToCheck.getRole() instanceof LoupGarouAmnesique) {
      if(((LoupGarouAmnesique) playerToCheck.getRole()).isHasBeenFound()) {
        sender.sendMessage("Le rôle de " + playerToCheck.getAccount().getPlayer().getName() + " est \"" + playerToCheck.getRole().getName() + "\" !");
      }else {
        sender.sendMessage("Le rôle de " + playerToCheck.getAccount().getPlayer().getName() + " est \"Villageois\" !");
      }
    }else {
      sender.sendMessage("Le rôle de " + playerToCheck.getAccount().getPlayer().getName() + " est \"" + playerToCheck.getRole().getName() + "\" !");
    }
    
    return true;
  }

  public static boolean isRoleChecked() {
    return roleChecked;
  }

  public static void setRoleChecked(boolean roleChecked) {
    LgVoir.roleChecked = roleChecked;
  }

}
