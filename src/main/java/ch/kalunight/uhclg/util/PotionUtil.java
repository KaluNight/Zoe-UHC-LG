package ch.kalunight.uhclg.util;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

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
}
