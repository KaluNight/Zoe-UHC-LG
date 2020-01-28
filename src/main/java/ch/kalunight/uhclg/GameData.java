package ch.kalunight.uhclg;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.bukkit.Location;
import ch.kalunight.uhclg.model.GameStatus;
import ch.kalunight.uhclg.model.LinkedDiscordAccount;
import ch.kalunight.uhclg.model.PlayerData;
import ch.kalunight.uhclg.model.RoleClan;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;

public class GameData {

  private static GameStatus gameStatus;
  
  private static final List<LinkedDiscordAccount> playersRegistered = Collections.synchronizedList(new ArrayList<>());
  
  private static final List<PlayerData> playersInGame = Collections.synchronizedList(new ArrayList<>());
  
  private static VoiceChannel lobbyVoiceChannel;
  
  private static TextChannel lobbyTextChannel;
  
  private static Location lobbyLocation;
  
  private static double spawnMinDistance = 100;
  
  private GameData() {
    //hide default public constructor
  }
  
  public static int getGroupSize() {
    
    int wolfsAlive = getWolfAlive();
    int villagersAlive = getVillagerAlive();
    
    if(wolfsAlive < 3 || villagersAlive < 3) {
      return 3;
    }
    
    if(wolfsAlive > 5 || villagersAlive > 5) {
      return 5;
    }
    
    if(wolfsAlive > villagersAlive) {
      return villagersAlive;
    }else {
      return wolfsAlive;
    }
  }
  
  public static int getPlayerAlive() {
    int playerAlive = 0;
    for(PlayerData player : playersInGame) {
      if(player.isAlive()) {
        playerAlive++;
      }
    }
    return playerAlive;
  }
  
  public static int getWolfAlive() {
    int wolfAlive = 0;
    for(PlayerData player : playersInGame) {
      if(player.getRole().getRoleEnum().getClan().equals(RoleClan.WOLFS) && player.isAlive()) {
        wolfAlive++;
      }
    }
    return wolfAlive;
  }
  
  public static int getVillagerAlive() {
    int villagersAlive = 0;
    for(PlayerData player : playersInGame) {
      if(player.getRole().getRoleEnum().getClan().equals(RoleClan.VILLAGE) && player.isAlive()) {
        villagersAlive++;
      }
    }
    return villagersAlive;
  }
  
  public static int getSpecialAlive() {
    int specialAlive = 0;
    for(PlayerData player : playersInGame) {
      if(player.getRole().getRoleEnum().getClan().equals(RoleClan.SPECIAL) && player.isAlive()) {
        specialAlive++;
      }
    }
    return specialAlive;
  }
  
  public static PlayerData getPlayerInGame(UUID uuid) {
    for(PlayerData player : playersInGame) {
      if(player.getAccount().getPlayerUUID().equals(uuid)) {
        return player;
      }
    }
    return null;
  }
  
  public static PlayerData getPlayerInGame(long discordID) {
    for(PlayerData player : playersInGame) {
      if(player.getAccount().getDiscordId() == discordID) {
        return player;
      }
    }
    return null;
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

  public static double getSpawnMinBlockDistance() {
    return spawnMinDistance;
  }

  public static void setSpawnMinBlockDistance(double spawnMinBlockDistance) {
    GameData.spawnMinDistance = spawnMinBlockDistance;
  }
}
