package ch.kalunight.uhclg.discord;

import ch.kalunight.uhclg.GameData;
import ch.kalunight.uhclg.model.JdaWithRateLimit;
import ch.kalunight.uhclg.model.PlayerData;
import ch.kalunight.uhclg.model.PlayerVoicePosition;
import ch.kalunight.uhclg.worker.VocalSystemWorker;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class DiscordEventListener extends ListenerAdapter {

  @Override
  public void onGuildVoiceLeave(GuildVoiceLeaveEvent event) {
    if(event.getChannelJoined() == null) {
      PlayerData player = GameData.getPlayerInGame(event.getEntity().getIdLong());
      if(player != null) {
        player.setDiscordVoiceConnected(false);
      }
    }
  }

  @Override
  public void onGuildVoiceJoin(GuildVoiceJoinEvent event) {
    if(event.getChannelLeft() == null) {
      PlayerData player = GameData.getPlayerInGame(event.getEntity().getIdLong());
      if(player != null) {
        player.setDiscordVoiceConnected(true);
        PlayerVoicePosition voicePosition = VocalSystemWorker.getPlayerVoicePosition(player.getAccount().getDiscordId());
        JdaWithRateLimit jdaRateLimit = VocalSystemWorker.getAvaibleJda();
        
        Guild guild = jdaRateLimit.getJda().getGuildById(event.getGuild().getIdLong());
        Member member = guild.getMemberById(event.getEntity().getIdLong());

        guild.moveVoiceMember(member, voicePosition.getActualVoiceChannel(guild)).queue();
        jdaRateLimit.addCall();
      }
    }
  }


}
