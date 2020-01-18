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
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import ch.kalunight.uhclg.GameData;
import ch.kalunight.uhclg.ZoePluginMaster;
import ch.kalunight.uhclg.model.GameStatus;
import ch.kalunight.uhclg.model.LinkedDiscordAccount;
import ch.kalunight.uhclg.model.PlayerData;

public class ScoreboardWorker implements Runnable {

  private static final DateTimeFormatter realTimeFormatter = DateTimeFormatter.ofPattern("HH'h'mm");

  private static final String LOBBY_SCOREBOARD_ID = "scoreLobby";

  private static final String IN_GAME_OBJECTIVE_ID = "inGameObjective";

  private static final String DUMMY = "dummy";

  private static final List<Objective> playerLobbyListObjective = Collections.synchronizedList(new ArrayList<>());

  private Scoreboard lobbyScoreboard;

  private Scoreboard inGameScoreBoard;

  private Score joueurRestant;

  private Score villageoisRestant;

  private Score loupGarouRestant;

  private Score specialRestant;
  
  private Score groupMax;

  @Override
  public void run() {

    if(GameData.getGameStatus().equals(GameStatus.IN_GAME)) {
      defineInGameScoreBoard();
      for(PlayerData player : GameData.getPlayersInGame()) {
        if(player.isConnected()) {
          player.setScoreboard(inGameScoreBoard);
        }
      }
      ZoePluginMaster.getMinecraftServer().getOnlinePlayers().forEach(e -> e.setScoreboard(inGameScoreBoard));

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

  private void defineInGameScoreBoard() {
    if(inGameScoreBoard == null) {
      Scoreboard scoreBoard = ZoePluginMaster.getMinecraftServer().getScoreboardManager().getNewScoreboard();
      Objective objective = scoreBoard.registerNewObjective(IN_GAME_OBJECTIVE_ID, DUMMY, "", RenderType.INTEGER);
      objective.setDisplaySlot(DisplaySlot.SIDEBAR);
      inGameScoreBoard = scoreBoard;
    }

    Objective objective = inGameScoreBoard.getObjective(IN_GAME_OBJECTIVE_ID);
    objective.setDisplayName("Loup Garou - UHC | " + GameWorker.getChronoString());

    objective.getScore("Jour " + GameWorker.getActualDayNumber() + " | " + GameWorker.getTimeStatus().getName()).setScore(10);
    if(joueurRestant == null) {
      joueurRestant = objective.getScore("Joueurs restant : " + GameData.getPlayerAlive());
      joueurRestant.setScore(9);

      villageoisRestant = objective.getScore("Villageois : " + GameData.getVillagerAlive());
      villageoisRestant.setScore(6);

      loupGarouRestant = objective.getScore("Loups : " + GameData.getWolfAlive());
      loupGarouRestant.setScore(5);

      specialRestant = objective.getScore("Specials : " + GameData.getSpecialAlive());
      specialRestant.setScore(4);
      
      groupMax = objective.getScore("Taille de groupe maximal : " 
      + GameData.getGroupSize());
      groupMax.setScore(2);

    }else {
      inGameScoreBoard.resetScores(joueurRestant.getEntry());
      joueurRestant = objective.getScore("Joueurs restant : " + GameData.getPlayerAlive());
      joueurRestant.setScore(9);

      inGameScoreBoard.resetScores(villageoisRestant.getEntry());
      villageoisRestant = objective.getScore("Villageois : " + GameData.getVillagerAlive());
      villageoisRestant.setScore(6);

      inGameScoreBoard.resetScores(loupGarouRestant.getEntry());
      loupGarouRestant = objective.getScore("Loups : " + GameData.getWolfAlive());
      loupGarouRestant.setScore(5);

      inGameScoreBoard.resetScores(specialRestant.getEntry());
      specialRestant = objective.getScore("Spéciaux : " + GameData.getSpecialAlive());
      specialRestant.setScore(4);
      
      groupMax = objective.getScore("Taille de groupe maximal : " 
      + GameData.getGroupSize());
      groupMax.setScore(2);
    }

    objective.getScore("    ").setScore(3);
    objective.getScore("  ").setScore(8);
    objective.getScore("Rôles restants").setScore(7);
  }

}
