package ch.kalunight.uhclg.util;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;

import ch.kalunight.uhclg.GameData;
import ch.kalunight.uhclg.model.GameConfig;

public class LocationUtil {

  private LocationUtil() {
    //hide default public constructor
  }
  
  public static boolean isAForbidenBiome(Location location) {
  
    Biome biome = location.getWorld().getBiome(location.getBlockX(), location.getBlockY(), location.getBlockZ());
  
    switch(biome) {
    case COLD_OCEAN:
    case DEEP_COLD_OCEAN:
    case DEEP_FROZEN_OCEAN:
    case DEEP_LUKEWARM_OCEAN:
    case DEEP_OCEAN:
    case DEEP_WARM_OCEAN:
    case FROZEN_OCEAN:
    case LUKEWARM_OCEAN:
    case OCEAN:
    case WARM_OCEAN:
      return true;
    default:
      return false;
    }
  }

  public static boolean isLocationNearOfTheList(Location location, List<Location> locations) {
    boolean isLocationNearOfTheList = false;
    for(Location locationToCheck : locations) {
      if(!location.equals(locationToCheck) 
          && location.distanceSquared(locationToCheck) < GameData.getSpawnMinBlockDistance()) {
        isLocationNearOfTheList = true;
        break;
      }
    }
    return isLocationNearOfTheList;
  }

  public static Location getRandomSpawnLocation(World world, Location spawnLocation) {
    int worldBorder = GameConfig.getGameConfigWithPlayerNumber(GameData.getPlayersInGame().size()).getWorldBorderSize();
    
    double x = MathUtil.getRandomNumberInRange(0, worldBorder) - (worldBorder / 2d);
    double z = MathUtil.getRandomNumberInRange(0, worldBorder) - (worldBorder / 2d);
  
    x = spawnLocation.getX() + x;
    z = spawnLocation.getZ() + z;
  
    return new Location(world, x, spawnLocation.getY() + 500, z);
  }

}
