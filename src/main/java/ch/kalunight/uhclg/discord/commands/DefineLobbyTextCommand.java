package ch.kalunight.uhclg.discord.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import ch.kalunight.uhclg.GameData;

public class DefineLobbyTextCommand extends Command {

  public DefineLobbyTextCommand() {
    this.name = "defineTextLobby";
    this.arguments = "@LobbyChannel";
    this.hidden = false;
    this.ownerCommand = true;
    this.guildOnly = true;
  }
  
  @Override
  protected void execute(CommandEvent event) {
    
    if(!event.getMessage().getMentionedChannels().isEmpty()) {
      GameData.setLobbyTextChannel(event.getMessage().getMentionedChannels().get(0));
      event.reply("Le channel a bien été défini !");
    }else {
      event.reply("Merci de mentionner un channel !");
    }
    
  }

}
