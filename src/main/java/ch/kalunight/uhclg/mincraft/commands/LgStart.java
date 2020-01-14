package ch.kalunight.uhclg.mincraft.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.base.Stopwatch;
import ch.kalunight.uhclg.GameData;
import ch.kalunight.uhclg.ZoePluginMaster;
import ch.kalunight.uhclg.model.GameConfig;
import ch.kalunight.uhclg.model.GameStatus;
import ch.kalunight.uhclg.model.LinkedDiscordAccount;
import ch.kalunight.uhclg.model.PlayerData;
import ch.kalunight.uhclg.model.Role;
import ch.kalunight.uhclg.util.MathUtil;
import ch.kalunight.uhclg.worker.GameWorker;
import ch.kalunight.uhclg.worker.VocalSystemWorker;
import net.dv8tion.jda.api.entities.Member;

public class LgStart implements CommandExecutor {

  private static final Logger logger = LoggerFactory.getLogger(LgStart.class);

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

    Server server = sender.getServer();

    if(GameData.getLobby() == null) {
      sender.sendMessage("Vous ne pouvez pas lancer une partie si le lobby n'a pas été défini !");
      logger.info("Impossible to launch the game, missing voice lobby");
      return true;
    }

    List<LinkedDiscordAccount> playersMissingInServer = new ArrayList<>();
    for(LinkedDiscordAccount registedPlayer : GameData.getPlayersRegistered()) {
      playersMissingInServer.add(registedPlayer);
      for(Player player : server.getOnlinePlayers()) {
        if(registedPlayer.getPlayerUUID().equals(player.getUniqueId())) {
          playersMissingInServer.remove(registedPlayer);
          break;
        }
      }
    }

    List<LinkedDiscordAccount> playersMissingInVocal = new ArrayList<>();
    for(LinkedDiscordAccount registedPlayer : GameData.getPlayersRegistered()) {
      playersMissingInVocal.add(registedPlayer);
      for(Member member : GameData.getLobby().getMembers()) {
        if(member.getIdLong() == registedPlayer.getDiscordId()) {
          playersMissingInVocal.remove(registedPlayer);
        }
      }
    }

    if(!playersMissingInServer.isEmpty() || !playersMissingInVocal.isEmpty()) {
      StringBuilder fixMessage = new StringBuilder();

      fixMessage.append("Joueurs qui sont enregistrés et qui manque sur le serveur (Pseudo Minecraft) :\n");

      for(LinkedDiscordAccount linkedAccount : playersMissingInServer) {
        OfflinePlayer player = server.getOfflinePlayer(linkedAccount.getPlayerUUID());
        fixMessage.append(player.getName() + "\n");
      }

      fixMessage.append("Joueurs qu'ils manquent dans la lobby vocal (Pseudo Discord) : \n");
      for(LinkedDiscordAccount missingPlayer : playersMissingInVocal) {
        fixMessage.append(missingPlayer.getDiscordUser().getAsMention() + "\n");
      }

      GameData.getLobbyTextChannel().sendMessage(fixMessage.toString()).queue();
      return true;
    }

    GameData.setGameStatus(GameStatus.STARTING_GAME);
    
    startTheGame();

    defineWorldBorder();
    
    VocalSystemWorker.setupVoiceChannels(GameData.getPlayersInGame());
    
    tpPlayers();
    
    sayRoleToPlayer();
    
    giveStuffOfRole();
    
    GameWorker.setGameStartTime(Stopwatch.createStarted());
    
    GameData.setGameStatus(GameStatus.IN_GAME);
    
