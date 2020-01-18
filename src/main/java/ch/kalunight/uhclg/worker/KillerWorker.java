package ch.kalunight.uhclg.worker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.kalunight.uhclg.model.PlayerData;

public class KillerWorker implements Runnable {
  
  private final List<PlayerData> saviorAlreadyAsked = new ArrayList<>();
  
  private PlayerData playerKilled;
  
  private PlayerData potentialSavior;
  
  private boolean hasBeenSaved;
  
  public KillerWorker(PlayerData playerKilled, PlayerData potentialSavior) {
    this.playerKilled = playerKilled;
    this.potentialSavior = potentialSavior;
  }
  
  @Override
  public void run() {
    
    if(hasBeenSaved) {
      
    }
    
  }

  public boolean isHasBeenSaved() {
    return hasBeenSaved;
  }

  public void setHasBeenSaved(boolean hasBeenSaved) {
    this.hasBeenSaved = hasBeenSaved;
  }

  public PlayerData getPlayerKilled() {
    return playerKilled;
  }

  public PlayerData getPotentialSavior() {
    return potentialSavior;
  }
  
}
