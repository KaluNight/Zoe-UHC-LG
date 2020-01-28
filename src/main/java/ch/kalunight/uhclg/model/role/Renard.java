package ch.kalunight.uhclg.model.role;

import java.io.File;
import ch.kalunight.uhclg.model.PlayerData;
import ch.kalunight.uhclg.model.Role;
import ch.kalunight.uhclg.model.TimeStatus;
import ch.kalunight.uhclg.util.PotionUtil;

public class Renard implements RoleImpl {

  private static final File annonceVoiceFile = new File(Role.SOUNDS_FOLDER + "renard.wav");
  
  @Override
  public Role getRole() {
    return Role.RENARD;
  }

  @Override
  public String getName() {
    return "Renard";
  }

  @Override
  public String getDescription() {
    return "il dispose de l’effet speed 1, a moins de 10 blocs d’un autre joueur, "
        + "il peut utiliser la commande /lgflairer “pseudo” pour savoir s’il est "
        + "chez les innocents ou chez les loups (le loup amnésique non révélé et l'assassin sont annoncé comme innocents)";
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
    PotionUtil.giveSpeed(player);
  }

}
