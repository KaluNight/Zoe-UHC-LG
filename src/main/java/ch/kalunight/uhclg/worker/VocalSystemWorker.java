package ch.kalunight.uhclg.worker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.kalunight.uhclg.GameData;
import ch.kalunight.uhclg.model.PlayerData;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.VoiceChannel;

public class VocalSystemWorker implements Runnable {

  private static final List<Long> listIdVoiceChannel = Collections.synchronizedList(new ArrayList<>());
  
  private static long idDeadPlayer;
  
  @Override
  public void run() {
    
  }
  
  public void setupVoiceChannels(List<PlayerData> playersInGame) {
    Guild guild = GameData.getLobby().getGuild();
    Role everyoneRole = guild.getPublicRole();
    
    List<Permission> permission = new ArrayList<>();
    
    permission.add(Permission.VOICE_CONNECT);
    permission.add(Permission.MESSAGE_READ);
    
    int i = 0;
    for(PlayerData player : playersInGame) {
      i++;
      VoiceChannel voiceChannel = guild.createVoiceChannel("Vocal " + i)
          .addPermissionOverride(guild.getPublicRole(), new ArrayList<>(), permission).complete();
      listIdVoiceChannel.add(voiceChannel.getIdLong());
      
      Member member = guild.getMember(player.getAccount().getDiscordUser());
      
      guild.moveVoiceMember(member, voiceChannel).queue();
    }
  }

  public static long getIdDeadPlayer() {
    return idDeadPlayer;
  }

  public static void setIdDeadPlayer(long idDeadPlayer) {
    VocalSystemWorker.idDeadPlayer = idDeadPlayer;
  }

}
