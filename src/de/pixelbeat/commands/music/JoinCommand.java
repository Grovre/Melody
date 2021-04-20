package de.pixelbeat.commands.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;

import de.pixelbeat.PixelBeat;
import de.pixelbeat.commands.types.ServerCommand;
import de.pixelbeat.music.MusicController;
import de.pixelbeat.music.MusicUtil;
import de.pixelbeat.speechpackets.MessageFormatter;
import de.pixelbeat.utils.Emojis;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.managers.AudioManager;

public class JoinCommand implements ServerCommand{
	
	private MessageFormatter mf = PixelBeat.INSTANCE.getMessageFormatter();
	
	@Override
	public void performCommand(Member m, TextChannel channel, Message message) {
		MusicUtil.updateChannel(channel);
		GuildVoiceState state;
		VoiceChannel vc;
		if((state = m.getVoiceState()) != null && (vc = state.getChannel()) != null) {
			AudioManager manager = vc.getGuild().getAudioManager();
			manager.openAudioConnection(vc);
			MusicController controller = PixelBeat.INSTANCE.playerManager.getController(channel.getGuild().getIdLong());
			AudioPlayer player = controller.getPlayer();
			player.setPaused(false);
			if(player.getPlayingTrack() == null) {
				MusicUtil.getVoiceAfkTime.put(vc.getGuild().getIdLong(), 600l);
			}
		}else {
			EmbedBuilder builder = new EmbedBuilder();
			builder.setDescription(channel.getJDA().getEmoteById(Emojis.ANIMATED_TICK_RED).getAsMention()+" "+mf.format(channel.getGuild().getIdLong(), "feedback.music.user-not-in-vc"));
			MusicUtil.sendEmbledError(channel.getGuild().getIdLong(), builder);		
		}
	}
}
