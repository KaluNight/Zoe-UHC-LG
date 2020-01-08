package ch.kalunight.uhclg;

import org.bukkit.plugin.java.JavaPlugin;
import ch.kalunight.uhclg.commands.LgStart;

public class UhcLg extends JavaPlugin {

  @Override
  public void onEnable() {
    this.getCommand("lgstart").setExecutor(new LgStart());
  }
  
  @Override
  public void onDisable() {

  }

}
