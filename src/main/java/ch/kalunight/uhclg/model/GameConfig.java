package ch.kalunight.uhclg.model;

import java.util.List;

public enum GameConfig {
  ONE_PLAYER(1, 1, 0, 0, 0),
  TWO_PLAYERS(2, 1, 0, 1, 0),
  THREE_PLAYERS(3, 2, 0, 1, 0),
  FOUR_PLAYERS(4, 2, 0, 2, 0),
  FIVE_PLAYERS(5, 1, 2, 2, 0),
  SIX_PLAYERS(6, 1, 2, 2, 1),
  SEVEN_PLAYERS(7, 1, 3, 2, 1),
  EIGHT_PLAYERS(8, 1, 3, 3, 1),
  NINE_PLAYERS(9, 1, 4, 3, 1),
  TEN_PLAYERS(10, 1, 4, 3, 2),
  ELEVEN_PLAYERS(11, 1, 5, 3, 2),
  TWELVE_PLAYERS(12, 1, 5, 4, 2),
  THIRTEEN_PLAYERS(13, 1, 6, 4, 2),
  FOURTEEN_PLAYERS(14, 1, 6, 4, 3),
  FIFTEEN_PLAYERS(15, 2, 6, 5, 2),
  SIXTEEN_PLAYERS(16, 2, 7, 5, 2),
  SEVENTEEN_PLAYERS(17, 2, 7, 6, 2),
  EIGHTEEN_PLAYERS(18, 2, 8, 6, 2),
  NINETEEN_PLAYERS(19, 2, 8, 7, 2),
  TWENTY_PLAYERS(20, 2, 8, 8, 2);
  
  private int playersNumber;
  private int villagersNumber;
  private int specialVillagersNumber;
  private int wolfsNumber;
  private int specialWolfsNumber;
  
  private GameConfig(int playersNumber, int villagersNumber, int specialVillagersNumber, int wolfsNumber, int specialWolfsNumber) {
    this.playersNumber = playersNumber;
    this.villagersNumber = villagersNumber;
    this.specialVillagersNumber = specialVillagersNumber;
    this.wolfsNumber = wolfsNumber;
    this.specialWolfsNumber = specialWolfsNumber;
  }
  
  public static GameConfig getGameConfigWithPlayerNumber(int playersNumber) {
    for(GameConfig gameConfig : GameConfig.values()) {
      if(gameConfig.playersNumber == playersNumber) {
        return gameConfig;
      }
    }
    return null;
  }

  public List<Role> getRoles(GameConfig config) {
    return Role.getRolesWithConfig(config);
  }
  
  public int getPlayersNumber() {
    return playersNumber;
  }

  public int getVillagersNumber() {
    return villagersNumber;
  }

  public int getSpecialVillagersNumber() {
    return specialVillagersNumber;
  }

  public int getWolfsNumber() {
    return wolfsNumber;
  }

  public int getSpecialWolfsNumber() {
    return specialWolfsNumber;
  }
  
  
}
