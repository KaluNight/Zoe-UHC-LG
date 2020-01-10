package ch.kalunight.uhclg.discord.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import ch.kalunight.uhclg.ZoePluginMaster;
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
    
    for(LinkedDiscordAccount account : ZoePluginMaster.getPlayersRegistered()) {
      if(account.getDiscordId() == event.getAuthor().getLongId()) {
        accountToDelete = account;
      }
    }
    
    if(accountToDelete != null) {
      ZoePluginMaster.getPlayersRegistered().remove(accountToDelete);
      event.reply("Votre compte a bien été delié");
    } else {
      event.reply("Vous n'avez jamais été enregistré !");
    }
  }

}
