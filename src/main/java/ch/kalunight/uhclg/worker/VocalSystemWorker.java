package ch.kalunight.uhclg.worker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.kalunight.uhclg.GameData;
import ch.kalunight.uhclg.model.GameStatus;
import ch.kalunight.uhclg.model.JdaWithRateLimit;
import ch.kalunight.uhclg.model.PlayerData;
import ch.kalunight.uhclg.model.PlayerVoicePosition;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.VoiceChannel;

public class VocalSystemWorker implements Runnable {

  private static final Logger logger = LoggerFactory.getLogger(VocalSystemWorker.class);

  private static final List<JdaWithRateLimit> jdaWorkers = Collections.synchronizedList(new ArrayList<>());

  private static final List<Long> listIdVoiceChannel = Collections.synchronizedList(new ArrayList<>());

  private static final List<PlayerData> playersAlreadyTreated = Collections.synchronizedList(new ArrayList<>());

  private static final List<PlayerVoicePosition> playersVoicePositions = Collections.synchronizedList(new ArrayList<>());

  private static long idDeadPlayer;

  private static double voiceDistance = 750d;

  @Override
  public void run() {
    playersAlreadyTreated.clear();

    if(GameData.getGameStatus().equals(GameStatus.IN_GAME)) {
      refreshPlayerVoiceLink();
      for(PlayerData player : GameData.getPlayersInGame()) {

        if(!playersAlreadyTreated.contains(player) && player.isConnected()) {
          PlayerVoicePosition voiceChannelPlayer = getPlayerVoicePosition(player.getAccount().getDiscordId());

          if(voiceChannelPlayer != null) {
            List<PlayerVoicePosition> playersInTheSameChannel = getPlayersNeededInTheChannel(new ArrayList<>(), voiceChannelPlayer);

            if(playersInTheSameChannel.size() == 1) {
              if(!isPlayerAlone(voiceChannelPlayer)) {
                JdaWithRateLimit jda = getAvaibleJda();
                Guild guild = getAvaibleGuild(jda);
                Member member = guild.getMemberById(player.getAccount().getDiscordId());

                VoiceChannel emptyVoiceChannel = getEmptyVoiceChannel(guild);

                guild.moveVoiceMember(member, emptyVoiceChannel).queue();
                jda.addCall();
                voiceChannelPlayer.setActualVoiceChannelId(emptyVoiceChannel.getIdLong());
                playersAlreadyTreated.add(player);
              }
            }else {
              long voiceChannelId = getVoiceMostPopulatedChannel(playersInTheSameChannel);

              for(PlayerVoicePosition playerToMove : playersInTheSameChannel) {
                if(playerToMove.getActualVoiceChannelId() != voiceChannelId) {
                  JdaWithRateLimit jda = getAvaibleJda();
                  Guild guild = getAvaibleGuild(jda);
                  Member member = guild.getMemberById(playerToMove.getPlayerData().getAccount().getDiscordId());

                  guild.moveVoiceMember(member, guild.getVoiceChannelById(voiceChannelId)).queue();
                  jda.addCall();
                  playerToMove.setActualVoiceChannelId(voiceChannelId);
                  playersAlreadyTreated.add(playerToMove.getPlayerData());
                }
              }
            }
          }
        }
      }
    }
  }

  private boolean isPlayerAlone(PlayerVoicePosition voiceChannelPlayer) {
    for(PlayerVoicePosition player : playersVoicePositions) {
      if(!player.equals(voiceChannelPlayer) && player.getActualVoiceChannelId() == voiceChannelPlayer.getActualVoiceChannelId()) {
        return false;
      }
    }
    return true;
  }

  private Guild getAvaibleGuild(JdaWithRateLimit jda) {
    return jda.getJda().getGuildById(GameData.getLobby().getGuild().getIdLong());
  }

  private long getVoiceMostPopulatedChannel(List<PlayerVoicePosition> playersInTheSameChannel) {

    List<Long> listIdChannel = new ArrayList<>();

    playersInTheSameChannel.forEach(e -> listIdChannel.add(e.getActualVoiceChannelId()));

    Map<Long, Long> occurrences = 
        listIdChannel.stream().collect(Collectors.groupingBy(w -> w, Collectors.counting()));

    long nbrPlayerInTheChannel = 0;
    long idMostPopulatedChannel = 0;

    for(long idChannel : listIdChannel) {
      long actualNmbrPlayer = occurrences.get(idChannel);
      if(nbrPlayerInTheChannel < actualNmbrPlayer) {
        idMostPopulatedChannel = idChannel;
        nbrPlayerInTheChannel = actualNmbrPlayer;
      }
    }

    return idMostPopulatedChannel;
  }

  private List<PlayerVoicePosition> getPlayersNeededInTheChannel(List<PlayerVoicePosition> playersAlreadyAdded, PlayerVoicePosition player) {

    playersAlreadyAdded.add(player);

    for(long userNearThePlayer : player.getUsersIdNearPlayer()) {
      PlayerVoicePosition playerNear = getPlayerVoicePosition(userNearThePlayer);
      if(!playersAlreadyAdded.contains(playerNear)) {
        playersAlreadyAdded.addAll(getPlayersNeededInTheChannel(playersAlreadyAdded, playerNear));}
    }

    return playersAlreadyAdded;
  }

