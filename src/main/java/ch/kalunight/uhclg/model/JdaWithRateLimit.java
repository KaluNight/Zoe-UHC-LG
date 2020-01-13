package ch.kalunight.uhclg.model;

import java.time.Duration;
import java.time.LocalDateTime;import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.dv8tion.jda.api.JDA;

public class JdaWithRateLimit {
  
  private static final Duration RATE_LIMIT = Duration.ofSeconds(10);
  
  private static final int CALLS_LIMIT = 8;
  
  private JDA jda;
  
  private List<LocalDateTime> callsDate = Collections.synchronizedList(new ArrayList<>());
  
  public JdaWithRateLimit(JDA jda) {
    this.jda = jda;
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
  
}
