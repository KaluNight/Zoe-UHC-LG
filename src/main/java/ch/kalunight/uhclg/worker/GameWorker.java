package ch.kalunight.uhclg.worker;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import org.bukkit.GameRule;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import com.google.common.base.Stopwatch;

import ch.kalunight.uhclg.GameData;
import ch.kalunight.uhclg.ZoePluginMaster;
import ch.kalunight.uhclg.mincraft.commands.LgListe;
import ch.kalunight.uhclg.mincraft.commands.LgVoir;
import ch.kalunight.uhclg.model.GameStatus;
import ch.kalunight.uhclg.model.PlayerData;
import ch.kalunight.uhclg.model.Role;
import ch.kalunight.uhclg.model.RoleClan;
import ch.kalunight.uhclg.model.TimeStatus;
import ch.kalunight.uhclg.model.VoiceRequest;
import ch.kalunight.uhclg.util.LoverUtil;
import ch.kalunight.uhclg.util.PotionUtil;

public class GameWorker implements Runnable {

  private static final int DAY_DURATION = 24000;
  private static final Duration DAY1_DURATION = Duration.ofMinutes(20);
  private static final Duration DAY2_DURATION = Duration.ofMinutes(10);
  private static final Duration PVP_START_DURATION = Duration.ofMinutes(30);

  private static final int START_OF_DAY = 0;

  private static final int START_OF_NIGHT = 12000;

  private static final int ROLE_START_DAY_NUMBER = 2;

  private static TimeStatus timeStatus;

  private static long secondsTime;

  private static long actualTime;

  private static long actualDayNumber;

  private static Stopwatch chrono;

  private static boolean pvpActivate;

  private static World world;

  @Override
  public void run() {
    if(GameData.getGameStatus().equals(GameStatus.IN_GAME)) {
      defineWorldTime();

      checkPVP();

      if(actualDayNumber >= ROLE_START_DAY_NUMBER) {
        givePlayerEffect();
      }
    }
  }

  private void givePlayerEffect() {
    for(PlayerData player : GameData.getPlayersInGame()) {
      if(player.isAlive() && player.isConnected()) {

        if(player.getRole().getClan().equals(RoleClan.WOLFS) || player.isInfected()) {
          if(timeStatus.equals(TimeStatus.NIGHT)) {
            if(player.getRole().equals(Role.ENFANT_SAUVAGE)) {
              if(GameData.getEnfantSauvageBuffVole() != null) {
                giveNightVision(player);
                givePower(player);
              }
            }else {
              giveNightVision(player);
              givePower(player);
            }
          } else {
            clearNightVision(player);
            clearPower(player);
          }
        }

        switch (player.getRole()) {
        case ANCIEN:
          giveResistance(player);
          break;
        case ASSASSIN:
          if(timeStatus.equals(TimeStatus.DAY)) {
            givePower(player);
          }else {
            clearPower(player);
          }
          break;
        case CUPIDON:
          break;
        case ENFANT_SAUVAGE:
          getEnfantSauvageBuff(player);
          break;
        case GRAND_MERE_LOUP:
          break;
        case INFECT_PERE_DES_LOUPS:
          break;
        case LOUP_GAROU:
          break;
        case LOUP_GAROU_AMNESIQUE:
          break;
        case LOUP_GAROU_BLANC:
          break;
        case PETITE_FILLE:
          if(timeStatus.equals(TimeStatus.NIGHT)) {
            giveWeakness(player);
            giveInvisiblity(player);
            giveNightVision(player);
          }else {
            clearWeakness(player);
            clearInvisibility(player);
            clearNightVision(player);
          }
          break;
        case PETIT_LOUP_GAROU:
          if(timeStatus.equals(TimeStatus.NIGHT)) {
            giveSpeed(player);
          }else {
            clearSpeed(player);
          }
          break;
        case RENARD:
          break;
        case SORCIERE:
          break;
        case VILLAGEOIS:
          break;
        case VOYANTE:
          giveNightVision(player);
          break;
        default:
          break;
        }
      }
    }
  }

