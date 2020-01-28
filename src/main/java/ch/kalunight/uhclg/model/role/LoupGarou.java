package ch.kalunight.uhclg.model.role;

import java.io.File;
import ch.kalunight.uhclg.model.PlayerData;
import ch.kalunight.uhclg.model.Role;
import ch.kalunight.uhclg.model.TimeStatus;

public class LoupGarou implements RoleImpl {

  private static final File annonceVoiceFile = new File(Role.SOUNDS_FOLDER + "loup_garou.wav");

  @Override
  public Role getRole() {
    return Role.LOUP_GAROU;
  }

  @Override
  public String getName() {
    return "Loup Garou";
  }

  @Override
  public String getDescription() {
    return "Loup-Garous : Les loups-Garous sont les ennemis des villageois, "
        + "ils doivent tuer tous les membres du village. "
        + "Ils ont night vision 24/24 et force 1 la nuit.";
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
    RoleImpl.giveWolfsEffects(player, time);
  }

}
