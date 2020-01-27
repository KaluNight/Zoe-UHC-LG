package ch.kalunight.uhclg.model;

import java.io.File;

public class VoiceRequest {

  private long guildId;
  private long userId;
  private File musicToPlayer;
  private boolean needToBeAlone;
  
  public VoiceRequest(long guildId, long userId, File musicToPlayer, boolean needToBeAlone){
    this.guildId = guildId;
    this.userId = userId;
    this.musicToPlayer = musicToPlayer;
    this.needToBeAlone = needToBeAlone;
  }

  public long getGuildId() {
    return guildId;
  }

  public void setGuildId(long guildId) {
    this.guildId = guildId;
  }

  public File getMusicToPlay() {
    return musicToPlayer;
  }

  public void setMusicToPlayer(File musicToPlayer) {
    this.musicToPlayer = musicToPlayer;
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