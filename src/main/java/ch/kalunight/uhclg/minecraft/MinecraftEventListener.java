package ch.kalunight.uhclg.minecraft;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
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
import ch.kalunight.uhclg.model.role.Ancien;
import ch.kalunight.uhclg.model.role.Cupidon;
import ch.kalunight.uhclg.model.role.EnfantSauvage;
import ch.kalunight.uhclg.model.role.GrandMereLoup;
import ch.kalunight.uhclg.model.role.LoupGarouAmnesique;
import ch.kalunight.uhclg.util.DeathUtil;
import ch.kalunight.uhclg.util.LocationUtil;
import ch.kalunight.uhclg.util.LoverUtil;
import ch.kalunight.uhclg.util.PotionUtil;
import ch.kalunight.uhclg.util.RoleUtil;
import ch.kalunight.uhclg.util.TitleUtil;
import ch.kalunight.uhclg.worker.KillerWorker;
import ch.kalunight.uhclg.worker.LoveKillerWorker;
import ch.kalunight.uhclg.worker.SpectatorWorker;

public class MinecraftEventListener implements Listener {

  private static final String END_MESSAGE_TITLE = "Fin de partie !";

  private static final int TIME_BEFORE_LIGHTNING_DAMAGE = 3;

  private static final List<KillerWorker> killedPlayersWhoCanBeSaved = Collections.synchronizedList(new ArrayList<>());

  private static LocalDateTime lastTimePlayerKilled = LocalDateTime.now();

  @EventHandler
  public void onPlayerJoin(final PlayerJoinEvent event) {
    if(GameData.getGameStatus().equals(GameStatus.IN_LOBBY)) {
      event.setJoinMessage("Bienvenue sur le serveur " + event.getPlayer().getName() + " !");

      event.getPlayer().setGameMode(GameMode.ADVENTURE);

      event.getPlayer().setInvulnerable(true);
    }else if(GameData.getGameStatus().equals(GameStatus.WAIT_LOBBY_CREATION)) {
      event.getPlayer().setGameMode(GameMode.SPECTATOR);

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
          otherLover.getAccount().getPlayer().playSound(otherLover.getAccount().getPlayer().getLocation(), Sound.MUSIC_END, 100, 100);

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
        if((player.getRole().getRoleEnum().getClan().equals(RoleClan.SPECIAL) || player.getRole().getRoleEnum().equals(Role.LOUP_GAROU_BLANC)) && !player.isInfected()) {
          specialList.add(player);
        }else if(player.getRole().getRoleEnum().getClan().equals(RoleClan.WOLFS) || player.isInfected()) {
          wolfsList.add(player);
        }else if((player.getRole().getRoleEnum().getClan().equals(RoleClan.VILLAGE)) && !player.isInfected()) {
          villageList.add(player);
        }

        if(player.isInLove() || player.getRole() instanceof Cupidon) {
          loverList.add(player);
        }
      }
    }

