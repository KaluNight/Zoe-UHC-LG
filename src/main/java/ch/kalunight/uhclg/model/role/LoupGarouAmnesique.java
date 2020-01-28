package ch.kalunight.uhclg.model.role;

import java.io.File;
import ch.kalunight.uhclg.model.PlayerData;
import ch.kalunight.uhclg.model.Role;
import ch.kalunight.uhclg.model.TimeStatus;
import ch.kalunight.uhclg.util.PotionUtil;

public class LoupGarouAmnesique implements RoleImpl {

  private static final File annonceVoiceFile = new File(Role.SOUNDS_FOLDER + "villageois.wav");

  private boolean hasBeenFound = false;

  @Override
  public Role getRoleEnum() {
    return Role.LOUP_GAROU_AMNESIQUE;
  }

  @Override
  public String getName() {
    if(hasBeenFound) {
      return "Loup Garou Amnésique";
    }
    return "Villageois";
  }

  @Override
  public String getDescription() {

    if(hasBeenFound) {
      return "Loup-Garou Amnésique : il est désigné Simple Villageois à l’annonce des rôles. "
          + "Quand il est frappé par un loup-garou, un message lui apparaît lui annonçant son "
          + "rôle loup-garou. Ses bonus de loup lui apparaissent donc et il reçoit la liste complète des pseudos des loups.";
    }

    return RoleImpl.getRole(Role.VILLAGEOIS).getDescription();
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
    if(hasBeenFound && time.equals(TimeStatus.NIGHT)) {
      PotionUtil.giveSpeed(player);
    }else {
      PotionUtil.clearSpeed(player);
    }
  }

  public boolean isHasBeenFound() {
    return hasBeenFound;
  }

  public void setHasBeenFound(boolean hasBeenFound) {
    this.hasBeenFound = hasBeenFound;
  }

}
