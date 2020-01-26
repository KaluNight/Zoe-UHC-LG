package ch.kalunight.uhclg.minecraft;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ch.kalunight.uhclg.GameData;
import ch.kalunight.uhclg.ZoePluginMaster;
import ch.kalunight.uhclg.model.GameStatus;
import ch.kalunight.uhclg.model.PlayerData;
import ch.kalunight.uhclg.model.Role;
import ch.kalunight.uhclg.model.RoleClan;
import ch.kalunight.uhclg.util.DeathUtil;
import ch.kalunight.uhclg.util.LocationUtil;
import ch.kalunight.uhclg.util.LoverUtil;
import ch.kalunight.uhclg.util.PotionUtil;
import ch.kalunight.uhclg.worker.KillerWorker;
import ch.kalunight.uhclg.worker.LoveKillerWorker;
import ch.kalunight.uhclg.worker.SpectatorWorker;

public class MinecraftEventListener implements Listener {

  private static final int TIME_BEFORE_LIGHTNING_DAMAGE = 3;

  private static final List<KillerWorker> killedPlayersWhoCanBeSaved = Collections.synchronizedList(new ArrayList<>());

  private static LocalDateTime lastTimePlayerKilled = LocalDateTime.now();

  @EventHandler
  public void onPlayerJoin(final PlayerJoinEvent event) {
    if(GameData.getGameStatus().equals(GameStatus.IN_LOBBY)) {
      event.setJoinMessage("Bienvenue sur le serveur " + event.getPlayer().getName() + " !");

      event.getPlayer().setGameMode(GameMode.ADVENTURE);

      event.getPlayer().setInvulnerable(true);
    }else {
      PlayerData playerInGame = GameData.getPlayerInGame(event.getPlayer().getUniqueId());

      if(playerInGame == null) {
        event.setJoinMessage(null);
        event.getPlayer().setGameMode(GameMode.SPECTATOR);
      }else {
        playerInGame.setMinecraftConnected(true);
      }
    }
  }

  @EventHandler
  public void onPlayerQuit(final PlayerQuitEvent event) {

    PlayerData playerInGame = GameData.getPlayerInGame(event.getPlayer().getUniqueId());

    if(playerInGame != null) {
      playerInGame.setMinecraftConnected(false);
      event.setQuitMessage("Le joueur " + event.getPlayer().getName() + " s'est déconnecté !");
    }else {
      event.setQuitMessage(null);
    }
  }

  @EventHandler
  public void onPlayerDeath(final PlayerDeathEvent event) {
    Player player = event.getEntity();
    PlayerData playerData = GameData.getPlayerInGame(player.getUniqueId());

    if(playerData != null) {

      if(playerData.isInLove()) {

        PlayerData otherLover = LoverUtil.getOtherLover(playerData.getAccount().getPlayerUUID());
        
        if(otherLover != null && otherLover.isAlive()) {
          
          otherLover.getAccount().getPlayer().sendMessage("Votre moitié vient de mourir, vous ne pouvez pas supporter cette perte. "
              + "Votre âme vous quittera dans 30 secondes. "
              + "Dans un dernier accès de rage, votre force et votre rapidité se voient augmentés.");
          
          otherLover.getAccount().getPlayer().addPotionEffect(PotionUtil.SPEED);
          otherLover.getAccount().getPlayer().addPotionEffect(PotionUtil.STRENGTH);
          otherLover.getAccount().getPlayer().playSound(otherLover.getAccount().getPlayer().getLocation(), Sound.MUSIC_END, 1, 1);
          
          LoveKillerWorker loveKillerWorker = new LoveKillerWorker(otherLover);
          ZoePluginMaster.getMinecraftServer().getScheduler().runTaskLater(ZoePluginMaster.getPlugin(), loveKillerWorker, DeathUtil.DEATH_TIME_IN_TICKS);
        }
      }
      
      event.setDeathMessage(player.getName() + " a été tué et était un " + playerData.getRole().getName());
      playerData.setAlive(false);

      makeRoleEffect(playerData);

      checkIfTheGameIsEnded();

      player.getLocation().getWorld().playEffect(player.getLocation(), Effect.SMOKE, 1);

      Location thunderLocation = 
          new Location(player.getLocation().getWorld(), player.getLocation().getX(), player.getLocation().getY() - 3,
              player.getLocation().getZ());

      setLastTimePlayerKilled(LocalDateTime.now());
      player.getLocation().getWorld().spawnEntity(thunderLocation, EntityType.LIGHTNING);

      ZoePluginMaster.getMinecraftServer().getScheduler().runTaskLater(ZoePluginMaster.getPlugin(), new SpectatorWorker(player), 100);
    } else {
      event.setDeathMessage(null);
    }
  }

