package ch.kalunight.uhclg;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Location;

import ch.kalunight.uhclg.model.GameStatus;
import ch.kalunight.uhclg.model.LinkedDiscordAccount;
import ch.kalunight.uhclg.model.PlayerData;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;

public class GameData {

  private static GameStatus gameStatus;
  
  private static final List<LinkedDiscordAccount> playersRegistered = Collections.synchronizedList(new ArrayList<>());
  
  private static final List<PlayerData> playersInGame = Collections.synchronizedList(new ArrayList<>());
  
  private static VoiceChannel lobbyVoiceChannel;
  
  private static TextChannel lobbyTextChannel;
  
  private static Location lobbyLocation;
  
  private static int baseWorldBorderSize = 3000;
  
  private static double spawnMinBlockDistance = 100;

  private GameData() {
    //hide default public constructor
  }
  
  public static GameStatus getGameStatus() {
    return gameStatus;
  }

  public static void setGameStatus(GameStatus gameStatus) {
    GameData.gameStatus = gameStatus;
  }

  public static VoiceChannel getLobby() {
    return lobbyVoiceChannel;
  }

  public static void setLobby(VoiceChannel lobby) {
    GameData.lobbyVoiceChannel = lobby;
  }

  public static Location getLobbyLocation() {
    return lobbyLocation;
  }

  public static void setLobbyLocation(Location lobbyLocation) {
    GameData.lobbyLocation = lobbyLocation;
  }

  public static List<LinkedDiscordAccount> getPlayersRegistered() {
    return playersRegistered;
  }

  public static TextChannel getLobbyTextChannel() {
    return lobbyTextChannel;
  }

  public static void setLobbyTextChannel(TextChannel lobbyTextChannel) {
    GameData.lobbyTextChannel = lobbyTextChannel;
  }

  public static List<PlayerData> getPlayersInGame() {
    return playersInGame;
  }

  public static int getBaseWorldBorderSize() {
    return baseWorldBorderSize;
  }

  public static void setBaseWorldBorderSize(int baseWorldBorderSize) {
    GameData.baseWorldBorderSize = baseWorldBorderSize;
  }

  public static double getSpawnMinBlockDistance() {
    return spawnMinBlockDistance;
  }

  public static void setSpawnMinBlockDistance(double spawnMinBlockDistance) {
    GameData.spawnMinBlockDistance = spawnMinBlockDistance;
  }
  
}
