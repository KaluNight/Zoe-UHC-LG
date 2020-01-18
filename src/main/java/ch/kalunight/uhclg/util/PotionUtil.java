package ch.kalunight.uhclg.util;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PotionUtil {

  public static final PotionEffect NIGHT_VISION = 
      new PotionEffect(PotionEffectType.NIGHT_VISION, 60, 1, false, false, false);
  
  public static final PotionEffect POWER  = 
      new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 60, 1, false, false, false);

  public static final PotionEffect LITTLE_WOLF_SPEED = 
      new PotionEffect(PotionEffectType.SPEED, 60, 1, false, false, false);

  public static final PotionEffect LITTLE_GIRL_WEAKNESS = 
      new PotionEffect(PotionEffectType.WEAKNESS, 60, 1, false, false, false);
  
  public static final PotionEffect LITTLE_GIRL_INVISIBILITY = 
      new PotionEffect(PotionEffectType.INVISIBILITY, 60, 1, false, false, false);
  
  public static final PotionEffect RESISTANCE = 
      new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 60, 1, false, false, false);
  
  public static final PotionEffect SPAWN_RESISTANCE =
      new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 500, 500, false, true, false);
  
  private PotionUtil() {
    // util class
  }
}
