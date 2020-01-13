package ch.kalunight.uhclg.worker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
  
  private static double voiceDistance = 500d;

  @Override
  public void run() {
    playersAlreadyTreated.clear();

    if(GameData.getGameStatus().equals(GameStatus.IN_GAME)) {
      JdaWithRateLimit jda = getAvaibleJda();
      Guild guild = jda.getJda().getGuildById(GameData.getLobby().getGuild().getIdLong());
      for(PlayerData player : GameData.getPlayersInGame()) {
        for(PlayerData playerNear : GameData.getPlayersInGame()) {
          if(!player.equals(playerNear) && (!playersAlreadyTreated.contains(player) || !playersAlreadyTreated.contains(playerNear))) {

            if(player.getAccount().getPlayer().getLocation().distanceSquared(playerNear.getAccount().getPlayer().getLocation()) < voiceDistance) {

              PlayerVoicePosition voiceChannelPlayer = getPlayerVoicePosition(player.getAccount().getDiscordId());
              PlayerVoicePosition voiceChannelPlayerNear = getPlayerVoicePosition(playerNear.getAccount().getDiscordId());
              
              if(voiceChannelPlayer != null && voiceChannelPlayerNear != null) {
                if(voiceChannelPlayer.getActualVoiceChannelId() != voiceChannelPlayerNear.getActualVoiceChannelId()) {
                  if(isAValidChannel(voiceChannelPlayer.getActualVoiceChannelId())) {
                    Member member = guild.getMemberById(playerNear.getAccount().getDiscordId());
                    
                    voiceChannelPlayerNear.setActualVoiceChannelId(voiceChannelPlayer.getActualVoiceChannelId());
                    
                    guild.moveVoiceMember(member, voiceChannelPlayer.getActualVoiceChannel(guild)).queue();
                    jda.addCall();
                    playersAlreadyTreated.add(player);
                    playersAlreadyTreated.add(playerNear);
                  }else if(isAValidChannel(voiceChannelPlayerNear.getActualVoiceChannelId())) {
                    Member member = guild.getMemberById(playerNear.getAccount().getDiscordId());
                    
                    voiceChannelPlayer.setActualVoiceChannelId(voiceChannelPlayerNear.getActualVoiceChannelId());
                    
                    guild.moveVoiceMember(member, voiceChannelPlayerNear.getActualVoiceChannel(guild)).queue();
                    jda.addCall();
                    playersAlreadyTreated.add(player);
                    playersAlreadyTreated.add(playerNear);
                  }
                }
              }

            }else {
              PlayerVoicePosition voiceChannelPlayer = getPlayerVoicePosition(player.getAccount().getDiscordId());
              PlayerVoicePosition voiceChannelPlayerNear = getPlayerVoicePosition(playerNear.getAccount().getDiscordId());

              if(voiceChannelPlayer == null || voiceChannelPlayerNear == null) {
                continue;
              }
              
              if(voiceChannelPlayer.getActualVoiceChannelId() == voiceChannelPlayerNear.getActualVoiceChannelId()) {
                VoiceChannel emptyVoiceChannel = getEmptyVoiceChannel(guild);
                Member member = guild.getMemberById(playerNear.getAccount().getDiscordId());
                if(emptyVoiceChannel != null) {
                  voiceChannelPlayerNear.setActualVoiceChannelId(emptyVoiceChannel.getIdLong());
                  
                  guild.moveVoiceMember(member, emptyVoiceChannel).queue();
                  jda.addCall();
                  playersAlreadyTreated.add(player);
                  playersAlreadyTreated.add(playerNear);
                }
              }
            }
          }
        }
      }
    }
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

  private VoiceChannel getEmptyVoiceChannel(Guild guildId) {
    for(long voiceChannelId : listIdVoiceChannel) {
      VoiceChannel voiceChannel = guildId.getVoiceChannelById(voiceChannelId);
      if(voiceChannel.getMembers().isEmpty()) {
        return voiceChannel;
      }
    }
    return null; //Normally impossible
  }

  private boolean isAValidChannel(long voiceChannelPlayerId) {
    for(long idValidVoiceChannel : listIdVoiceChannel) {
      if(idValidVoiceChannel == voiceChannelPlayerId) {
        return true;
      }
    }
    return false;
  }

  public boolean isContainMemberInVocal(VoiceChannel voiceChannel, long discordId) {
    for(Member member : voiceChannel.getMembers()) {
      if(member.getIdLong() == discordId) {
        return true;
      }
    }
    return false;
  }


  public VoiceChannel getVoiceChannelByMember(Guild guild, long discordId) {
    PlayerVoicePosition playerVoicePosition = getPlayerVoicePosition(discordId);
    if(playerVoicePosition == null) {
      return null;
    }
    return guild.getVoiceChannelById(playerVoicePosition.getActualVoiceChannelId());
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