  private void refreshPlayerVoiceLink() {
    for(PlayerData player : GameData.getPlayersInGame()) {
      for(PlayerData playerNear : GameData.getPlayersInGame()) {
        if(!player.equals(playerNear) && (!playersAlreadyTreated.contains(player) || !playersAlreadyTreated.contains(playerNear)
            && (player.isConnected() && playerNear.isConnected()))) {

          if(isPlayersInTheSameWorld(player, playerNear)
              && player.getAccount().getPlayer().getLocation().distanceSquared
              (playerNear.getAccount().getPlayer().getLocation()) < voiceDistance) {

            PlayerVoicePosition voiceChannelPlayer = getPlayerVoicePosition(player.getAccount().getDiscordId());
            PlayerVoicePosition voiceChannelPlayerNear = getPlayerVoicePosition(playerNear.getAccount().getDiscordId());

            if(voiceChannelPlayer != null && voiceChannelPlayerNear != null) {

              if(!voiceChannelPlayer.getUsersIdNearPlayer().contains(voiceChannelPlayerNear.getPlayerData().getAccount().getDiscordId())) {
                voiceChannelPlayer.getUsersIdNearPlayer().add(voiceChannelPlayerNear.getPlayerData().getAccount().getDiscordId());
              }

              if(!voiceChannelPlayerNear.getUsersIdNearPlayer().contains(voiceChannelPlayer.getPlayerData().getAccount().getDiscordId())){
                voiceChannelPlayerNear.getUsersIdNearPlayer().add(voiceChannelPlayer.getPlayerData().getAccount().getDiscordId());
              }
            }

          }else {
            PlayerVoicePosition voiceChannelPlayer = getPlayerVoicePosition(player.getAccount().getDiscordId());
            PlayerVoicePosition voiceChannelPlayerNear = getPlayerVoicePosition(playerNear.getAccount().getDiscordId());

            if(voiceChannelPlayer == null || voiceChannelPlayerNear == null) {
              continue;
            }

            if(voiceChannelPlayer.getUsersIdNearPlayer().contains(voiceChannelPlayerNear.getPlayerData().getAccount().getDiscordId())) {
              voiceChannelPlayer.getUsersIdNearPlayer().remove(voiceChannelPlayerNear.getPlayerData().getAccount().getDiscordId());
            }

            if(voiceChannelPlayerNear.getUsersIdNearPlayer().contains(voiceChannelPlayer.getPlayerData().getAccount().getDiscordId())) {
              voiceChannelPlayerNear.getUsersIdNearPlayer().remove(voiceChannelPlayer.getPlayerData().getAccount().getDiscordId());
            }
          }
        }
      }
    }
  }

  private boolean isPlayersInTheSameWorld(PlayerData player, PlayerData playerNear) {
    return player.getAccount().getPlayer().getWorld().getName()
        .equals(playerNear.getAccount().getPlayer().getWorld().getName());
  }

  private JdaWithRateLimit getAvaibleJda() {
    for(JdaWithRateLimit jda : jdaWorkers) {
      if(jda.refreshCalls()) {
        return jda;
      }
    }

    logger.warn("All workers has overload !");
    return jdaWorkers.get(0);
  }

  private VoiceChannel getEmptyVoiceChannel(Guild guild) {
    List<Long> voiceChannelAvailble = new ArrayList<>();
    voiceChannelAvailble.addAll(listIdVoiceChannel);

    for(PlayerVoicePosition playerVoiceChannel : playersVoicePositions) {
      voiceChannelAvailble.remove(playerVoiceChannel.getActualVoiceChannelId());
    }
    return guild.getVoiceChannelById(voiceChannelAvailble.get(0));
  }

  private PlayerVoicePosition getPlayerVoicePosition(long discordId) {
    for(PlayerVoicePosition playerVoicePosition : playersVoicePositions) {
      if(playerVoicePosition.getPlayerData().getAccount().getDiscordId() == discordId) {
        return playerVoicePosition;
      }
    }
    return null;
  }

  public static void setupVoiceChannels(List<PlayerData> playersInGame) {
    Guild guild = GameData.getLobby().getGuild();
    Role everyoneRole = guild.getPublicRole();

    List<Permission> permission = new ArrayList<>();

    permission.add(Permission.VOICE_CONNECT);
    permission.add(Permission.MESSAGE_READ);

    setIdDeadPlayer(guild.createVoiceChannel("Le channel des morts")
        .addPermissionOverride(everyoneRole, new ArrayList<>(), permission).complete().getIdLong());

    int i = 0;
    for(PlayerData player : playersInGame) {
      i++;
      Member member = guild.getMember(player.getAccount().getDiscordUser());
      guild.createVoiceChannel("Vocal " + i)
      .addPermissionOverride(everyoneRole, new ArrayList<>(), permission).queue(
          e -> {
            listIdVoiceChannel.add(e.getIdLong());
            guild.moveVoiceMember(member, e).queue();
            playersVoicePositions.add(new PlayerVoicePosition(player, e.getIdLong()));
          });
    }
  }

  public static long getIdDeadPlayer() {
    return idDeadPlayer;
  }

  public static void setIdDeadPlayer(long idDeadPlayer) {
    VocalSystemWorker.idDeadPlayer = idDeadPlayer;
  }

  public static List<JdaWithRateLimit> getJdaWorkers() {
    return jdaWorkers;
  }

}