  private void checkIfTheGameIsEnded() {

    List<PlayerData> villageList = new ArrayList<>();
    List<PlayerData> wolfsList = new ArrayList<>();
    List<PlayerData> loverList = new ArrayList<>();
    List<PlayerData> specialList = new ArrayList<>();

    for(PlayerData player : GameData.getPlayersInGame()) {
      if(player.isAlive()) {
        if((player.getRole().getClan().equals(RoleClan.SPECIAL) || player.getRole().equals(Role.LOUP_GAROU_BLANC)) && !player.isInfected()) {
          specialList.add(player);
        }else if(player.getRole().getClan().equals(RoleClan.WOLFS) || player.isInfected()) {
          wolfsList.add(player);
        }else if((player.getRole().getClan().equals(RoleClan.VILLAGE)) && !player.isInfected()) {
          villageList.add(player);
        }
        
        if(player.isInLove()) {
          loverList.add(player);
        }
      }
    }

    if(!villageList.isEmpty() && wolfsList.isEmpty() && specialList.isEmpty()) {
      ZoePluginMaster.getMinecraftServer().broadcastMessage("Le village gagne !");
    }else if(villageList.isEmpty() && !wolfsList.isEmpty() && specialList.isEmpty()) {
      ZoePluginMaster.getMinecraftServer().broadcastMessage("Les loups gagnent !");
    }else if(villageList.isEmpty() && wolfsList.isEmpty() && specialList.size() == 1) {
      ZoePluginMaster.getMinecraftServer().broadcastMessage("Un joueur avec un rôle spécial gagne !");
    }else if(villageList.size() + wolfsList.size() + specialList.size() == 2 && !loverList.isEmpty()) {
      ZoePluginMaster.getMinecraftServer().broadcastMessage("Les amoureux gagnent !");
    }
  }

  private void makeRoleEffect(PlayerData playerKilled) {
    if(playerKilled.equals(GameData.getEnfantSauvageModel())) {
      PlayerData enfantSauvage = null;

      for(PlayerData player : GameData.getPlayersInGame()) {
        if(player.getRole().equals(Role.ENFANT_SAUVAGE)) {
          enfantSauvage = player;
        }
      }

      if(enfantSauvage != null && enfantSauvage.isAlive()) {
        enfantSauvage.getAccount().getPlayer().sendMessage("Votre modèle vient de mourir, vous êtes donc désormais un loup garou ! "
            + "Vous optenez également tous les bonus de votre modèle qui était " + playerKilled.getRole().getName());
        GameData.setEnfantSauvageBuffVole(playerKilled.getRole());
      }
    }
  }

  private boolean isSavior(PlayerData playerData) {
    if(playerData.getRole().equals(Role.SORCIERE) || playerData.getRole().equals(Role.INFECT_PERE_DES_LOUPS)) {
      return true;
    }
    return false;
  }

  private boolean playerCanBeSaved(PlayerData playerToBeSaved) {
    for(PlayerData playerCheckRole : GameData.getPlayersInGame()) {
      if((playerCheckRole.getRole().equals(Role.SORCIERE) || playerCheckRole.getRole().equals(Role.INFECT_PERE_DES_LOUPS)) && playerCheckRole.isAlive()
          && !playerToBeSaved.getAccount().getPlayerUUID().equals(playerCheckRole.getAccount().getPlayerUUID()) && playerToBeSaved.isAlive()) {
        return true;
      }
    }
    return false;
  }

  @EventHandler
  public void onEntityDamage(final EntityDamageEvent e) {
    if (e.getCause() == DamageCause.LIGHTNING) {
      if(LocalDateTime.now().minusSeconds(TIME_BEFORE_LIGHTNING_DAMAGE).isBefore(lastTimePlayerKilled)) {
        e.setCancelled(true);
        e.setDamage(0);
      }
    }
  }

  private void reviveOldVillager(PlayerData playerData, RoleClan killer) {
    GameData.setOldVillagerHasRespawn(true);

    List<Location> locationsOfEveryone = new ArrayList<>();

    for(PlayerData playerAliveAndConnected : GameData.getPlayersInGame()) {
      if(playerAliveAndConnected.isAlive() && playerAliveAndConnected.isConnected()) {
        locationsOfEveryone.add(playerAliveAndConnected.getAccount().getPlayer().getLocation());
      }
    }

    locationsOfEveryone.add(GameData.getLobbyLocation());

    Location location = LocationUtil.getRandomSpawnLocation(ZoePluginMaster.getMinecraftServer().getWorld("world"),
        GameData.getLobbyLocation());

    while(LocationUtil.isLocationNearOfTheList(location, locationsOfEveryone) || LocationUtil.isAForbidenBiome(location)) {
      Location newLocation = LocationUtil.getRandomSpawnLocation(location.getWorld(), GameData.getLobbyLocation());
      location.setX(newLocation.getX());
      location.setZ(newLocation.getZ());
    }

    double respawnLife = playerData.getAccount().getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue();
    if(killer.equals(RoleClan.VILLAGE)) {
      respawnLife /= 2;
    }

    playerData.getAccount().getPlayer().setHealth(respawnLife);
    playerData.getAccount().getPlayer().removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
    playerData.getAccount().getPlayer().addPotionEffect(PotionUtil.SPAWN_RESISTANCE);
    playerData.getAccount().getPlayer().teleport(location);
  }

