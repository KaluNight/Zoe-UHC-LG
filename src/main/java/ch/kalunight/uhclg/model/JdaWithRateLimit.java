package ch.kalunight.uhclg.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import ch.kalunight.uhclg.discord.audio.BotVoiceManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;

public class JdaWithRateLimit {
  
  private static final Duration RATE_LIMIT = Duration.ofSeconds(10);
  
  private static final int CALLS_LIMIT = 8;
  
  private JDA jda;
  
  private BotVoiceManager botVoiceManager;
  
  private List<LocalDateTime> callsDate = Collections.synchronizedList(new ArrayList<>());
  
  public JdaWithRateLimit(JDA jda) {
    this.jda = jda;
  }
  
  public void defineGuildVoiceHandler(Guild guild) {
    if(guild.getAudioManager().getSendingHandler() == null || !guild.getAudioManager().getSendingHandler().equals(botVoiceManager.getMusicManager().getSendHandler())){
      guild.getAudioManager().setSendingHandler(botVoiceManager.getMusicManager().getSendHandler());
      botVoiceManager.setAudioManager(guild.getAudioManager());
    }
  }
  
  public void addCall() {
    callsDate.add(LocalDateTime.now());
  }
  
  public boolean refreshCalls() {
    List<LocalDateTime> timesToRemove = new ArrayList<>();
    
    for(LocalDateTime time : callsDate) {
      if(LocalDateTime.now().minus(RATE_LIMIT).isAfter(time)) {
        timesToRemove.add(time);
      }
    }
     
    callsDate.removeAll(timesToRemove);
    
    return callsDate.size() < CALLS_LIMIT;
  }

  public JDA getJda() {
    return jda;
  }

  public BotVoiceManager getBotVoiceManager() {
    return botVoiceManager;
  }

  public void setBotVoiceManager(BotVoiceManager botVoiceManager) {
    this.botVoiceManager = botVoiceManager;
  }
}
