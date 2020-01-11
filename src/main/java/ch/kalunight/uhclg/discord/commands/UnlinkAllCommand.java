package ch.kalunight.uhclg.discord.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import ch.kalunight.uhclg.GameData;

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
    GameData.getPlayersRegistered().clear();
    
    event.reply("All players has been unlink !");
  }

}
