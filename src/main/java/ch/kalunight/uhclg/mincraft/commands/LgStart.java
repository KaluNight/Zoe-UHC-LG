package ch.kalunight.uhclg.mincraft.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.RenderType;
import org.bukkit.scoreboard.Scoreboard;

import ch.kalunight.uhclg.ZoePluginMaster;
import ch.kalunight.uhclg.model.GameStatus;

public class LgStart implements CommandExecutor {

  private static Scoreboard scoreBoard;
  
  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

    Server server = sender.getServer();

    server.broadcastMessage("The game start !");

    scoreBoard = sender.getServer().getScoreboardManager().getNewScoreboard();
    
    Objective objective = scoreBoard.registerNewObjective("Test", "Test", "Test");

    objective.setRenderType(RenderType.INTEGER);
    
    objective.setDisplaySlot(DisplaySlot.SIDEBAR);
    
    objective.getScore("Test");
    
    List<Player> players = new ArrayList<>();

    for(OfflinePlayer offlinePlayer : sender.getServer().getOfflinePlayers()) {
      if(server.getPlayer(offlinePlayer.getUniqueId()) != null) {
        players.add(server.getPlayer(offlinePlayer.getUniqueId()));
      }
    }
    
    for(Player player : players) {
      player.setScoreboard(scoreBoard);
    }
    
    ZoePluginMaster.setGameStatus(GameStatus.IN_GAME);
    
    return true;
  }

}
