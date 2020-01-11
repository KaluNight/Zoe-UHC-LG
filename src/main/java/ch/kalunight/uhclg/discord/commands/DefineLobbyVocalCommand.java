package ch.kalunight.uhclg.discord.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import ch.kalunight.uhclg.GameData;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;

public class DefineLobbyVocalCommand extends Command {

  public DefineLobbyVocalCommand() {
    this.name = "defineVoiceLobby";
    this.arguments = "";
    this.hidden = false;
    this.ownerCommand = true;
    this.guildOnly = true;
  }

  @Override
  protected void execute(CommandEvent event) {

    VoiceChannel selectedVoiceChannel = null;
    
    for(VoiceChannel voiceChannel : event.getGuild().getVoiceChannels()) {
      for(Member member : voiceChannel.getMembers()) {
        if(member.getIdLong() == event.getAuthor().getIdLong()) {
          selectedVoiceChannel = voiceChannel;
          break;
        }
      }
      if(selectedVoiceChannel != null) {
        break;
      }
    }
    
    if(selectedVoiceChannel != null) {
      GameData.setLobby(selectedVoiceChannel);
      event.reply("Le lobby vocal a bien été défini !");
    }else {
      event.reply("Vous devez être dans un canal vocal pour pouvoir le définir !");
    }
    
  }

}
