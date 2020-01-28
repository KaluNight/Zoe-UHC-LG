package ch.kalunight.uhclg.model.role;

import java.io.File;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import ch.kalunight.uhclg.model.PlayerData;
import ch.kalunight.uhclg.model.Role;
import ch.kalunight.uhclg.model.TimeStatus;
import ch.kalunight.uhclg.util.PotionUtil;

public class PetiteFille implements RoleImpl {

  private static final File annonceVoiceFile = new File(Role.SOUNDS_FOLDER + "petite_fille.wav");
  
  @Override
  public Role getRole() {
    return Role.PETITE_FILLE;
  }

  @Override
  public String getName() {
    return "Petite Fille";
  }

  @Override
  public String getDescription() {
    return "Petite Fille : elle reçoit l’effet night vision et invisibilité et faiblesse 1 durant la nuit. Elle reçoit aussi 5 TNT.";
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
    player.getAccount().getPlayer().getInventory().addItem(new ItemStack(Material.TNT, 5));
  }

  @Override
  public void givePotionEffect(PlayerData player, TimeStatus time) {
    if(time.equals(TimeStatus.NIGHT)) {
      PotionUtil.giveWeakness(player);
      PotionUtil.giveInvisiblity(player);
      PotionUtil.giveNightVision(player);
    }else {
      PotionUtil.clearWeakness(player);
      PotionUtil.clearInvisibility(player);
      PotionUtil.clearNightVision(player);
    }
  }

}
