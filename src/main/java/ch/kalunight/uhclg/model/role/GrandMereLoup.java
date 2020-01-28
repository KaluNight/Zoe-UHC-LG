package ch.kalunight.uhclg.model.role;

import java.io.File;
import ch.kalunight.uhclg.model.PlayerData;
import ch.kalunight.uhclg.model.Role;
import ch.kalunight.uhclg.model.TimeStatus;

public class GrandMereLoup implements RoleImpl {

  private static final File annonceVoiceFile = new File(Role.SOUNDS_FOLDER + "grand_mere_loup.wav");
  
  @Override
  public Role getRoleEnum() {
    return Role.GRAND_MERE_LOUP;
  }

  @Override
  public String getName() {
    return "Grand-Mère Loup";
  }

  @Override
  public String getDescription() {
    return "Grand-mère loup : Quand spotted par un rôle (voyante, renard etc…) "
        + "il apparaît comme simple villageois, cependant contrairement au loup amnésique, "
        + "il est conscient de son rôle. Après qu’un joueur (non squelette) lui tirer une flèche, "
        + "il apparaîtra chez la voyante ou renard comme un vrai loup garous. Elle dispose des pouvoirs classiques des loup garou.";
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
      RoleImpl.giveWolfsEffects(player, time);
    }
  }
}
