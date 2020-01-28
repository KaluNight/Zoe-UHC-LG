package ch.kalunight.uhclg.model.role;

import java.io.File;
import org.bukkit.attribute.Attribute;
import ch.kalunight.uhclg.model.PlayerData;
import ch.kalunight.uhclg.model.Role;
import ch.kalunight.uhclg.model.TimeStatus;

public class LoupGarouBlanc implements RoleImpl {

  private static final File annonceVoiceFile = new File(Role.SOUNDS_FOLDER + "loup_garou_blanc.wav");
  
  @Override
  public Role getRole() {
    return Role.LOUP_GAROU_BLANC;
  }

  @Override
  public String getName() {
    return "Loup-Garou Blanc";
  }

  @Override
  public String getDescription() {
    return "Loup-Garou Blanc : il a les mêmes pouvoir que les loups mais "
        + "il doit gagner seul, il possède 15 coeurs contrairement aux autres joueurs.";
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
    player.getAccount().getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(30);
    player.getAccount().getPlayer().setHealth(player.getAccount().getPlayer()
        .getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue());
  }

  @Override
  public void givePotionEffect(PlayerData player, TimeStatus time) {
    RoleImpl.giveWolfsEffects(player, time);
  }

}
