package ch.kalunight.uhclg.mincraft.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import ch.kalunight.uhclg.GameData;
import ch.kalunight.uhclg.model.PlayerData;
import ch.kalunight.uhclg.model.role.EnfantSauvage;
import ch.kalunight.uhclg.worker.GameWorker;

public class LgModel implements CommandExecutor {
  
  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    
    PlayerData enfantSauvage = null;
    for(PlayerData player : GameData.getPlayersInGame()) {
      if(player.getAccount().getPlayer().getName().equals(sender.getName())) {
        enfantSauvage = player;
      }
    }
    
    if(enfantSauvage == null || !(enfantSauvage.getRole() instanceof EnfantSauvage)) {
      sender.sendMessage("Vous n'êtes pas un enfant sauvage !");
      return true;
    }
    
    EnfantSauvage enfantSauvageRole = (EnfantSauvage) enfantSauvage.getRole();
    
    if(enfantSauvageRole.getModel() != null) {
      sender.sendMessage("Vous avez déjà défini " + enfantSauvageRole.getModel().getAccount().getPlayer().getName() + " comme modèle.");
      return true;
    }
    
    if(GameWorker.getActualDayNumber() != 2) {
      sender.sendMessage("Vous pouvez choisir un modèle seulement le jour 2");
      return true;
    }
    
    if(args.length != 1) {
      sender.sendMessage("Vous devez mentionner un joueur !");
      return false;
    }
    
    String modelName = args[0];
    PlayerData model = null;
    for(PlayerData player : GameData.getPlayersInGame()) {
      if(player.getAccount().getPlayer().getName().equals(modelName)) {
        model = player;
      }
    }
    
    if(model == null) {
      sender.sendMessage("Ce joueur n'existe pas !");
      return true;
    }
    
    enfantSauvageRole.setModel(model);
    sender.sendMessage("Vous avez selectionné " + model.getAccount().getPlayer().getName() + " comme modèle !");
    
    return true;
  }
}
