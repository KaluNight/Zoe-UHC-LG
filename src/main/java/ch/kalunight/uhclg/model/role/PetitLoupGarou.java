package ch.kalunight.uhclg.model.role;

import java.io.File;
import ch.kalunight.uhclg.model.PlayerData;
import ch.kalunight.uhclg.model.Role;
import ch.kalunight.uhclg.model.TimeStatus;
import ch.kalunight.uhclg.util.PotionUtil;

public class PetitLoupGarou implements RoleImpl {

  private static final File annonceVoiceFile = new File(Role.SOUNDS_FOLDER + "petit_loup_garou.wav");
  
  @Override
  public Role getRoleEnum() {
    return Role.PETIT_LOUP_GAROU;
  }

  @Override
  public String getName() {
    return "Vilain Petit Loup";
  }

  @Override
  public String getDescription() {
    return "Vilain Petit Loup : il dispose de l’effet speed 1 "
        + "la nuit ainsi que des pouvoirs classiques des loups. il doit gagner avec les autres loups";
  }

  @Override
  public File getVoiceFile() {
    return annonceVoiceFile;
  }

  @Override
  public String getVoicePath() {
    return annonceVoiceFile.getAbsolutePath();
  }

  @Override
  public void giveRoleEffectAndItem(PlayerData player) {
    //nothing
  }

  @Override
  public void givePotionEffect(PlayerData player, TimeStatus time) {
    if(time.equals(TimeStatus.NIGHT)) {
      PotionUtil.giveSpeed(player);
    }else {
      PotionUtil.clearSpeed(player);
    }
    RoleImpl.giveWolfsEffects(player, time);
  }

}
