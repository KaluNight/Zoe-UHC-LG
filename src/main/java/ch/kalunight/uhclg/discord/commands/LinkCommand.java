package ch.kalunight.uhclg.discord.commands;

import org.bukkit.entity.Player;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import ch.kalunight.uhclg.GameData;
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

    for(LinkedDiscordAccount linkedDiscord : GameData.getPlayersRegistered()) {
      if(ZoePluginMaster.getMinecraftServer().getOfflinePlayer(linkedDiscord.getPlayerUUID()).getName() != null) {
        event.reply("Ce compte est déjà lié avec le joueur " 
            + ZoePluginMaster.getJda().retrieveUserById(linkedDiscord.getDiscordId()).complete().getName() + ".");
        return;
      }
    }

    Player player = null;

    for(Player onlinePlayer : ZoePluginMaster.getMinecraftServer().getOnlinePlayers()) {
      if(onlinePlayer.getName().equalsIgnoreCase(event.getArgs())) {
        player = onlinePlayer;
      }
    }

    if(player != null) {
      GameData.getPlayersRegistered().add(new LinkedDiscordAccount(event.getAuthor().getIdLong(), player.getUniqueId()));
      event.reply("Vous avez été enregistré avec le compte \"" + player.getName() + "\".");
    }else {
      event.reply("Aucun joueur n'a été trouvé, vous avez besoin d'être connecté sur le serveur pour faire cette commande");
    }
  }

}
