package ch.kalunight.uhclg.model.role;

import java.io.File;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import ch.kalunight.uhclg.model.PlayerData;
import ch.kalunight.uhclg.model.Role;
import ch.kalunight.uhclg.model.TimeStatus;

public class Cupidon implements RoleImpl {

  private static final File annonceVoiceFile = new File(Role.SOUNDS_FOLDER + "cupidon.wav");
  
  @Override
  public Role getRoleEnum() {
    return Role.CUPIDON;
  }

  @Override
  public String getName() {
    return "Cupidon";
  }

  @Override
  public String getDescription() {
    return "Cupidon : il reçoit 3 fils et 1 livre power 2 punch 1 et 64 flèches. A l’annonce " + 
    "de son rôle, il a 5mn pour choisir 2 personnes qui seront en couple (/lglove pseudo_1 pseudo_2, " +
    "il ne peut pas se désigner. Il peut gagner avec les village ou les amoureux. Si un des amoureux meurt, " +
    "l’autre meurt 30 seconds après. Dans un dernier élan de rage, il obtient force 1 + speed 1 avant de mourir.";
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
    player.getAccount().getPlayer().getInventory().addItem(new ItemStack(Material.STRING, 3));
    ItemStack book = new ItemStack(Material.ENCHANTED_BOOK, 1);
    ItemMeta bookMeta = book.getItemMeta();
    bookMeta.addEnchant(Enchantment.ARROW_KNOCKBACK, 0, true);
    bookMeta.addEnchant(Enchantment.ARROW_DAMAGE, 1, true);
    book.setItemMeta(bookMeta);
    player.getAccount().getPlayer().getInventory().addItem(book);

    ItemStack arrows = new ItemStack(Material.ARROW, 64);
    player.getAccount().getPlayer().getInventory().addItem(arrows);
  }

  @Override
  public void givePotionEffect(PlayerData player, TimeStatus time) {
    //No effect
  }

}
