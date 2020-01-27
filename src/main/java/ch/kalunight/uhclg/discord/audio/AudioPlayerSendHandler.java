package ch.kalunight.uhclg.discord.audio;

import java.nio.ByteBuffer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame;
import net.dv8tion.jda.api.audio.AudioSendHandler;

/**
 * This is a wrapper around AudioPlayer which makes it behave as an AudioSendHandler for JDA. As JDA calls canProvide before every call to
 * provide20MsAudio(), we pull the frame in canProvide() and use the frame we already pulled in provide20MsAudio().
 */
public class AudioPlayerSendHandler implements AudioSendHandler {
  private final AudioPlayer audioPlayer;
  private AudioFrame lastFrame;

  public AudioPlayerSendHandler(AudioPlayer audioPlayer) {
    this.audioPlayer = audioPlayer;
  }

  @Override
  public boolean canProvide() {
    lastFrame = audioPlayer.provide();
    return lastFrame != null;
  }

  @Override
  public ByteBuffer provide20MsAudio() {
    return ByteBuffer.wrap(lastFrame.getData());
  }

  @Override
  public boolean isOpus() {
    return true;
  }
}