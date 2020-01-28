package ch.kalunight.uhclg.discord.audio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.managers.AudioManager;

public class BotVoiceManager {

  private static final Logger logger = LoggerFactory.getLogger(BotVoiceManager.class);
  
  private AudioPlayerManager playerManager;
  private VoiceManager musicManager;
  private AudioManager audioManager;
  private VoiceChannel currentVoiceChannel;

  public BotVoiceManager() {
    this.playerManager = new DefaultAudioPlayerManager();
    AudioSourceManagers.registerLocalSource(playerManager);
    musicManager = new VoiceManager(playerManager);
  }

  public void loadAndPlay(final String voiceFile, final VoiceChannel voiceChannel) {

    playerManager.loadItemOrdered(musicManager, voiceFile, new AudioLoadResultHandler() {
      @Override
      public void trackLoaded(AudioTrack track) {
        play(musicManager, track, voiceChannel);
      }

      @Override
      public void playlistLoaded(AudioPlaylist playlist) {
        AudioTrack firstTrack = playlist.getSelectedTrack();

        if(firstTrack != null) {
          play(musicManager, firstTrack, voiceChannel);
        } else {
          for(AudioTrack track : playlist.getTracks()) {
            musicManager.scheduler.queue(track);
          }

          audioManager.openAudioConnection(voiceChannel);
          setCurrentVoiceChannel(voiceChannel);
        }
      }

      @Override
      public void noMatches() {
        logger.error("Voice issue : Fichier introuvable !");
      }

      @Override
      public void loadFailed(FriendlyException exception) {
        logger.error("Voice issue : Erreur lors du chargement : {}", exception.getMessage());
      }
    });
  }

  public void skipActualTrack() {
    musicManager.scheduler.nextTrack();
  }

  public void leaveVoiceChannel() {
    musicManager.player.stopTrack();
    musicManager.scheduler.deleteTheQueue();
    audioManager.closeAudioConnection();
    setCurrentVoiceChannel(null);
  }

  public void clearQueue() {
    musicManager.scheduler.deleteTheQueue();
  }

  private void play(VoiceManager musicManager, AudioTrack track, VoiceChannel voiceChannel) {
    audioManager.openAudioConnection(voiceChannel);
    setCurrentVoiceChannel(voiceChannel);
    
    musicManager.scheduler.queue(track);
  }

  public AudioPlayerManager getPlayerManager() {
    return playerManager;
  }

  public void setPlayerManager(AudioPlayerManager playerManager) {
    this.playerManager = playerManager;
  }

  public VoiceManager getMusicManager() {
    return musicManager;
  }

  public void setMusicManager(VoiceManager musicManager) {
    this.musicManager = musicManager;
  }

  public VoiceChannel getActualVoiceChannel() {
    return currentVoiceChannel;
  }

  public void setCurrentVoiceChannel(VoiceChannel currentVoiceChannel) {
    this.currentVoiceChannel = currentVoiceChannel;
  }

  public AudioManager getAudioManager() {
    return audioManager;
  }

  public void setAudioManager(AudioManager audioManager) {
    this.audioManager = audioManager;
  }
}
