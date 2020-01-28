package ch.kalunight.uhclg.util;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ch.kalunight.uhclg.model.PlayerData;

public class PotionUtil {

  public static final PotionEffect NIGHT_VISION = 
      new PotionEffect(PotionEffectType.NIGHT_VISION, 999999, 0, false, false, false);
  
  public static final PotionEffect INVISIBILITY = 
      new PotionEffect(PotionEffectType.INVISIBILITY, 999999, 0, false, false, false);
  
  public static final PotionEffect STRENGTH  = 
      new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 999999, 0, false, false, false);

  public static final PotionEffect SPEED = 
      new PotionEffect(PotionEffectType.SPEED, 999999, 0, false, false, false);

  public static final PotionEffect WEAKNESS = 
      new PotionEffect(PotionEffectType.WEAKNESS, 999999, 0, false, false, false);
  
  public static final PotionEffect LITTLE_GIRL_INVISIBILITY = 
      new PotionEffect(PotionEffectType.INVISIBILITY, 60, 0, false, false, false);
  
  public static final PotionEffect RESISTANCE = 
      new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 999999, 0, false, false, false);
  
  public static final PotionEffect SPAWN_RESISTANCE =
      new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 500, 999, false, true, false);
  
  private PotionUtil() {
    // util class
  }

  public static void giveIncreaseDamage(PlayerData player) {
    PotionEffect power = null;
    for(PotionEffect potionEffect : player.getAccount().getPlayer().getActivePotionEffects()) {
      if(potionEffect.getType().equals(PotionEffectType.INCREASE_DAMAGE)) {
        power = potionEffect;
      }
    }
  
    if(power == null) {
      player.getAccount().getPlayer().addPotionEffect(STRENGTH);
    }
  }

  public static void clearIncreaseDamage(PlayerData player) {
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

  public static void clearInvisibility(PlayerData player) {
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

  public static void giveInvisiblity(PlayerData player) {
    PotionEffect nightVision = null;
    for(PotionEffect potionEffect : player.getAccount().getPlayer().getActivePotionEffects()) {
      if(potionEffect.getType().equals(PotionEffectType.INVISIBILITY)) {
        nightVision = potionEffect;
      }
    }
  
    if(nightVision == null) {
      player.getAccount().getPlayer().addPotionEffect(INVISIBILITY);
    }
  }

  public static void giveResistance(PlayerData player) {
    PotionEffect resistance = null;
    for(PotionEffect potionEffect : player.getAccount().getPlayer().getActivePotionEffects()) {
      if(potionEffect.getType().equals(PotionEffectType.DAMAGE_RESISTANCE)) {
        resistance = potionEffect;
      }
    }
  
    if(resistance == null) {
      player.getAccount().getPlayer().addPotionEffect(RESISTANCE);
    }
  }

  public static void clearSpeed(PlayerData player) {
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

  public static void giveSpeed(PlayerData player) {
    PotionEffect speed = null;
    for(PotionEffect potionEffect : player.getAccount().getPlayer().getActivePotionEffects()) {
      if(potionEffect.getType().equals(PotionEffectType.SPEED)) {
        speed = potionEffect;
      }
    }
  
    if(speed == null) {
      player.getAccount().getPlayer().addPotionEffect(SPEED);
    }
  }

  public static void clearWeakness(PlayerData player) {
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

  public static void giveWeakness(PlayerData player) {
    PotionEffect weakness = null;
    for(PotionEffect potionEffect : player.getAccount().getPlayer().getActivePotionEffects()) {
      if(potionEffect.getType().equals(PotionEffectType.WEAKNESS)) {
        weakness = potionEffect;
      }
    }
  
    if(weakness == null) {
      player.getAccount().getPlayer().addPotionEffect(WEAKNESS);
    }
  }

  public static void clearNightVision(PlayerData player) {
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

  public static void giveNightVision(PlayerData player) {
    PotionEffect nightVision = null;
    for(PotionEffect potionEffect : player.getAccount().getPlayer().getActivePotionEffects()) {
      if(potionEffect.getType().equals(PotionEffectType.NIGHT_VISION)) {
        nightVision = potionEffect;
      }
    }
  
    if(nightVision == null) {
      player.getAccount().getPlayer().addPotionEffect(NIGHT_VISION);
    }
  }
}
