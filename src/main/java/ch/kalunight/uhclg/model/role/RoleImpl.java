package ch.kalunight.uhclg.model.role;

import java.io.File;
import ch.kalunight.uhclg.model.PlayerData;
import ch.kalunight.uhclg.model.Role;
import ch.kalunight.uhclg.model.TimeStatus;
import ch.kalunight.uhclg.util.PotionUtil;

public interface RoleImpl {
  
  public Role getRole();
  
  public String getName();
  
  public String getDescription();
  
  public File getVoiceFile();
  
  public String getVoicePath();
  
  public void giveRoleEffectAndItem(PlayerData player);
  
  public void givePotionEffect(PlayerData player, TimeStatus time);
  
  public static void giveWolfsEffects(PlayerData player, TimeStatus time) {
    if(time.equals(TimeStatus.NIGHT)) {
      PotionUtil.giveNightVision(player);
      PotionUtil.giveIncreaseDamage(player);
    } else {
      PotionUtil.clearNightVision(player);
      PotionUtil.clearIncreaseDamage(player);
    }
  }
  
}