    if(!villageList.isEmpty() && wolfsList.isEmpty() && specialList.isEmpty()) {
      ZoePluginMaster.getMinecraftServer().broadcastMessage("Le village gagne !");
      ZoePluginMaster.getMinecraftServer()
      .getOnlinePlayers().forEach(e -> TitleUtil.sendTitleToPlayer(e, END_MESSAGE_TITLE, "Le village gagne !", 100));
    }else if(villageList.isEmpty() && !wolfsList.isEmpty() && specialList.isEmpty()) {
      ZoePluginMaster.getMinecraftServer().broadcastMessage("Les loups gagnent !");
      ZoePluginMaster.getMinecraftServer()
      .getOnlinePlayers().forEach(e -> TitleUtil.sendTitleToPlayer(e, END_MESSAGE_TITLE, "Les loups gagnent !", 100));
    }else if(villageList.isEmpty() && wolfsList.isEmpty() && specialList.size() == 1) {
      ZoePluginMaster.getMinecraftServer().broadcastMessage(specialList.get(0).getAccount().getPlayer().getName() + " gagne !");
      ZoePluginMaster.getMinecraftServer()
      .getOnlinePlayers().forEach(e -> TitleUtil.sendTitleToPlayer(e, END_MESSAGE_TITLE,
          specialList.get(0).getAccount().getPlayer().getName() + " gagne !", 100));
    }else if(villageList.size() + wolfsList.size() + specialList.size() == loverList.size() && !loverList.isEmpty()) {
      ZoePluginMaster.getMinecraftServer().broadcastMessage("Les amoureux gagnent !\n");
      ZoePluginMaster.getMinecraftServer()
      .getOnlinePlayers().forEach(e -> TitleUtil.sendTitleToPlayer(e, END_MESSAGE_TITLE, "Les amoureux gagnent !", 100));
    }
  }

  private void makeRoleEffect(PlayerData playerKilled) {

    List<PlayerData> enfantsSauvage = RoleUtil.getPlayerByRole(GameData.getPlayersInGame(), Role.ENFANT_SAUVAGE);

    for(PlayerData enfantSauvage : enfantsSauvage) {

      EnfantSauvage enfantRole = (EnfantSauvage) enfantSauvage.getRole();

      if(enfantSauvage.isAlive() && enfantRole.getModel().getAccount().getPlayerUUID().equals(playerKilled.getAccount().getPlayerUUID())) {
        enfantSauvage.getAccount().getPlayer().sendMessage("Votre modèle vient de mourir, vous évoluez en véritable loup garou ! "
            + "Vous optenez également tous les bonus de votre modèle qui était " + playerKilled.getRole().getName());
      }
    }
  }

  private boolean playerCanBeSaved(PlayerData playerToBeSaved) {
    for(PlayerData playerCheckRole : GameData.getPlayersInGame()) {
      if((playerCheckRole.getRole().getRoleEnum().equals(Role.SORCIERE) || playerCheckRole.getRole().getRoleEnum().equals(Role.INFECT_PERE_DES_LOUPS)) && playerCheckRole.isAlive()
          && !playerToBeSaved.getAccount().getPlayerUUID().equals(playerCheckRole.getAccount().getPlayerUUID()) && playerToBeSaved.isAlive()) {
        return true;
      }
    }
    return false;
  }

  @EventHandler
  public void onEntityDamage(final EntityDamageEvent e) {
    PlayerData playerData = GameData.getPlayerInGame(e.getEntity().getUniqueId());

    if(playerData != null) {
      for(KillerWorker killerWorkerAlreadyCreated : getKilledPlayersWhoCanBeSaved()) {
        if(playerData.getAccount().getPlayerUUID().equals(killerWorkerAlreadyCreated.getPlayerKilled().getAccount().getPlayerUUID())) {
          e.setCancelled(true);
          e.setDamage(0);
          return;
        }
      }
    }
    
    if (e.getCause() == DamageCause.LIGHTNING) {
      if(LocalDateTime.now().minusSeconds(TIME_BEFORE_LIGHTNING_DAMAGE).isBefore(lastTimePlayerKilled)) {
        e.setCancelled(true);
        e.setDamage(0);
      }
    }
  }

  private void reviveOldVillager(PlayerData playerData, RoleClan killer) {
    ((Ancien) playerData.getRole()).setHasBeenSaved(true);

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
    playerData.getAccount().getPlayer().playSound(playerData.getAccount().getPlayer().getLocation(),
        Sound.ENTITY_ENDERMAN_TELEPORT, SoundCategory.MASTER, 100, 100);
    playerData.getAccount().getPlayer().teleport(location);
  }

  @EventHandler
  public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent e) {
    PlayerData playerData = GameData.getPlayerInGame(e.getEntity().getUniqueId());

    if(playerData != null && playerData.getAccount().getPlayer().getHealth() - e.getDamage() < 1) {
      if(playerData.getRole() instanceof Ancien && !((Ancien) playerData.getRole()).isHasBeenSaved()) {
        PlayerData damager = GameData.getPlayerInGame(e.getDamager().getUniqueId());

        e.setDamage(0);

        if(damager != null) {
          reviveOldVillager(playerData, damager.getRole().getRoleEnum().getClan());
        }else {
          reviveOldVillager(playerData, RoleClan.WOLFS);
        }
        return;
      }
    }

    if(playerData != null && playerData.isAlive()) {
      if(playerData.getAccount().getPlayer().getHealth() - e.getDamage() < 1) {

        for(KillerWorker killerWorkerAlreadyCreated : getKilledPlayersWhoCanBeSaved()) {
          if(playerData.getAccount().getPlayerUUID().equals(killerWorkerAlreadyCreated.getPlayerKilled().getAccount().getPlayerUUID())) {
            e.setCancelled(true);
            e.setDamage(0);
            return;
          }
        }

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

    if(e.getCause().equals(DamageCause.PROJECTILE) && e.getDamager() instanceof Arrow) {
      Arrow a = (Arrow) e.getDamager();
      if(a.getShooter() instanceof Player && e.getEntity() instanceof Player) {
        PlayerData playerShooted = GameData.getPlayerInGame(e.getEntity().getUniqueId());

        if(playerShooted != null && playerShooted.getRole() instanceof GrandMereLoup) {
          ((GrandMereLoup) playerShooted.getRole()).setFound(true);
        }

      }
    }

    PlayerData potentialLoupAmnesique = GameData.getPlayerInGame(e.getEntity().getUniqueId());

    if(potentialLoupAmnesique != null && potentialLoupAmnesique.getRole() instanceof LoupGarouAmnesique) {
      PlayerData damager = GameData.getPlayerInGame(e.getDamager().getUniqueId());

      if(damager != null && damager.isAlive() && damager.getRole().getRoleEnum().getClan().equals(RoleClan.WOLFS)) {
        ((LoupGarouAmnesique) potentialLoupAmnesique.getRole()).setHasBeenFound(true);

        potentialLoupAmnesique.getAccount().getPlayer()
        .sendMessage("Vous êtes un LOUP GAROU AMNESIQUE ! PREVENEZ VOS ALLIÉS LOUPS !");
        potentialLoupAmnesique.getAccount().getPlayer().playSound(
            potentialLoupAmnesique.getAccount().getPlayer().getLocation(), Sound.ENTITY_WOLF_HOWL, 100, 100);
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