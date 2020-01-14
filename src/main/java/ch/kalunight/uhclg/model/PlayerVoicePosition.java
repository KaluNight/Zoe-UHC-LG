package ch.kalunight.uhclg.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.VoiceChannel;

public class PlayerVoicePosition {
  private PlayerData playerData;
  private final List<Long> usersIdNearPlayer = Collections.synchronizedList(new ArrayList<>());
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

  public List<Long> getUsersIdNearPlayer() {
    return usersIdNearPlayer;
  }


  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (int) (actualVoiceChannelId ^ (actualVoiceChannelId >>> 32));
    result = prime * result + ((playerData == null) ? 0 : playerData.hashCode());
    result = prime * result + ((usersIdNearPlayer == null) ? 0 : usersIdNearPlayer.hashCode());
    return result;
  }



  @Override
  public boolean equals(Object obj) {
    if(obj == null) {
      return false;
    }

    if (this.getClass() != obj.getClass()) {
      return false;
    }

    return playerData.getAccount().getDiscordId() == ((PlayerVoicePosition) obj).playerData.getAccount().getDiscordId();
  }

}