    return true;
  }

  private void sayRoleToPlayer() {
    for(PlayerData player : GameData.getPlayersInGame()) {
      Role role = player.getRole();
      
      player.getAccount().getPlayer().sendMessage("Votre rôle : " + role.getName());
    }
  }

  private void giveStuffOfRole() {
    
  }

  private void tpPlayers() {
    List<Location> startLocations = new ArrayList<>();

    World world = ZoePluginMaster.getMinecraftServer().getWorld("world");
    Location spawnLocation = GameData.getLobbyLocation();

    for(PlayerData playerData : GameData.getPlayersInGame()) {

      startLocations.add(getRandomSpawnLocation(world, spawnLocation));

      playerData.getAccount().getPlayer().setGameMode(GameMode.SURVIVAL);
      
      playerData.getAccount().getPlayer().setInvulnerable(false);
    }

    for(Location location : startLocations) {
      while(isLocationNearOfTheList(location, startLocations) || isAForbidenBiome(location)) {// TODO Fix that
        Location newLocation = getRandomSpawnLocation(location.getWorld(), spawnLocation);
        location.setX(newLocation.getX());
        location.setZ(newLocation.getZ());
      }
    }
    
    int i = -1;
    for(PlayerData player : GameData.getPlayersInGame()) {
      i++;
      world.getChunkAt(startLocations.get(i));
      
      player.getAccount().getPlayer().getPlayer()
      .addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 300, 500, false, true, false));
      
      player.getAccount().getPlayer().teleport(startLocations.get(i));
    }
  }

  private boolean isAForbidenBiome(Location location) {
    
    Biome biome = location.getWorld().getBiome(location.getBlockX(), location.getBlockY(), location.getBlockZ());
    
    switch(biome) {
      case COLD_OCEAN:
      case DEEP_COLD_OCEAN:
      case DEEP_FROZEN_OCEAN:
      case DEEP_LUKEWARM_OCEAN:
      case DEEP_OCEAN:
      case DEEP_WARM_OCEAN:
      case FROZEN_OCEAN:
      case LUKEWARM_OCEAN:
      case OCEAN:
      case WARM_OCEAN:
        return true;
      default:
        return false;
    }
  }

  private boolean isLocationNearOfTheList(Location location, List<Location> locations) {
    boolean isLocationNearOfTheList = false;
    for(Location locationToCheck : locations) {
      if(!location.equals(locationToCheck) 
          && location.distanceSquared(locationToCheck) < GameData.getSpawnMinBlockDistance()) {
        isLocationNearOfTheList = true;
        break;
      }
    }
    return isLocationNearOfTheList;
  }
  

  private Location getRandomSpawnLocation(World world, Location spawnLocation) {
    double x = MathUtil.getRandomNumberInRange(0, GameData.getBaseWorldBorderSize()) - (GameData.getBaseWorldBorderSize() / 2d);
    double z = MathUtil.getRandomNumberInRange(0, GameData.getBaseWorldBorderSize()) - (GameData.getBaseWorldBorderSize() / 2d);

    x = spawnLocation.getX() + x;
    z = spawnLocation.getZ() + z;

    return new Location(world, x, spawnLocation.getY() + 500, z);
  }

  private void defineWorldBorder() {
    World world = ZoePluginMaster.getMinecraftServer().getWorld("world");

    world.getWorldBorder().setCenter(world.getSpawnLocation());

    world.getWorldBorder().setSize(GameData.getBaseWorldBorderSize());
  }

  private void startTheGame() {
    GameData.getPlayersRegistered().forEach(e -> e.getPlayer().sendTitle("La partie va commencer !", "Vous allez être téleporté ...", 10, 40, 10));

    List<Role> rolesInTheGame = getRoleWithNumberOfPlayer(GameData.getPlayersRegistered().size());

    definePlayerInGame(rolesInTheGame);
  }

  private void definePlayerInGame(List<Role> rolesInTheGame) {
    int i = -1;
    for(Role role : rolesInTheGame) {
      i++;
      LinkedDiscordAccount account = GameData.getPlayersRegistered().get(i);

      PlayerData playerInGame = new PlayerData(account);
      playerInGame.setRole(role);

      GameData.getPlayersInGame().add(playerInGame);
    }
  }

  private List<Role> getRoleWithNumberOfPlayer(int numberOfPlayer) {
    GameConfig config = GameConfig.getGameConfigWithPlayerNumber(numberOfPlayer);

    List<Role> roles = config.getRoles(config);

    Collections.shuffle(roles);

    return roles;
  }

}
