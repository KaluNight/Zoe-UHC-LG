package ch.kalunight.uhclg.mincraft.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
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
import ch.kalunight.uhclg.util.LocationUtil;
import ch.kalunight.uhclg.util.PotionUtil;
import ch.kalunight.uhclg.worker.GameWorker;
import ch.kalunight.uhclg.worker.VocalSystemWorker;
import net.dv8tion.jda.api.entities.Member;

public class LgStart implements CommandExecutor {

  private static final Logger logger = LoggerFactory.getLogger(LgStart.class);

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

    if(!GameData.getGameStatus().equals(GameStatus.IN_LOBBY)) {
      sender.sendMessage("La partie est déjà lancé !");
    }
    
    Server server = sender.getServer();
    
    if(!sender.isOp()) {
      sender.sendMessage("Vous devez être administrateur pour pouvoir faire cette commande.");
      return true;
    }
    
    if(!GameData.getGameStatus().equals(GameStatus.IN_LOBBY)) {
      sender.sendMessage("La partie est déjà lancé");
      return true;
    }

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

    GameWorker.setGameStartTime(Stopwatch.createStarted());

    GameWorker.setupGameWorker();

    GameData.setGameStatus(GameStatus.IN_GAME);

    return true;
  }

  private void tpPlayers() {
    List<Location> startLocations = new ArrayList<>();

    World world = ZoePluginMaster.getMinecraftServer().getWorld("world");
    Location spawnLocation = GameData.getLobbyLocation();

    for(PlayerData playerData : GameData.getPlayersInGame()) {

      startLocations.add(LocationUtil.getRandomSpawnLocation(world, spawnLocation));

      playerData.getAccount().getPlayer().setGameMode(GameMode.SURVIVAL);

      playerData.getAccount().getPlayer().setInvulnerable(false);
    }

    for(Location location : startLocations) {
      while(LocationUtil.isLocationNearOfTheList(location, startLocations) || LocationUtil.isAForbidenBiome(location)) {
        Location newLocation = LocationUtil.getRandomSpawnLocation(location.getWorld(), spawnLocation);
        location.setX(newLocation.getX());
        location.setZ(newLocation.getZ());
      }
    }

    int i = -1;
    for(PlayerData player : GameData.getPlayersInGame()) {
      i++;
      world.getChunkAt(startLocations.get(i));

      player.getAccount().getPlayer().getPlayer()
      .addPotionEffect(PotionUtil.SPAWN_RESISTANCE);

      player.getAccount().getPlayer().teleport(startLocations.get(i));
    }
  }

  private void defineWorldBorder() {
    World world = ZoePluginMaster.getMinecraftServer().getWorld("world");

    world.getWorldBorder().setCenter(world.getSpawnLocation());

    world.getWorldBorder().setSize(GameConfig.getGameConfigWithPlayerNumber(GameData.getPlayersRegistered().size()).getWorldBorderSize());
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
