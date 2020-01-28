package ch.kalunight.uhclg.model.role;

import java.io.File;
import ch.kalunight.uhclg.model.PlayerData;
import ch.kalunight.uhclg.model.Role;
import ch.kalunight.uhclg.model.TimeStatus;
import ch.kalunight.uhclg.util.PotionUtil;

public class Ancien implements RoleImpl {

  private static final File annonceVoiceFile = new File(Role.SOUNDS_FOLDER + "ancien.wav");

  private boolean hasBeenSaved = false;

  @Override
  public Role getRoleEnum() {
    return Role.ANCIEN;
  }

  @Override
  public String getName() {
    return "Ancien";
  }

  @Override
  public String getDescription() {
    return "Ancien : Il dispose de l’effet resistance 1, il sera ressuscité (1 seule fois). "
        + "S’il est tué par un villageois, celui-ci perdra la moitié de sa vie.";
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
    if(hasBeenSaved) {
      PotionUtil.giveResistance(player);
    }
  }

  public boolean isHasBeenSaved() {
    return hasBeenSaved;
  }

  public void setHasBeenSaved(boolean hasBeenSaved) {
    this.hasBeenSaved = hasBeenSaved;
  }

}