  private void giveResistance(PlayerData player) {
    PotionEffect resistance = null;
    for(PotionEffect potionEffect : player.getAccount().getPlayer().getActivePotionEffects()) {
      if(potionEffect.getType().equals(PotionEffectType.DAMAGE_RESISTANCE)) {
        resistance = potionEffect;
      }
    }

    if(resistance == null) {
      player.getAccount().getPlayer().addPotionEffect(PotionUtil.RESISTANCE);
    }
  }

  private void clearSpeed(PlayerData player) {
    PotionEffect speed = null;
    for(PotionEffect potionEffect : player.getAccount().getPlayer().getActivePotionEffects()) {
      if(potionEffect.getType().equals(PotionEffectType.SPEED)) {
        speed = potionEffect;
      }
    }

    if(LoverUtil.isLoverAndOtherLoverDead(player)) {
      return;
    }

    if(speed != null) {
      player.getAccount().getPlayer().removePotionEffect(PotionEffectType.SPEED);
    }
  }

  private void giveSpeed(PlayerData player) {
    PotionEffect speed = null;
    for(PotionEffect potionEffect : player.getAccount().getPlayer().getActivePotionEffects()) {
      if(potionEffect.getType().equals(PotionEffectType.SPEED)) {
        speed = potionEffect;
      }
    }

    if(speed == null) {
      player.getAccount().getPlayer().addPotionEffect(PotionUtil.SPEED);
    }
  }

  private void clearWeakness(PlayerData player) {
    PotionEffect weakness = null;
    for(PotionEffect potionEffect : player.getAccount().getPlayer().getActivePotionEffects()) {
      if(potionEffect.getType().equals(PotionEffectType.WEAKNESS)) {
        weakness = potionEffect;
      }
    }
    
    if(weakness != null) {
      player.getAccount().getPlayer().removePotionEffect(PotionEffectType.WEAKNESS);
    }
  }

  private void giveWeakness(PlayerData player) {
    PotionEffect weakness = null;
    for(PotionEffect potionEffect : player.getAccount().getPlayer().getActivePotionEffects()) {
      if(potionEffect.getType().equals(PotionEffectType.WEAKNESS)) {
        weakness = potionEffect;
      }
    }

    if(weakness == null) {
      player.getAccount().getPlayer().addPotionEffect(PotionUtil.WEAKNESS);
    }
  }

  private void clearPower(PlayerData player) {
    PotionEffect power = null;
    for(PotionEffect potionEffect : player.getAccount().getPlayer().getActivePotionEffects()) {
      if(potionEffect.getType().equals(PotionEffectType.INCREASE_DAMAGE)) {
        power = potionEffect;
      }
    }

    if(LoverUtil.isLoverAndOtherLoverDead(player)) {
      return;
    }
    
    if(power != null) {
      player.getAccount().getPlayer().removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
    }
  }

  private void givePower(PlayerData player) {
    PotionEffect power = null;
    for(PotionEffect potionEffect : player.getAccount().getPlayer().getActivePotionEffects()) {
      if(potionEffect.getType().equals(PotionEffectType.INCREASE_DAMAGE)) {
        power = potionEffect;
      }
    }

    if(power == null) {
      player.getAccount().getPlayer().addPotionEffect(PotionUtil.STRENGTH);
    }
  }

  private void clearInvisibility(PlayerData player) {
    PotionEffect nightVision = null;
    for(PotionEffect potionEffect : player.getAccount().getPlayer().getActivePotionEffects()) {
      if(potionEffect.getType().equals(PotionEffectType.INVISIBILITY)) {
        nightVision = potionEffect;
      }
    }

    if(nightVision != null) {
      player.getAccount().getPlayer().removePotionEffect(PotionEffectType.INVISIBILITY);
    }
  }

  private void giveInvisiblity(PlayerData player) {
    PotionEffect nightVision = null;
    for(PotionEffect potionEffect : player.getAccount().getPlayer().getActivePotionEffects()) {
      if(potionEffect.getType().equals(PotionEffectType.INVISIBILITY)) {
        nightVision = potionEffect;
      }
    }

    if(nightVision == null) {
      player.getAccount().getPlayer().addPotionEffect(PotionUtil.INVISIBILITY);
    }
  }

