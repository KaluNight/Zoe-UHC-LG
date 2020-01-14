package ch.kalunight.uhclg.minecraft;

import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import ch.kalunight.uhclg.GameData;
import ch.kalunight.uhclg.model.PlayerData;

public class MinecraftEventListener implements Listener {

  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent event) {
      event.setJoinMessage("Bienvenue sur le serveur " + event.getPlayer().getName() + " !");
      
      event.getPlayer().setGameMode(GameMode.ADVENTURE);
      
      event.getPlayer().setInvulnerable(true);
  }
  
  @EventHandler
  public void onPlayerDeath(PlayerDeathEvent event){
      Player player = event.getEntity();
      PlayerData playerData = GameData.getPlayerInGame(player.getUniqueId());
      
      event.setDeathMessage(player.getName() + " a été tué et était un " + playerData.getRole().getName());
      playerData.setAlive(false);
      
      player.getLocation().getWorld().playEffect(player.getLocation(), Effect.SMOKE, 1);
  }
  
}