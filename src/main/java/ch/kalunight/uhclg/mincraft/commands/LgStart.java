package ch.kalunight.uhclg.mincraft.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.kalunight.uhclg.GameData;
import ch.kalunight.uhclg.ZoePluginMaster;
import ch.kalunight.uhclg.model.GameStatus;
import ch.kalunight.uhclg.model.LinkedDiscordAccount;
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
    
    List<Player> playersMissingInServer = new ArrayList<>();
    for(Player player : server.getOnlinePlayers()) {
      playersMissingInServer.add(player);
      for(LinkedDiscordAccount registedPlayer : GameData.getPlayersRegistered()) {
        if(registedPlayer.getPlayerUUID().equals(player.getUniqueId())) {
          playersMissingInServer.remove(player);
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
      
      for(Player player : playersMissingInServer) {
        fixMessage.append(player.getName() + "\n");
      }
      
      fixMessage.append("Joueurs qu'ils manquent dans la lobby vocal (Pseudo Discord) : \n");
      for(LinkedDiscordAccount missingPlayer : playersMissingInVocal) {
        fixMessage.append(ZoePluginMaster.getJda().getUserById(missingPlayer.getDiscordId()).getAsMention() + "\n");
      }
      
      GameData.getLobbyTextChannel().sendMessage(fixMessage.toString()).queue();
      
      return true;
    }
    
    
    
    
    server.broadcastMessage("The game start !");
    
    
    
    GameData.setGameStatus(GameStatus.IN_GAME);
    
    return true;
  }

}
