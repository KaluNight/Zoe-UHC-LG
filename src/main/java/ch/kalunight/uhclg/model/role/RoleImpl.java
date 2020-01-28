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
  
  public static RoleImpl getRole(Role role) {
    switch(role) {
      case ANCIEN:
        return new Ancien();
      case ASSASSIN:
        return new Assassin();
      case CUPIDON:
        return new Cupidon();
      case ENFANT_SAUVAGE:
        return new EnfantSauvage();
      case GRAND_MERE_LOUP:
        return new GrandMereLoup();
      case INFECT_PERE_DES_LOUPS:
        return new InfectPereDesLoups();
      case LOUP_GAROU:
        return new LoupGarou();
      case LOUP_GAROU_AMNESIQUE:
        return new LoupGarouAmnesique();
      case LOUP_GAROU_BLANC:
        return new LoupGarouBlanc();
      case PETITE_FILLE:
        return new PetiteFille();
      case PETIT_LOUP_GAROU:
        return new PetitLoupGarou();
      case RENARD:
        return new Renard();
      case SORCIERE:
        return new Sorciere();
      case VILLAGEOIS:
        return new Villageois();
      case VOYANTE:
        return new Voyante();
      default:
        return null;
    }
  }
  
}
