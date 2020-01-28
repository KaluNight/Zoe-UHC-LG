package ch.kalunight.uhclg.util;

import java.util.ArrayList;
import java.util.List;

import ch.kalunight.uhclg.model.PlayerData;
import ch.kalunight.uhclg.model.Role;

public class RoleUtil {

  private RoleUtil() {
    // hide default public constructor
  }
  
  public static List<PlayerData> getPlayerByRole(List<PlayerData> players, Role role) {
    List<PlayerData> playersWithTheSameRole = new ArrayList<>();
    
    for(PlayerData player : players) {
      if(player.getRole().getRoleEnum().equals(role)) {
        playersWithTheSameRole.add(player);
      }
    }
    return playersWithTheSameRole;
  }
  
}
