package ch.kalunight.uhclg.model;

public enum GroupConfig {
  ONE_PLAYER(1, 1),
  TWO_PLAYERS(2, 2),
  THREE_PLAYERS(3, 2),
  FOUR_PLAYERS(4, 2),
  FIVE_PLAYERS(5, 2),
  SIX_PLAYERS(6, 3),
  SEVEN_PLAYERS(7, 3),
  EIGHT_PLAYERS(8, 3),
  NINE_PLAYERS(9, 3),
  TEN_PLAYERS(10, 4),
  ELEVEN_PLAYERS(11, 4),
  TWELVE_PLAYERS(12, 4),
  THIRTEEN_PLAYERS(13, 4),
  FOURTEEN_PLAYERS(14, 4),
  FIFTEEN_PLAYERS(15, 5),
  SIXTEEN_PLAYERS(16, 5),
  SEVENTEEN_PLAYERS(17, 5),
  EIGHTEEN_PLAYERS(18, 5),
  NINETEEN_PLAYERS(19, 5),
  TWENTY_PLAYERS(20, 5);
  
  private int nbrPlayers;
  private int maxGroup;
  
  private GroupConfig(int nbrPlayer, int maxPlayersInGroup) {
    this.nbrPlayers = nbrPlayer;
    this.maxGroup = maxPlayersInGroup;
  }

  public int getNbrPlayers() {
    return nbrPlayers;
  }

  public int getMaxGroup() {
    return maxGroup;
  }
  
  public static GroupConfig getGroupConfig(int playersAlive) {
    for(GroupConfig groupConfig : GroupConfig.values()) {
      if(groupConfig.getNbrPlayers() == playersAlive) {
        return groupConfig;
      }
    }
    return null;
  }
  
}
