package ch.kalunight.uhclg.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class LgStart implements CommandExecutor {

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    
    sender.getServer().broadcastMessage("The game start !");
    
    return true;
  }

}
