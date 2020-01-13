package ch.kalunight.uhclg.worker;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.RenderType;
import org.bukkit.scoreboard.Scoreboard;
import ch.kalunight.uhclg.GameData;
import ch.kalunight.uhclg.ZoePluginMaster;
import ch.kalunight.uhclg.model.GameStatus;
import ch.kalunight.uhclg.model.LinkedDiscordAccount;
import ch.kalunight.uhclg.model.PlayerData;

public class ScoreboardWorker implements Runnable {

  private static final DateTimeFormatter realTimeFormatter = DateTimeFormatter.ofPattern("HH'h'mm");

  private static final String LOBBY_SCOREBOARD_ID = "scoreLobby";

  private static final String DUMMY = "dummy";

  private static final List<Objective> playerLobbyListObjective = Collections.synchronizedList(new ArrayList<>());

  private Scoreboard lobbyScoreboard;

  private Scoreboard wolfsScoreBoard;

  private Scoreboard villagersScoreBoard;

  @Override
  public void run() {

    if(GameData.getGameStatus().equals(GameStatus.IN_GAME)) {
      for(PlayerData player : GameData.getPlayersInGame()) {

        //defineInGameScoreBoard(player);
      }
    }else if(GameData.getGameStatus().equals(GameStatus.IN_LOBBY)) {

      updateLobbyScoreBoard();
      for(Player player : ZoePluginMaster.getMinecraftServer().getOnlinePlayers()) {

        defineInLobbyScoreBoard(player);
      }
    }
  }

  private void updateLobbyScoreBoard() {
    if(lobbyScoreboard == null) {
      Scoreboard scoreboard = ZoePluginMaster.getMinecraftServer().getScoreboardManager().getNewScoreboard();

      lobbyScoreboard = scoreboard;

      lobbyScoreboard.registerNewObjective(LOBBY_SCOREBOARD_ID, DUMMY, "", RenderType.INTEGER).setDisplaySlot(DisplaySlot.SIDEBAR);
    }

    lobbyScoreboard.getObjective(LOBBY_SCOREBOARD_ID).setDisplayName("Loup Garou - UHC | " 
        + realTimeFormatter.format(LocalDateTime.now()));
    
    lobbyScoreboard.getObjective(LOBBY_SCOREBOARD_ID).getScore("Nombre joueurs :").setScore(GameData.getPlayersRegistered().size());
    
    playerLobbyListObjective.clear();
    
    int i = GameData.getPlayersRegistered().size();
    for(LinkedDiscordAccount playerRegistered : GameData.getPlayersRegistered()) {
      Player player = playerRegistered.getPlayer();

      i--;
      lobbyScoreboard.getObjective(LOBBY_SCOREBOARD_ID).getScore(player.getName()).setScore(i);
    }

  }

  private void defineInLobbyScoreBoard(Player player) {
    player.setScoreboard(lobbyScoreboard);
  }

  private void defineInGameScoreBoard(PlayerData player) {
    if(player.getScoreboard() == null) {
      Scoreboard scoreBoard = ZoePluginMaster.getMinecraftServer().getScoreboardManager().getNewScoreboard();
      player.getAccount().getPlayer().setScoreboard(scoreBoard);
      player.setScoreboard(scoreBoard);
    }
  }

  public void setupInGameScoreBoard(Player player) {
    if(player.getScoreboard() == null) {
      Scoreboard scoreBoard = ZoePluginMaster.getMinecraftServer().getScoreboardManager().getNewScoreboard();
      player.setScoreboard(scoreBoard);
    }

  }

}
