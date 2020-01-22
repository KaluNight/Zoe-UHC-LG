package ch.kalunight.uhclg.worker;

import ch.kalunight.uhclg.model.PlayerData;

public class LoveKillerWorker implements Runnable {

  private PlayerData loverToKill;
  
  public LoveKillerWorker(PlayerData loverToKill) {
    this.loverToKill = loverToKill;
  }
  
  @Override
  public void run() {
    loverToKill.getAccount().getPlayer().damage(9999999);
  }

}
