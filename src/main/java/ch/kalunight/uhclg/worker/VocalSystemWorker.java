package ch.kalunight.uhclg.worker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.kalunight.uhclg.GameData;
import ch.kalunight.uhclg.model.GameStatus;
import ch.kalunight.uhclg.model.PlayerData;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.VoiceChannel;

public class VocalSystemWorker implements Runnable {

  private static double voiceDistance = 500d;

  private static final List<Long> listIdVoiceChannel = Collections.synchronizedList(new ArrayList<>());

  private static final List<PlayerData> playersAlreadyTreated = Collections.synchronizedList(new ArrayList<>());

  private static long idDeadPlayer;

  @Override
  public void run() {
    playersAlreadyTreated.clear();

    if(GameData.getGameStatus().equals(GameStatus.IN_GAME)) {
      Guild guild = GameData.getLobby().getGuild();
      for(PlayerData player : GameData.getPlayersInGame()) {
        for(PlayerData playerNear : GameData.getPlayersInGame()) {
          if(!player.equals(playerNear) && (!playersAlreadyTreated.contains(player) || !playersAlreadyTreated.contains(playerNear))) {

            if(player.getAccount().getPlayer().getLocation().distanceSquared(playerNear.getAccount().getPlayer().getLocation()) < voiceDistance) {

              VoiceChannel voiceChannelPlayer = getVoiceChannelByMember(guild, player.getAccount().getDiscordId());
              VoiceChannel voiceChannelPlayerNear = getVoiceChannelByMember(guild, playerNear.getAccount().getDiscordId());
              
              if(voiceChannelPlayer != null && voiceChannelPlayerNear != null) {
                if(voiceChannelPlayer.getIdLong() != voiceChannelPlayerNear.getIdLong()) {
                  if(isAValidChannel(voiceChannelPlayer)) {
                    Member member = guild.getMember(playerNear.getAccount().getDiscordUser());
                    
                    guild.moveVoiceMember(member, voiceChannelPlayer).complete();
                    playersAlreadyTreated.add(player);
                    playersAlreadyTreated.add(playerNear);
                  }else if(isAValidChannel(voiceChannelPlayerNear)) {
                    Member member = guild.getMember(player.getAccount().getDiscordUser());
                    guild.moveVoiceMember(member, voiceChannelPlayerNear).complete();
                    playersAlreadyTreated.add(player);
                    playersAlreadyTreated.add(playerNear);
                  }
                }
              }

            }else {
              VoiceChannel voiceChannelPlayer = getVoiceChannelByMember(guild, player.getAccount().getDiscordId());
              VoiceChannel voiceChannelPlayerNear = getVoiceChannelByMember(guild, playerNear.getAccount().getDiscordId());

              if(voiceChannelPlayer.getIdLong() == voiceChannelPlayerNear.getIdLong()) {
                VoiceChannel emptyVoiceChannel = getEmptyVoiceChannel(guild);
                Member member = guild.getMember(playerNear.getAccount().getDiscordUser());
                if(emptyVoiceChannel != null) {
                  guild.moveVoiceMember(member, emptyVoiceChannel).complete();
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

  private VoiceChannel getEmptyVoiceChannel(Guild guildId) {
    for(long voiceChannelId : listIdVoiceChannel) {
      VoiceChannel voiceChannel = guildId.getVoiceChannelById(voiceChannelId);
      if(voiceChannel.getMembers().isEmpty()) {
        return voiceChannel;
      }
    }
    return null; //Normally impossible
  }

  private boolean isAValidChannel(VoiceChannel voiceChannelPlayer) {
    for(long idValidVoiceChannel : listIdVoiceChannel) {
      if(idValidVoiceChannel == voiceChannelPlayer.getIdLong()) {
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
    for(VoiceChannel voiceChannel : guild.getVoiceChannels()) {
      for(Member member : voiceChannel.getMembers()) {
        if(member.getIdLong() == discordId) {
          return voiceChannel;
        }
      }
    }

    for(Member member : GameData.getLobby().getMembers()) {
      if(member.getIdLong() == discordId) {
        return movePlayerInAEmptyChannel(guild, discordId);
      }
    }

    return null;
  }

  private VoiceChannel movePlayerInAEmptyChannel(Guild guild, long discordId) {

    VoiceChannel voiceChannel = getEmptyVoiceChannel(guild);
    guild.moveVoiceMember(guild.getMemberById(discordId), voiceChannel).complete();
    
    return voiceChannel;
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
          });
    }
  }

  public static long getIdDeadPlayer() {
    return idDeadPlayer;
  }

  public static void setIdDeadPlayer(long idDeadPlayer) {
    VocalSystemWorker.idDeadPlayer = idDeadPlayer;
  }

}
