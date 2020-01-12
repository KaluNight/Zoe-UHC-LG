package ch.kalunight.uhclg.model;

import java.util.UUID;

import org.bukkit.entity.Player;

import ch.kalunight.uhclg.ZoePluginMaster;
import net.dv8tion.jda.api.entities.User;

public class LinkedDiscordAccount {

  private long discordId;
  private UUID playerUUID;
  
  public LinkedDiscordAccount(long discordId, UUID playerUUID) {
    this.discordId = discordId;
    this.playerUUID = playerUUID;
  }
  
  public long getDiscordId() {
    return discordId;
  }

  public User getDiscordUser() {
    return ZoePluginMaster.getJda().retrieveUserById(discordId).complete();
  }

  public void setDiscordId(long discordId) {
    this.discordId = discordId;
  }

  public UUID getPlayerUUID() {
    return playerUUID;
  }
  
  public Player getPlayer() {
    return ZoePluginMaster.getMinecraftServer().getPlayer(playerUUID);
  }

  public void setPlayerUUID(UUID playerUUID) {
    this.playerUUID = playerUUID;
  }

}
