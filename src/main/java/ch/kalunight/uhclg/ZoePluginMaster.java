package ch.kalunight.uhclg;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.security.auth.login.LoginException;

import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jagrosh.jdautilities.command.CommandClientBuilder;

import ch.kalunight.uhclg.discord.DiscordEventListener;
import ch.kalunight.uhclg.discord.commands.DefineLobbyTextCommand;
import ch.kalunight.uhclg.discord.commands.DefineLobbyVocalCommand;
import ch.kalunight.uhclg.discord.commands.LinkCommand;
import ch.kalunight.uhclg.discord.commands.UnlinkAllCommand;
import ch.kalunight.uhclg.discord.commands.UnlinkCommand;
import ch.kalunight.uhclg.mincraft.commands.LgDescription;
import ch.kalunight.uhclg.mincraft.commands.LgFlairer;
import ch.kalunight.uhclg.mincraft.commands.LgListe;
import ch.kalunight.uhclg.mincraft.commands.LgLove;
import ch.kalunight.uhclg.mincraft.commands.LgModel;
import ch.kalunight.uhclg.mincraft.commands.LgSauver;
import ch.kalunight.uhclg.mincraft.commands.LgStart;
import ch.kalunight.uhclg.mincraft.commands.LgVoir;
import ch.kalunight.uhclg.minecraft.MinecraftEventListener;
import ch.kalunight.uhclg.model.GameStatus;
import ch.kalunight.uhclg.model.JdaWithRateLimit;
import ch.kalunight.uhclg.worker.GameWorker;
import ch.kalunight.uhclg.worker.PositionWorker;
import ch.kalunight.uhclg.worker.ScoreboardWorker;
import ch.kalunight.uhclg.worker.VocalSystemWorker;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;

public class ZoePluginMaster extends JavaPlugin {

  private static final Logger logger = LoggerFactory.getLogger(ZoePluginMaster.class);

  private static final File SECRET_FILE = new File("plugins/secret.txt");

  private static final File OWNER_ID_FILE = new File("plugins/owner.txt");

  private static final File JDA_WORKERS_FILE = new File("plugins/jdaWorkers.txt");

  private static Server server;

  private static JDA jda;
  
  private static ZoePluginMaster plugin;

  public static void main(String[] args) {
    //Do not use
  }

  @Override
  public void onEnable() {
    
    setPlugin(this);

    setMinecraftServer(getServer());

    GameData.setGameStatus(GameStatus.IN_LOBBY);

    CommandClientBuilder client = new CommandClientBuilder();

    client.setPrefix("/");

    client.addCommand(new LinkCommand());
    client.addCommand(new UnlinkCommand());
    client.addCommand(new UnlinkAllCommand());
    client.addCommand(new DefineLobbyVocalCommand());
    client.addCommand(new DefineLobbyTextCommand());

    try {
      client.setOwnerId(getParamWithFile(OWNER_ID_FILE));
      setJda(new JDABuilder()//
          .setToken(getParamWithFile(SECRET_FILE))//
          .setStatus(OnlineStatus.ONLINE)//
          .addEventListeners(new DiscordEventListener())//
          .addEventListeners(client.build()).build());
      VocalSystemWorker.getJdaWorkers().add(new JdaWithRateLimit(getJda()));
    } catch (LoginException | IOException e) {
      logger.error("Issue when loading Discord !", e);
    }

    try {
      generateJdaVoiceWorker();
    } catch (IOException e) {
      logger.error("Issue when loading Discord !", e);
    }

    this.getCommand("lgstart").setExecutor(new LgStart());
    this.getCommand("lgmodel").setExecutor(new LgModel());
    this.getCommand("lgliste").setExecutor(new LgListe());
    this.getCommand("lgvoir").setExecutor(new LgVoir());
    this.getCommand("lgsauver").setExecutor(new LgSauver());
    this.getCommand("lglove").setExecutor(new LgLove());
    this.getCommand("lgdesc").setExecutor(new LgDescription());
    this.getCommand("lgflairer").setExecutor(new LgFlairer());

    generateLobby();
    
    defineGameRule();

    getServer().getScheduler().runTaskTimer(this, new PositionWorker(), 10, 10);
    getServer().getScheduler().runTaskTimer(this, new VocalSystemWorker(), 10, 10);
    getServer().getScheduler().runTaskTimer(this, new ScoreboardWorker(), 20, 20);
    getServer().getScheduler().runTaskTimer(this, new GameWorker(), 20, 20);
    
    getServer().getPluginManager().registerEvents(new MinecraftEventListener(), this);
  }

  private void defineGameRule() {
    for(World world : ZoePluginMaster.getMinecraftServer().getWorlds()) {
      world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
      world.setGameRule(GameRule.NATURAL_REGENERATION, true);
      world.setPVP(true);
    }
  }

  private void generateJdaVoiceWorker() throws IOException {
    List<String> tockenList = getFileListParam(JDA_WORKERS_FILE);

    for(String tocken : tockenList) {
      try {
        VocalSystemWorker.getJdaWorkers().add(new JdaWithRateLimit(new JDABuilder()//
            .setToken(tocken)//
            .setStatus(OnlineStatus.ONLINE).build()));
      } catch (LoginException e) {
        logger.error("Issue when loading Discord !", e);
      }
    }
  }

  private List<String> getFileListParam(File jdaWorkersFile) throws IOException {
    List<String> tocken = new ArrayList<>();

    try(BufferedReader br = new BufferedReader(new FileReader(jdaWorkersFile))){
      String ligne;
      while ((ligne=br.readLine())!=null){
        tocken.add(ligne);
      }
    }

    return tocken;
  }

  private void generateLobby() {
    World world = getMinecraftServer().getWorld("world");
    
    GameWorker.setWorld(world);
    
    Location spawn = world.getSpawnLocation();

    int spawnSize = 25;
    int spawnHigh = 200;

    for(int j = 0; j < spawnSize; j++) {
      for(int i = 0; i < spawnSize; i++) {
        world.getBlockAt(spawn.getBlockX() + i, spawnHigh, spawn.getBlockY() + j).setType(Material.GLASS);
      }
    }

    world.setSpawnLocation(spawn.getBlockX() + (spawnSize / 2), spawnHigh + 1, spawn.getBlockY() + (spawnSize / 2));

    GameData.setLobbyLocation(spawn);
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
    VocalSystemWorker.getJdaWorkers().forEach(e -> e.getJda().shutdownNow());
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

  public static ZoePluginMaster getPlugin() {
    return plugin;
  }

  public static void setPlugin(ZoePluginMaster plugin) {
    ZoePluginMaster.plugin = plugin;
  }

}
