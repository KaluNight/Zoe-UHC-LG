package ch.kalunight.uhclg.discord.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import ch.kalunight.uhclg.GameData;
import ch.kalunight.uhclg.model.LinkedDiscordAccount;

public class UnlinkCommand extends Command {

  public UnlinkCommand() {
    this.name = "unlink";
    this.arguments = "";
    this.hidden = false;
    this.ownerCommand = false;
    this.guildOnly = true;
  }
  
  @Override
  protected void execute(CommandEvent event) {
    LinkedDiscordAccount accountToDelete = null;
    
    for(LinkedDiscordAccount account : GameData.getPlayersRegistered()) {
      if(account.getDiscordId() == event.getAuthor().getIdLong()) {
        accountToDelete = account;
      }
    }
    
    if(accountToDelete != null) {
      GameData.getPlayersRegistered().remove(accountToDelete);
      event.reply("Votre compte a bien été delié");
    } else {
      event.reply("Vous n'avez jamais été enregistré !");
    }
  }

}