  private void clearNightVision(PlayerData player) {
    PotionEffect nightVision = null;
    for(PotionEffect potionEffect : player.getAccount().getPlayer().getActivePotionEffects()) {
      if(potionEffect.getType().equals(PotionEffectType.NIGHT_VISION)) {
        nightVision = potionEffect;
      }
    }

    if(nightVision != null) {
      player.getAccount().getPlayer().removePotionEffect(PotionEffectType.NIGHT_VISION);
    }
  }

  private void giveNightVision(PlayerData player) {
    PotionEffect nightVision = null;
    for(PotionEffect potionEffect : player.getAccount().getPlayer().getActivePotionEffects()) {
      if(potionEffect.getType().equals(PotionEffectType.NIGHT_VISION)) {
        nightVision = potionEffect;
      }
    }

    if(nightVision == null) {
      player.getAccount().getPlayer().addPotionEffect(PotionUtil.NIGHT_VISION);
    }
  }

  private void getEnfantSauvageBuff(PlayerData player) {
    if(GameData.getEnfantSauvageBuffVole() != null) {

      switch(GameData.getEnfantSauvageBuffVole()) {
      case PETITE_FILLE:
        if(timeStatus.equals(TimeStatus.NIGHT)) {
          giveWeakness(player);
          giveInvisiblity(player);
          giveNightVision(player);
        }else {
          clearWeakness(player);
          clearInvisibility(player);
          clearNightVision(player);
        }
        break;
      case VOYANTE:
        giveNightVision(player);
        break;
      case PETIT_LOUP_GAROU:
        if(timeStatus.equals(TimeStatus.NIGHT)) {
          giveSpeed(player);
        }else {
          clearSpeed(player);
        }
        break;
      case ASSASSIN:
        if(timeStatus.equals(TimeStatus.DAY)) {
          givePower(player);
        }else {
          clearPower(player);
        }
        break;
      case ANCIEN:
        giveResistance(player);
        break;
      default:
        break;
      }
    }
  }

  private void checkPVP() {
    if(!pvpActivate && chrono.elapsed(TimeUnit.MINUTES) > PVP_START_DURATION.toMinutes()) {
      for(World worldToChange : ZoePluginMaster.getMinecraftServer().getWorlds()) {
        worldToChange.setGameRule(GameRule.NATURAL_REGENERATION, true); //TODO : Disable natural regeneration ?
        worldToChange.setPVP(true);
      }

      pvpActivate = true;
    }
  }

  private void defineWorldTime() {
    setActualTime(actualTime + secondsTime);

    if(actualTime > DAY_DURATION) {
      setSecondsTime(DAY_DURATION / DAY2_DURATION.getSeconds());
      setActualTime(0);
      setTimeStatus(TimeStatus.DAY);
      actualDayNumber++;
      world.setTime(0);

      if(actualDayNumber == ROLE_START_DAY_NUMBER) {
        ZoePluginMaster.getMinecraftServer().broadcastMessage("Le jour 2 commence ! "
            + "Vos rôles sont dévoilés maintenant ! Le PVP sera activé à " + PVP_START_DURATION.getSeconds() / 60 + " minutes !");
        sayRoleToPlayer(); //Les rôles sont disponible a partir du jour 2
        giveStuffOfRole();
      }

      sayMessageOfDay();

      WorldBorder worldBorder = world.getWorldBorder();
      if(worldBorder.getSize() > 750) {
        double worldBorderSize = worldBorder.getSize() - 500;
        worldBorder.setSize(worldBorderSize, 180);
        sayWorldBorderMove(worldBorderSize > 750);
      }else {
        double newSize = worldBorder.getSize() / 2;
        worldBorder.setSize(newSize, 300);
        ZoePluginMaster.getMinecraftServer().broadcastMessage("La carte se réduit de " + newSize + " blocs ! ");
      }

    } else {
      if(actualTime > START_OF_NIGHT) {
        setTimeStatus(TimeStatus.NIGHT);
      }else {
        setTimeStatus(TimeStatus.DAY);
      }
      world.setTime(actualTime);
    }
  }

