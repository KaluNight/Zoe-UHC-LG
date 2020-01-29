package ch.kalunight.uhclg.model.role;

public abstract class SaviorRole {
  
  private boolean playerSaved = false;

  public boolean isPlayerSaved() {
    return playerSaved;
  }

  public void setPlayerSaved(boolean hasSaved) {
    this.playerSaved = hasSaved;
  }
}
