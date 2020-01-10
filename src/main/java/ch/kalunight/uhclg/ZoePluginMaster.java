package ch.kalunight.uhclg;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.security.auth.login.LoginException;

import org.bukkit.Server;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jagrosh.jdautilities.command.CommandClientBuilder;

import ch.kalunight.uhclg.discord.DiscordEventListener;
import ch.kalunight.uhclg.discord.commands.LinkCommand;
import ch.kalunight.uhclg.discord.commands.UnlinkCommand;
import ch.kalunight.uhclg.mincraft.commands.LgStart;
import ch.kalunight.uhclg.model.LinkedDiscordAccount;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;

public class ZoePluginMaster extends JavaPlugin {

  private static final Logger logger = LoggerFactory.getLogger(ZoePluginMaster.class);
  
  private static final List<LinkedDiscordAccount> playersRegistered = Collections.synchronizedList(new ArrayList<>());
  
  private static Server server;
  
  private static JDA jda;

  @Override
  public void onEnable() {
    
    CommandClientBuilder client = new CommandClientBuilder();
    
    client.setPrefix("/");
    
    client.addCommand(new LinkCommand());
    client.addCommand(new UnlinkCommand());
    
    try {
      setJda(new JDABuilder()
          .setToken("NjY1MjU5NzgyMzc4MzU2NzM4.XhjE0w.p0ULsxbxT0qvXvEcRRSbRpovs1g")//
          .setStatus(OnlineStatus.ONLINE)//
          .addEventListeners(new DiscordEventListener()).build());
    } catch (LoginException e) {
      logger.error("Discord Error !");
    }//
    
    setMinecraftServer(getServer());
    
    this.getCommand("lgstart").setExecutor(new LgStart());
  }
  
  @Override
  public void onDisable() {
    jda.shutdownNow();
  }

  public static Server getMinecraftServer() {
    return server;
  }
  
  private static void setMinecraftServer(Server server) {
    ZoePluginMaster.server = server;
  }
  
  public static JDA getJda() {
    return jda;
  }

  private static void setJda(JDA jda) {
    ZoePluginMaster.jda = jda;
  }

  public static List<LinkedDiscordAccount> getPlayersRegistered() {
    return playersRegistered;
  }
  
}
