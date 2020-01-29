package ch.kalunight.uhclg.model.role;

import java.io.File;
import ch.kalunight.uhclg.model.PlayerData;
import ch.kalunight.uhclg.model.Role;
import ch.kalunight.uhclg.model.TimeStatus;

public class InfectPereDesLoups extends SaviorRole implements RoleImpl {

  private static final File annonceVoiceFile = new File(Role.SOUNDS_FOLDER + "infect_loup.wav");
  
  private boolean playerHasBeenSaved = false;
  
  @Override
  public Role getRoleEnum() {
    return Role.INFECT_PERE_DES_LOUPS;
  }

  @Override
  public String getName() {
    return "Infect Père des Loups Garou";
  }

  @Override
  public String getDescription() {
    return "L’infect père des loups : ils doit tuer tous les villageois, il dispose des bonus classiques des loups "
        + "(night vision et force 1 la nuit) ainsi que la possibilité de réanimer un villageois mort par un loup une "
        + "fois dans la partie avec la commande /lgsave nomDuJoueur, le joueur infecté conserve ses pouvoirs avant d’être tué.";
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

  public boolean isPlayerHasBeenSaved() {
    return playerHasBeenSaved;
  }

  public void setPlayerHasBeenSaved(boolean playerHasBeenSaved) {
    this.playerHasBeenSaved = playerHasBeenSaved;
  }

}
