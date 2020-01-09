package ch.kalunight.uhclg;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class EventListener implements Listener {

  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent event) {
      event.setJoinMessage("Bienvenue sur le serveur " + event.getPlayer().getName() + " !");
  }
}