  private void sayWorldBorderMove(boolean minSizeHit) {
    if(!minSizeHit) {
      ZoePluginMaster.getMinecraftServer().broadcastMessage("La carte se réduit de 500 blocs !");
    }else {
      ZoePluginMaster.getMinecraftServer()
      .broadcastMessage("La carte se réduit de 500 blocs ! Elle rétraicira plus doucement à partir de maintenant.");
      world.getWorldBorder().setWarningDistance(0);
    }

  }

  private void sayMessageOfDay() {

    PlayerData voyante = null;

    for(PlayerData player : GameData.getPlayersInGame()) {
      if(player.getRole().equals(Role.VOYANTE)) {
        voyante = player;
      }
    }

    if(voyante != null && voyante.isConnected() && voyante.isAlive()) {
      voyante.getAccount().getPlayer().sendMessage("Vous pouvez utiliser votre pouvoir de voyante maintenant avec la commande \"lgvoir nomDuJoueur\".");
    }

    LgVoir.setRoleChecked(false);
  }

  private void giveStuffOfRole() {
    for(PlayerData playerData : GameData.getPlayersInGame()) {
      switch(playerData.getRole()) {
      case ANCIEN:
        break;
      case ASSASSIN:
        ItemStack book = new ItemStack(Material.ENCHANTED_BOOK, 1);
        ItemMeta bookMeta = book.getItemMeta();
        bookMeta.addEnchant(Enchantment.DAMAGE_ALL, 3, true);
        book.setItemMeta(bookMeta);
        playerData.getAccount().getPlayer().getInventory().addItem(book);
        book = new ItemStack(Material.ENCHANTED_BOOK, 1);
        bookMeta = book.getItemMeta();
        bookMeta.addEnchant(Enchantment.ARROW_DAMAGE, 3, true);
        book.setItemMeta(bookMeta);
        playerData.getAccount().getPlayer().getInventory().addItem(book);
        book = new ItemStack(Material.ENCHANTED_BOOK, 1);
        bookMeta = book.getItemMeta();
        bookMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 3, true);
        book.setItemMeta(bookMeta);
        playerData.getAccount().getPlayer().getInventory().addItem(book);
        break;
      case CUPIDON:
        playerData.getAccount().getPlayer().getInventory().addItem(new ItemStack(Material.STRING, 3));
        book = new ItemStack(Material.ENCHANTED_BOOK, 1);
        bookMeta = book.getItemMeta();
        bookMeta.addEnchant(Enchantment.ARROW_KNOCKBACK, 0, true);
        bookMeta.addEnchant(Enchantment.ARROW_DAMAGE, 1, true);
        book.setItemMeta(bookMeta);
        playerData.getAccount().getPlayer().getInventory().addItem(book);

        ItemStack arrows = new ItemStack(Material.ARROW, 64);
        playerData.getAccount().getPlayer().getInventory().addItem(arrows);
        break;
      case ENFANT_SAUVAGE:
        break;
      case GRAND_MERE_LOUP:
        break;
      case INFECT_PERE_DES_LOUPS:
        break;
      case LOUP_GAROU_AMNESIQUE:
        break;
      case LOUP_GAROU_BLANC:
        playerData.getAccount().getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(30);
        playerData.getAccount().getPlayer().setHealth(playerData.getAccount().getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue());
        break;
      case PETITE_FILLE:
        playerData.getAccount().getPlayer().getInventory().addItem(new ItemStack(Material.TNT, 5));
        break;
      case PETIT_LOUP_GAROU:
        break;
      case RENARD:
        playerData.getAccount().getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 999999, 0, false, false, true));
        break;
      case SORCIERE:

        ItemStack potion = new ItemStack(Material.POTION, 1);
        PotionMeta meta = (PotionMeta) potion.getItemMeta();
        meta.setBasePotionData(new PotionData(PotionType.REGEN));
        potion.setItemMeta(meta);
        playerData.getAccount().getPlayer().getInventory().addItem(potion);

