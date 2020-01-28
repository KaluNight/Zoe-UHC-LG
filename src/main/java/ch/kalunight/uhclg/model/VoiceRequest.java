package ch.kalunight.uhclg.model;

import java.io.File;

public class VoiceRequest {

  private long guildId;
  private long userId;
  private String musicToPlayURL;
  private boolean needToBeAlone;
  
  public VoiceRequest(long guildId, long userId, String musicToPlayURL, boolean needToBeAlone){
    this.guildId = guildId;
    this.userId = userId;
    this.musicToPlayURL = musicToPlayURL;
    this.needToBeAlone = needToBeAlone;
  }

  public long getGuildId() {
    return guildId;
  }

  public void setGuildId(long guildId) {
    this.guildId = guildId;
  }

  public String getMusicToPlay() {
    return musicToPlayURL;
  }

  public void setMusicToPlayer(String musicToPlayURL) {
    this.musicToPlayURL = musicToPlayURL;
  }

  public boolean isNeedToBeAlone() {
    return needToBeAlone;
  }

  public void setNeedToBeAlone(boolean needToBeAlone) {
    this.needToBeAlone = needToBeAlone;
  }

  public long getUserId() {
    return userId;
  }

  public void setUserId(long userId) {
    this.userId = userId;
  }
}