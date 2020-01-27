package ch.kalunight.uhclg.worker;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Location;
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

  private Map<UUID, Scoreboard> inGameScoreboards = Collections.synchronizedMap(new HashMap<>());

  private Map<UUID, String> scorePostions = Collections.synchronizedMap(new HashMap<>());

  private Map<UUID, String> dayStatus = Collections.synchronizedMap(new HashMap<>());

  private Map<UUID, String> joueurRestant = Collections.synchronizedMap(new HashMap<>());

  private Map<UUID, String> villageoisRestant = Collections.synchronizedMap(new HashMap<>());

  private Map<UUID, String> loupGarouRestant = Collections.synchronizedMap(new HashMap<>());

  private Map<UUID, String> specialRestant = Collections.synchronizedMap(new HashMap<>());

  private Map<UUID, String> groupMax = Collections.synchronizedMap(new HashMap<>());

  @Override
  public void run() {

    if(GameData.getGameStatus().equals(GameStatus.IN_GAME)) {
      defineInGameScoreBoard();
      for(PlayerData player : GameData.getPlayersInGame()) {
        if(player.isConnected()) {
          Scoreboard inGameScoreBoard = inGameScoreboards.get(player.getAccount().getPlayerUUID());

          if(inGameScoreBoard != null && !inGameScoreBoard.equals(player.getAccount().getPlayer().getScoreboard())) {
            player.setScoreboard(inGameScoreBoard);
            player.getAccount().getPlayer().setScoreboard(inGameScoreBoard);
          }
        }
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

  private void defineInGameScoreBoard() {

    String actualDayStatus = getActualDayStatusScore();
    String playerAlive = getPlayerAliveScore();
    String villagerAlive = getVillagerAliveScore();
    String wolfsAlive = getWolfsAliveScore();
    String specialAlive = getSpecialAliveScore();
    String groupSize = getGroupMaxScore();

    for(PlayerData player : GameData.getPlayersInGame()) {

      if(!player.isConnected()) {
        continue;
      }

      Scoreboard inGameScoreBoard = inGameScoreboards.get(player.getAccount().getPlayerUUID());

      if(inGameScoreBoard == null) {
        Scoreboard scoreBoard = ZoePluginMaster.getMinecraftServer().getScoreboardManager().getNewScoreboard();
        Objective objective = scoreBoard.registerNewObjective(IN_GAME_OBJECTIVE_ID, DUMMY, "", RenderType.INTEGER);
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        inGameScoreBoard = scoreBoard;
      }

      UUID playerUUID = player.getAccount().getPlayerUUID();

      Objective objective = inGameScoreBoard.getObjective(IN_GAME_OBJECTIVE_ID);
      objective.setDisplayName("Loup Garou - UHC | " + GameWorker.getChronoString());

      if(joueurRestant.get(playerUUID) == null) {
        Score dayStatusScore = objective.getScore(actualDayStatus);
        dayStatusScore.setScore(10);
        dayStatus.put(playerUUID, dayStatusScore.getEntry());

        Score joueurRestantScore = objective.getScore(playerAlive);
        joueurRestantScore.setScore(9);
        joueurRestant.put(playerUUID, joueurRestantScore.getEntry());

        Score villageoisRestantScore = objective.getScore(villagerAlive);
        villageoisRestantScore.setScore(6);
        villageoisRestant.put(playerUUID, villageoisRestantScore.getEntry());

        Score loupGarouRestantScore = objective.getScore(wolfsAlive);
        loupGarouRestantScore.setScore(5);
        loupGarouRestant.put(playerUUID, loupGarouRestantScore.getEntry());

        Score specialRestantScore = objective.getScore(specialAlive);
        specialRestantScore.setScore(4);
        specialRestant.put(playerUUID, specialRestantScore.getEntry());

        Score groupMaxScore = objective.getScore(groupSize);
        groupMaxScore.setScore(2);
        groupMax.put(playerUUID, groupMaxScore.getEntry());

      }else {

        String dayStatusPlayer = dayStatus.get(playerUUID);
        if(!dayStatusPlayer.equals(actualDayStatus)) {
          inGameScoreBoard.resetScores(dayStatusPlayer);
          Score dayStatusScore = objective.getScore(actualDayStatus);
          dayStatusScore.setScore(10);
          dayStatus.put(playerUUID, actualDayStatus);
        }

        String joueurRestantPlayer = joueurRestant.get(playerUUID);
        if(!joueurRestantPlayer.equals(playerAlive)) {
          inGameScoreBoard.resetScores(joueurRestantPlayer);
          Score joueurRestantScore = objective.getScore(playerAlive);
          joueurRestantScore.setScore(9);
          joueurRestant.put(playerUUID, playerAlive);
        }

        String villageoisRestantPlayer = villageoisRestant.get(playerUUID);
        if(!villageoisRestantPlayer.equals(villagerAlive)) {
          inGameScoreBoard.resetScores(villageoisRestantPlayer);
          Score villageoisRestantScore = objective.getScore(villagerAlive);
          villageoisRestantScore.setScore(6);
          villageoisRestant.put(playerUUID, villagerAlive);
        }

        String loupGarouRestantPlayer = loupGarouRestant.get(playerUUID);
        if(!loupGarouRestantPlayer.equals(wolfsAlive)) {
          inGameScoreBoard.resetScores(loupGarouRestantPlayer);
          Score loupGarouRestantScore = objective.getScore(wolfsAlive);
          loupGarouRestantScore.setScore(5);
          loupGarouRestant.put(playerUUID, wolfsAlive);
        }

        String specialRestantPlayer = specialRestant.get(playerUUID);
        if(!specialRestantPlayer.equals(specialAlive)) {
          inGameScoreBoard.resetScores(specialRestantPlayer);
          Score specialRestantScore = objective.getScore(specialAlive);
          specialRestantScore.setScore(4);
          specialRestant.put(playerUUID, specialAlive);
        }

        String groupMaxPlayer = groupMax.get(playerUUID);
        if(!groupMaxPlayer.equals(groupSize)) {
          inGameScoreBoard.resetScores(groupMaxPlayer);
          Score groupMaxScore = objective.getScore(groupSize);
          groupMaxScore.setScore(2);
          groupMax.put(playerUUID, groupSize);
        }
      }

      objective.getScore("---------------").setScore(3);
      objective.getScore("  ").setScore(8);
      objective.getScore("Rôles restants").setScore(7);


      String positionText = scorePostions.get(player.getAccount().getPlayerUUID());

      if(positionText != null) {
        inGameScoreBoard.resetScores(positionText);
      }

      Location lobbyLocation = GameData.getLobbyLocation();
      Location playerLocation = player.getAccount().getPlayer().getLocation();

      final Location addaptedLobbyLocation = 
          new Location(ZoePluginMaster.getMinecraftServer().getWorld("world"),
              lobbyLocation.getX(), playerLocation.getY(), lobbyLocation.getZ());

      int distanceOfCenter = (int) playerLocation.distance(addaptedLobbyLocation);

      positionText = "Centre : " + distanceOfCenter + " blocs";
      objective.getScore(positionText).setScore(2);

      scorePostions.put(player.getAccount().getPlayerUUID(), positionText);
      inGameScoreboards.put(player.getAccount().getPlayerUUID(), inGameScoreBoard);
    }
  }

  private String getGroupMaxScore() {
    return "Taille groupe max : " + GameData.getGroupSize();
  }

  private String getSpecialAliveScore() {
    return "Spéciaux : " + GameData.getSpecialAlive();
  }

  private String getWolfsAliveScore() {
    return "Loups : " + GameData.getWolfAlive();
  }

  private String getVillagerAliveScore() {
    return "Villageois : " + GameData.getVillagerAlive();
  }

  private String getPlayerAliveScore() {
    return "Joueurs restant : " + GameData.getPlayerAlive();
  }

  private String getActualDayStatusScore() {
    return "Jour " + GameWorker.getActualDayNumber() + " | " + GameWorker.getTimeStatus().getName();
  }
}