        potion = new ItemStack(Material.SPLASH_POTION, 3);
        meta = (PotionMeta) potion.getItemMeta();
        meta.setBasePotionData(new PotionData(PotionType.INSTANT_DAMAGE));
        potion.setItemMeta(meta);
        playerData.getAccount().getPlayer().getInventory().addItem(potion);

        potion = new ItemStack(Material.SPLASH_POTION, 3);
        meta = (PotionMeta) potion.getItemMeta();
        meta.setBasePotionData(new PotionData(PotionType.INSTANT_HEAL));
        potion.setItemMeta(meta);
        playerData.getAccount().getPlayer().getInventory().addItem(potion);
        break;
      case VILLAGEOIS:
        break;
      case VOYANTE:
        playerData.getAccount().getPlayer().getInventory().addItem(new ItemStack(Material.OBSIDIAN, 4));
        playerData.getAccount().getPlayer().getInventory().addItem(new ItemStack(Material.BOOKSHELF, 4));
        break;
      default:
        break;
      }
    }
  }

  private void sayRoleToPlayer() {
    for(PlayerData player : GameData.getPlayersInGame()) {
      Role role = player.getRole();

      if(player.getRole().equals(Role.LOUP_GAROU_AMNESIQUE) && !GameData.isLoupAmnesiqueFound()) {
        player.getAccount().getPlayer().sendMessage("Votre rôle : Villageois");
        
        VocalSystemWorker.getVoiceRequests().add(new VoiceRequest(GameData.getLobby().getGuild().getIdLong(),
            player.getAccount().getDiscordId(), Role.VILLAGEOIS.getVoiceFile(), true));
      }else {
        player.getAccount().getPlayer().sendMessage("Votre rôle : " + role.getName());
        
        VocalSystemWorker.getVoiceRequests().add(new VoiceRequest(GameData.getLobby().getGuild().getIdLong(),
            player.getAccount().getDiscordId(), player.getRole().getVoiceFile(), true));
      }
      player.getAccount().getPlayer().sendMessage("Description : " + role.getDescription());
      if(player.getRole().getClan().equals(RoleClan.WOLFS) && !player.getRole().equals(Role.LOUP_GAROU_AMNESIQUE)) {
        player.getAccount().getPlayer().sendMessage(LgListe.getListWolfs());
      }
    }
  }

  public static void setupGameWorker() {
    world.setTime(START_OF_DAY);
    setTimeStatus(TimeStatus.DAY);
    world.getWorldBorder().setWarningDistance(500);

    secondsTime = DAY_DURATION / DAY1_DURATION.getSeconds();
    actualTime = 0;

    actualDayNumber = 1;
    pvpActivate = false;

    world.setTime(actualTime);
  }

  public static String getChronoString() {
    long seconds = chrono.elapsed(TimeUnit.SECONDS);
    long absSeconds = Math.abs(seconds);
    return String.format(
        "%02d:%02d",
        (absSeconds % 3600) / 60,
        absSeconds % 60);
  }

  public static Stopwatch getGameStartTime() {
    return chrono;
  }

  public static void setGameStartTime(Stopwatch gameStartTime) {
    GameWorker.chrono = gameStartTime;
  }

  public static World getWorld() {
    return world;
  }

  public static void setWorld(World world) {
    GameWorker.world = world;
  }

  public static TimeStatus getTimeStatus() {
    return timeStatus;
  }

  public static void setTimeStatus(TimeStatus timeStatus) {
    GameWorker.timeStatus = timeStatus;
  }

  public static long getSecondsTime() {
    return secondsTime;
  }

  public static void setSecondsTime(long secondsTime) {
    GameWorker.secondsTime = secondsTime;
  }

  public static long getActualTime() {
    return actualTime;
  }

  public static long getActualDayNumber() {
    return actualDayNumber;
  }

  public static void setActualTime(long actualTime) {
    GameWorker.actualTime = actualTime;
  }

}
