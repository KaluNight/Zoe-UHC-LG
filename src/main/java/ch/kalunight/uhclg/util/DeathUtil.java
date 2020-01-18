package ch.kalunight.uhclg.util;

import java.util.List;

import ch.kalunight.uhclg.GameData;
import ch.kalunight.uhclg.model.PlayerData;

public class DeathUtil {

  /*
   * Death time = 30 sec
   */
  public static final int DEATH_TIME_IN_TICKS = 600;
  
  private DeathUtil() {
    // hide default public constructor
  }
  
  public static PlayerData getAviableSavior(List<PlayerData> playerAlreadyAsked) {
    for(PlayerData playerData : GameData.getPlayersInGame()) {
      if(!playerAlreadyAsked.contains(playerData)) {
        return playerData;
      }
    }
    
    return null;
  }
  
}
