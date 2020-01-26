package ch.kalunight.uhclg.util;

import java.util.UUID;

import ch.kalunight.uhclg.GameData;
import ch.kalunight.uhclg.model.PlayerData;

public class LoverUtil {

  private LoverUtil() {
    // hide default public constructor
  }
  
  public static PlayerData getOtherLover(UUID playerUUID) {
    PlayerData otherLover = null;

    for(PlayerData player : GameData.getPlayersInGame()) {
      if(player.isAlive() && !player.getAccount().getPlayerUUID().equals(playerUUID) && player.isInLove()) {
        otherLover = player;
        break;
      }
    }

    return otherLover;
  }

  public static boolean isLoverAndOtherLoverDead(PlayerData player) {
    UUID playerUUID = player.getAccount().getPlayerUUID();
    PlayerData lover = LoverUtil.getOtherLover(playerUUID);

    return player.isInLove() && lover != null && !lover.isAlive();
  }
  
}
