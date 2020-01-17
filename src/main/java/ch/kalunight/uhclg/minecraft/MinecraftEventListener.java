package ch.kalunight.uhclg.minecraft;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import ch.kalunight.uhclg.GameData;
import ch.kalunight.uhclg.ZoePluginMaster;
import ch.kalunight.uhclg.model.GameStatus;
import ch.kalunight.uhclg.model.PlayerData;
import ch.kalunight.uhclg.model.Role;
import ch.kalunight.uhclg.model.RoleClan;
import ch.kalunight.uhclg.worker.KillerWorker;

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
      event.setQuitMessage("Le joueur " + event.getPlayer().getName() + " c'est déconnecté !");
    }else {
      event.setQuitMessage(null);
    }
  }

  @EventHandler
  public void onPlayerDeath(final PlayerDeathEvent event){
    Player player = event.getEntity();
    PlayerData playerData = GameData.getPlayerInGame(player.getUniqueId());

    if(playerCanBeSaved()) {
      KillerWorker killerWorker = new KillerWorker(playerData, getFirstSavior());
      ZoePluginMaster.getMinecraftServer().getScheduler().runTask(ZoePluginMaster.getPlugin(), )
    }
    
    event.setDeathMessage(player.getName() + " a été tué et était un " + playerData.getRole().getName());
    playerData.setAlive(false);

    player.getLocation().getWorld().playEffect(player.getLocation(), Effect.SMOKE, 1);

    Location thunderLocation = 
        new Location(player.getLocation().getWorld(), player.getLocation().getX(), player.getLocation().getY() - 3,
            player.getLocation().getZ());

    player.getLocation().getWorld().spawnEntity(thunderLocation, EntityType.LIGHTNING);

    setLastTimePlayerKilled(LocalDateTime.now());
  }

  private PlayerData getFirstSavior() {
    
    for(PlayerData savior : GameData.getPlayersInGame()) {
      
    }
    
    return null;
  }

  private boolean playerCanBeSaved() {
    for(PlayerData playerCheckRole : GameData.getPlayersInGame()) {
      if(playerCheckRole.getRole().equals(Role.SORCIERE) || playerCheckRole.getRole().equals(Role.INFECT_PERE_DES_LOUPS)) {
        return true;
      }
    }
    return false;
  }

  @EventHandler
  public void onEntityDamage(final EntityDamageEvent e) {
    if (!(e.getEntity() instanceof Player || e.getEntityType().equals(EntityType.DROPPED_ITEM))) {
      return;
    }

    if (e.getCause() == DamageCause.LIGHTNING) {
      if(LocalDateTime.now().minusSeconds(TIME_BEFORE_LIGHTNING_DAMAGE).isBefore(lastTimePlayerKilled)) {
        e.setCancelled(true);
        e.setDamage(0);
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

}