  @EventHandler
  public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent e) {
    PlayerData playerData = GameData.getPlayerInGame(e.getEntity().getUniqueId());

    if(playerData != null && playerData.getAccount().getPlayer().getHealth() - e.getDamage() < 1) {
      if(playerData.getRole().equals(Role.ANCIEN) && !GameData.isOldVillagerHasRespawn()) {
        PlayerData damager = GameData.getPlayerInGame(e.getDamager().getUniqueId());

        if(damager != null) {
          reviveOldVillager(playerData, damager.getRole().getClan());
        }else {
          reviveOldVillager(playerData, RoleClan.WOLFS);
        }
        return;
      }
    }

    if(playerData != null && playerData.isAlive()) {
      if(playerData.getAccount().getPlayer().getHealth() - e.getDamage() < 1) {
        
        if(playerCanBeSaved(playerData)) {
          KillerWorker killerWorker = new KillerWorker(playerData, DeathUtil.getAviableSavior(new ArrayList<>()));
          ZoePluginMaster.getMinecraftServer().getScheduler().runTaskLater(ZoePluginMaster.getPlugin(), killerWorker, DeathUtil.DEATH_TIME_IN_TICKS);
          killedPlayersWhoCanBeSaved.add(killerWorker);
          playerData.getAccount().getPlayer().setInvulnerable(true);
          playerData.getAccount().getPlayer().addPotionEffect(
              new PotionEffect(PotionEffectType.INVISIBILITY, DeathUtil.DEATH_TIME_IN_TICKS, 0, false, false, false));
          playerData.getAccount().getPlayer().addPotionEffect(
              new PotionEffect(PotionEffectType.BLINDNESS, DeathUtil.DEATH_TIME_IN_TICKS, 5, false, false, false));
          playerData.getAccount().getPlayer().addPotionEffect(
              new PotionEffect(PotionEffectType.SLOW, DeathUtil.DEATH_TIME_IN_TICKS, 30, false, false, false));
          playerData.getAccount().getPlayer().sendMessage("Vous êtes aux portes de la mort. Quelqu'un peux encore vous sauver ...");
          killerWorker.getPotentialSavior().getAccount().getPlayer()
          .sendMessage(playerData.getAccount().getPlayer().getName() + " est entrain de mourir, "
              + "vous avez 30 secondes pour sauver cette personne avec la commande \"lgsauver " 
              + playerData.getAccount().getPlayer().getName() + "\".");
          e.setDamage(0);
        }
      }
    }

    if(!GameData.isGrandMereLoupReveal() && e.getCause().equals(DamageCause.PROJECTILE) && e.getDamager() instanceof Arrow) {
      Arrow a = (Arrow) e.getDamager();
      if(a.getShooter() instanceof Player && e.getEntity() instanceof Player) {
        PlayerData playerShooted = GameData.getPlayerInGame(e.getEntity().getUniqueId());

        if(playerShooted != null) {
          if(playerShooted.getRole().equals(Role.GRAND_MERE_LOUP)) {
            GameData.setGrandMereLoupReveal(true);
          }
        }
      }
    }

    if(!GameData.isLoupAmnesiqueFound()) {
      PlayerData potentionLoupAmnesique = GameData.getPlayerInGame(e.getEntity().getUniqueId());

      if(potentionLoupAmnesique != null && potentionLoupAmnesique.getRole().equals(Role.LOUP_GAROU_AMNESIQUE)) {
        PlayerData damager = GameData.getPlayerInGame(e.getDamager().getUniqueId());

        if(damager != null && damager.isAlive() && damager.getRole().getClan().equals(RoleClan.WOLFS)) {
          GameData.setLoupAmnesiqueFound(true);

          potentionLoupAmnesique.getAccount().getPlayer()
          .sendMessage("Vous êtes un LOUP GAROU AMNESIQUE ! PREVENEZ VOS ALLIÉS LOUPS !");
        }
      }
    }
  }

  @EventHandler
  public void onEntityCombustByEntity​(final EntityCombustEvent event) {
    if ((event.getEntityType().equals(EntityType.DROPPED_ITEM) || event.getEntityType().equals(EntityType.PLAYER)) 
        && LocalDateTime.now().minusSeconds(TIME_BEFORE_LIGHTNING_DAMAGE).isBefore(lastTimePlayerKilled)) {
      event.setCancelled(true);
      event.setDuration(0);
    }
  }

  private static void setLastTimePlayerKilled(LocalDateTime lastTimePlayerKilled) {
    MinecraftEventListener.lastTimePlayerKilled = lastTimePlayerKilled;
  }

  public static List<KillerWorker> getKilledPlayersWhoCanBeSaved() {
    return killedPlayersWhoCanBeSaved;
  }

}