package ch.kalunight.uhclg.model.role;

import java.io.File;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;
import ch.kalunight.uhclg.model.PlayerData;
import ch.kalunight.uhclg.model.Role;
import ch.kalunight.uhclg.model.TimeStatus;

public class Sorciere extends SaviorRole implements RoleImpl {

  private static final File annonceVoiceFile = new File(Role.SOUNDS_FOLDER + "sorciere.wav");
  
  @Override
  public Role getRoleEnum() {
    return Role.SORCIERE;
  }

  @Override
  public String getName() {
    return "Sorcière";
  }

  @Override
  public String getDescription() {
    return "Sorcière : Elle reçoit 3 splash potion de vie 1, "
        + "une splash de regen 1 et 3 potions d’instant damage 1. "
        + "Elle peut ressusciter un joueur mort avec la commande /lgsauver “pseudo”";
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
    ItemStack potion = new ItemStack(Material.POTION, 1);
    PotionMeta meta = (PotionMeta) potion.getItemMeta();
    meta.setBasePotionData(new PotionData(PotionType.REGEN));
    potion.setItemMeta(meta);
    player.getAccount().getPlayer().getInventory().addItem(potion);

    potion = new ItemStack(Material.SPLASH_POTION, 3);
    meta = (PotionMeta) potion.getItemMeta();
    meta.setBasePotionData(new PotionData(PotionType.INSTANT_DAMAGE));
    potion.setItemMeta(meta);
    player.getAccount().getPlayer().getInventory().addItem(potion);

    potion = new ItemStack(Material.SPLASH_POTION, 3);
    meta = (PotionMeta) potion.getItemMeta();
    meta.setBasePotionData(new PotionData(PotionType.INSTANT_HEAL));
    potion.setItemMeta(meta);
    player.getAccount().getPlayer().getInventory().addItem(potion);
  }

  @Override
  public void givePotionEffect(PlayerData player, TimeStatus time) {
    //nothing
  }

}
