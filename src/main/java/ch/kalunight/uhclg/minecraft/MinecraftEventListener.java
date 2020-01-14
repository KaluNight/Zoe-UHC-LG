package ch.kalunight.uhclg.minecraft;

import java.time.LocalDateTime;
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
import ch.kalunight.uhclg.GameData;
import ch.kalunight.uhclg.model.PlayerData;

public class MinecraftEventListener implements Listener {

  private static final int TIME_BEFORE_LIGHTNING_DAMAGE = 3;
  
  private static LocalDateTime lastTimePlayerKilled = LocalDateTime.now();

  @EventHandler
  public void onPlayerJoin(final PlayerJoinEvent event) {
    event.setJoinMessage("Bienvenue sur le serveur " + event.getPlayer().getName() + " !");

    event.getPlayer().setGameMode(GameMode.ADVENTURE);

    event.getPlayer().setInvulnerable(true);
  }

  @EventHandler
  public void onPlayerDeath(final PlayerDeathEvent event){
    Player player = event.getEntity();
    PlayerData playerData = GameData.getPlayerInGame(player.getUniqueId());

    event.setDeathMessage(player.getName() + " a été tué et était un " + playerData.getRole().getName());
    playerData.setAlive(false);

    player.getLocation().getWorld().playEffect(player.getLocation(), Effect.SMOKE, 1);

    Location thunderLocation = 
        new Location(player.getLocation().getWorld(), player.getLocation().getX(), player.getLocation().getY() + 3, player.getLocation().getZ());

    player.getLocation().getWorld().spawnEntity(thunderLocation, EntityType.LIGHTNING);

    setLastTimePlayerKilled(LocalDateTime.now());
  }

  @EventHandler
  public void onEntityDamageEvent(final EntityDamageEvent e) {
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
  public void onEntityCombustByEntityEvent​(final EntityCombustEvent event) {
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