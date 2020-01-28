package ch.kalunight.uhclg.model.role;

import java.io.File;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import ch.kalunight.uhclg.model.PlayerData;
import ch.kalunight.uhclg.model.Role;
import ch.kalunight.uhclg.model.TimeStatus;
import ch.kalunight.uhclg.util.PotionUtil;

public class Voyante implements RoleImpl {

  private static final File annonceVoiceFile = new File(Role.SOUNDS_FOLDER + "voyante.wav");
  
  @Override
  public Role getRoleEnum() {
    return Role.VOYANTE;
  }

  @Override
  public String getName() {
    return "Voyante";
  }

  @Override
  public String getDescription() {
    return "Voyante : À chaque début de journée (et à l’annonce de son rôle), il reçoit un message et obtient la possibilité de voir le rôle de qqn (/lgvoir ”pseudo”). Il reçoit 4 obsidienne et 4 bibliothèque elle dispose également de l’effet night vision.";
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
    player.getAccount().getPlayer().getInventory().addItem(new ItemStack(Material.OBSIDIAN, 4));
    player.getAccount().getPlayer().getInventory().addItem(new ItemStack(Material.BOOKSHELF, 4));
  }

  @Override
  public void givePotionEffect(PlayerData player, TimeStatus time) {
    PotionUtil.giveNightVision(player);
  }

}
