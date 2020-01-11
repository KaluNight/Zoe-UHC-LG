package ch.kalunight.uhclg.worker;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.RenderType;
import org.bukkit.scoreboard.Scoreboard;

import ch.kalunight.uhclg.ZoePluginMaster;

public class ScoreboardWorker implements Runnable {

  @Override
  public void run() {
    
    Scoreboard scoreBoard = ZoePluginMaster.getMinecraftServer().getScoreboardManager().getNewScoreboard();
    
    Objective objective = scoreBoard.registerNewObjective("Test", "Test", "Test");

    objective.setRenderType(RenderType.INTEGER);
    
    objective.setDisplaySlot(DisplaySlot.SIDEBAR);
    
    objective.getScore("Test");
    
    List<Player> players = new ArrayList<>();

    for(OfflinePlayer offlinePlayer : ZoePluginMaster.getMinecraftServer().getOfflinePlayers()) {
      if(ZoePluginMaster.getMinecraftServer().getPlayer(offlinePlayer.getUniqueId()) != null) {
        players.add(ZoePluginMaster.getMinecraftServer().getPlayer(offlinePlayer.getUniqueId()));
      }
    }
    
    for(Player player : players) {
      player.setScoreboard(scoreBoard);
    }
  }

}
