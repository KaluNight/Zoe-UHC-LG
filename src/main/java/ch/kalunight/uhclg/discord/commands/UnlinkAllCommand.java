package ch.kalunight.uhclg.discord.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import ch.kalunight.uhclg.ZoePluginMaster;

public class UnlinkAllCommand extends Command {

  public UnlinkAllCommand() {
    this.name = "unlinkAll";
    this.arguments = "";
    this.hidden = false;
    this.ownerCommand = true;
    this.guildOnly = true;
  }
  
  @Override
  protected void execute(CommandEvent event) {
    ZoePluginMaster.getPlayersRegistered().clear();
    
    event.reply("All players has been unlink !");
  }

}
