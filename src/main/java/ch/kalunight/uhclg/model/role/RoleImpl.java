package ch.kalunight.uhclg.model.role;

import ch.kalunight.uhclg.model.PlayerData;
import ch.kalunight.uhclg.model.TimeStatus;

public interface RoleImpl {
  
  public String getName();
  
  public String getDescription();
  
  public void giveItem(PlayerData player);
  
  public void givePotionEffect(PlayerData player, TimeStatus time);
  
}
