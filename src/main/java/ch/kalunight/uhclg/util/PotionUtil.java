package ch.kalunight.uhclg.util;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PotionUtil {

  public static final PotionEffect NIGHT_VISION = 
      new PotionEffect(PotionEffectType.NIGHT_VISION, 999999, 0, false, false, false);
  
  public static final PotionEffect POWER  = 
      new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 60, 0, false, false, false);

  public static final PotionEffect LITTLE_WOLF_SPEED = 
      new PotionEffect(PotionEffectType.SPEED, 60, 0, false, false, false);

  public static final PotionEffect LITTLE_GIRL_WEAKNESS = 
      new PotionEffect(PotionEffectType.WEAKNESS, 60, 0, false, false, false);
  
  public static final PotionEffect LITTLE_GIRL_INVISIBILITY = 
      new PotionEffect(PotionEffectType.INVISIBILITY, 60, 0, false, false, false);
  
  public static final PotionEffect RESISTANCE = 
      new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 60, 0, false, false, false);
  
  public static final PotionEffect SPAWN_RESISTANCE =
      new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 500, 1000, false, true, false);
  
  private PotionUtil() {
    // util class
  }
}
