package ch.kalunight.uhclg.discord;

import ch.kalunight.uhclg.GameData;
import ch.kalunight.uhclg.model.PlayerData;
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
      }
    }
  }


}
