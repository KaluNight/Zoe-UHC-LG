package ch.kalunight.uhclg.model;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.VoiceChannel;

public class PlayerVoicePosition {
  private PlayerData playerData;
  private long actualVoiceChannelId;
  
  public PlayerVoicePosition(PlayerData playerData, long actualVoiceChannelId) {
    this.playerData = playerData;
    this.actualVoiceChannelId = actualVoiceChannelId;
  }
  
  public PlayerData getPlayerData() {
    return playerData;
  }
  
  public void setPlayerData(PlayerData playerData) {
    this.playerData = playerData;
  }
  
  public long getActualVoiceChannelId() {
    return actualVoiceChannelId;
  }
  
  public VoiceChannel getActualVoiceChannel(Guild guild) {
    return guild.getVoiceChannelById(actualVoiceChannelId);
  }
  
  public void setActualVoiceChannelId(long actualVoiceChannelId) {
    this.actualVoiceChannelId = actualVoiceChannelId;
  }
}
