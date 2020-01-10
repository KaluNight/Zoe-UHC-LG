package ch.kalunight.uhclg.model;

import java.util.UUID;

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

  public void setDiscordId(long discordId) {
    this.discordId = discordId;
  }

  public UUID getPlayerUUID() {
    return playerUUID;
  }

  public void setPlayerUUID(UUID playerUUID) {
    this.playerUUID = playerUUID;
  }

}
