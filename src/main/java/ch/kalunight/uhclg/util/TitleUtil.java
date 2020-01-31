package ch.kalunight.uhclg.util;

import org.bukkit.entity.Player;

public class TitleUtil {

  private TitleUtil() {
    // hide default public constructor
  }
  
  public static void sendTitleToPlayer(Player player, String title, String littleText, int ticksDuration) {
    player.sendTitle(title, littleText, 10, ticksDuration, 10);
  }
}
