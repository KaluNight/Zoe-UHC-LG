package ch.kalunight.uhclg.worker;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import ch.kalunight.uhclg.GameData;
import ch.kalunight.uhclg.ZoePluginMaster;
import ch.kalunight.uhclg.minecraft.MinecraftEventListener;
import ch.kalunight.uhclg.model.PlayerData;
import ch.kalunight.uhclg.model.Role;
import ch.kalunight.uhclg.util.DeathUtil;
import ch.kalunight.uhclg.util.LocationUtil;
import ch.kalunight.uhclg.util.PotionUtil;

public class KillerWorker implements Runnable {
  
  private final List<PlayerData> saviorAlreadyAsked = new ArrayList<>();
  
  private PlayerData playerKilled;
  
  private PlayerData potentialSavior;
  
  private boolean hasBeenSaved;
  
  public KillerWorker(PlayerData playerKilled, PlayerData potentialSavior) {
    this.playerKilled = playerKilled;
    this.potentialSavior = potentialSavior;
    this.hasBeenSaved = false;
  }
  
  @Override
  public void run() {
    
    if(hasBeenSaved) {
      if(potentialSavior.getRole().equals(Role.INFECT_PERE_DES_LOUPS)) {
        playerKilled.setRole(Role.LOUP_GAROU);
        playerKilled.getAccount().getPlayer().sendMessage("Vous avez été sauvé par l'infect père des loups garous, vous êtes désomais un loup garou !");
      }else {
        playerKilled.getAccount().getPlayer().sendMessage("Vous avez été sauvé par la sorcière !");
      }
      
      Location location = LocationUtil.getRandomSpawnLocation(ZoePluginMaster.getMinecraftServer().getWorld("world"), GameData.getLobbyLocation());
      
      List<Location> locationsOfEveryone = new ArrayList<>();
      
      for(PlayerData playerData : GameData.getPlayersInGame()) {
        if(playerData.isAlive() && playerData.isConnected()) {
          locationsOfEveryone.add(playerData.getAccount().getPlayer().getLocation());
        }
      }
      
      locationsOfEveryone.add(GameData.getLobbyLocation());
      
      while(LocationUtil.isLocationNearOfTheList(location, locationsOfEveryone) || LocationUtil.isAForbidenBiome(location)) {
        Location newLocation = LocationUtil.getRandomSpawnLocation(location.getWorld(), GameData.getLobbyLocation());
        location.setX(newLocation.getX());
        location.setZ(newLocation.getZ());
      }
      
      playerKilled.getAccount().getPlayer().addPotionEffect(PotionUtil.SPAWN_RESISTANCE);
      playerKilled.getAccount().getPlayer().setInvulnerable(false);
      playerKilled.getAccount().getPlayer().teleport(location);
    }else {
      saviorAlreadyAsked.add(potentialSavior);
      
      PlayerData savior = DeathUtil.getAviableSavior(saviorAlreadyAsked);
      
      if(savior == null) {
        MinecraftEventListener.getKilledPlayersWhoCanBeSaved().remove(this);
        
        playerKilled.getAccount().getPlayer().sendMessage("Personne n'a voulu vous sauver ...");
        
        playerKilled.setAlive(false);
        playerKilled.getAccount().getPlayer().damage(1000);
      } else {
        potentialSavior = savior;
        playerKilled.getAccount().getPlayer().sendMessage("Un autre joueur peux encore vous sauver ...");
        
        playerKilled.getAccount().getPlayer().addPotionEffect(
            new PotionEffect(PotionEffectType.INVISIBILITY, DeathUtil.DEATH_TIME_IN_TICKS, 1, false, false, false));
        playerKilled.getAccount().getPlayer().addPotionEffect(
            new PotionEffect(PotionEffectType.BLINDNESS, DeathUtil.DEATH_TIME_IN_TICKS, 5, false, false, false));
        playerKilled.getAccount().getPlayer().addPotionEffect(
            new PotionEffect(PotionEffectType.SLOW, DeathUtil.DEATH_TIME_IN_TICKS, 30, false, false, false));
        
        potentialSavior.getAccount().getPlayer()
        .sendMessage(playerKilled.getAccount().getPlayer().getName() + " est entrain de mourir, "
            + "vous avez 30 secondes pour sauver cette personne avec la commande \"lgsauver " 
            + playerKilled.getAccount().getPlayer().getName() + "\".");
        ZoePluginMaster.getMinecraftServer().getScheduler().runTaskLater(ZoePluginMaster.getPlugin(), this, DeathUtil.DEATH_TIME_IN_TICKS);
      }
    }
    
  }

  public boolean isHasBeenSaved() {
    return hasBeenSaved;
  }

  public void setHasBeenSaved(boolean hasBeenSaved) {
    this.hasBeenSaved = hasBeenSaved;
  }

  public PlayerData getPlayerKilled() {
    return playerKilled;
  }

  public PlayerData getPotentialSavior() {
    return potentialSavior;
  }
  
}
