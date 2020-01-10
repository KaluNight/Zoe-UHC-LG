package ch.kalunight.uhclg.discord.commands;

import org.bukkit.entity.Player;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import ch.kalunight.uhclg.ZoePluginMaster;
import ch.kalunight.uhclg.model.LinkedDiscordAccount;

public class LinkCommand extends Command {

  public LinkCommand() {
    this.name = "link";
    this.arguments = "Pseudo Minecraft";
    this.hidden = false;
    this.ownerCommand = false;
    this.guildOnly = true;
  }

  @Override
  protected void execute(CommandEvent event) {

    Player player = null;

    for(Player onlinePlayer : ZoePluginMaster.getMinecraftServer().getOnlinePlayers()) {
      if(onlinePlayer.getName().equalsIgnoreCase(event.getArgs())) {
        onlinePlayer = player;
      }
    }

    if(player != null) {
      ZoePluginMaster.getPlayersRegistered().add(new LinkedDiscordAccount(event.getAuthor().getId(), player.getUniqueId()));
      event.reply("Vous avez été enregistré avec le compte \"" + player.getName() + "\".");
    }else {
      event.reply("Aucun joueur n'a été trouvé, vous avez besoin d'être connecté sur le serveur pour faire cette commande");
    }
  }

}
