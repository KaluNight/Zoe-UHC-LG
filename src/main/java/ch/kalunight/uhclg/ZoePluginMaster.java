package ch.kalunight.uhclg;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
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
import ch.kalunight.uhclg.discord.commands.UnlinkAllCommand;
import ch.kalunight.uhclg.discord.commands.UnlinkCommand;
import ch.kalunight.uhclg.mincraft.commands.LgStart;
import ch.kalunight.uhclg.model.LinkedDiscordAccount;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;

public class ZoePluginMaster extends JavaPlugin {

  private static final Logger logger = LoggerFactory.getLogger(ZoePluginMaster.class);
  
  private static final List<LinkedDiscordAccount> playersRegistered = Collections.synchronizedList(new ArrayList<>());
  
  private static final File SECRET_FILE = new File("plugins/secret.txt");
  
  private static final File OWNER_ID_FILE = new File("plugins/owner.txt");
  
  private static Server server;
  
  private static JDA jda;

  public static void main(String[] args) {
    //Does not use
  }
  
  @Override
  public void onEnable() {
    
    CommandClientBuilder client = new CommandClientBuilder();
    
    client.setPrefix("/");
    
    client.addCommand(new LinkCommand());
    client.addCommand(new UnlinkCommand());
    client.addCommand(new UnlinkAllCommand());
    
    try {
      client.setOwnerId(getParamWithFile(OWNER_ID_FILE));
      setJda(new JDABuilder()//
          .setToken(getParamWithFile(SECRET_FILE))//
          .setStatus(OnlineStatus.ONLINE)//
          .addEventListeners(new DiscordEventListener())//
          .addEventListeners(client.build()).build());
    } catch (LoginException | IOException e) {
      logger.error("Issue when loading Discord !", e);
    }
    
    setMinecraftServer(getServer());
    
    this.getCommand("lgstart").setExecutor(new LgStart());
    
    generateLobby();
  }
  
  private void generateLobby() {
    Server server = getMinecraftServer();
    
    server.getWorld("world");
    
  }

  private String getParamWithFile(File file) throws IOException {
    String line;
    try(BufferedReader br = new BufferedReader(new FileReader(file))){
      line = br.readLine();
    }

    return line;
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
