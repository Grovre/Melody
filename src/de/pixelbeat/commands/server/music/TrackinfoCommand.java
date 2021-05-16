package de.pixelbeat.commands.server.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import de.pixelbeat.PixelBeat;
import de.pixelbeat.commands.types.ServerCommand;
import de.pixelbeat.music.MusicController;
import de.pixelbeat.utils.Images;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class TrackinfoCommand implements ServerCommand{
	
	private PixelBeat pixelbeat = PixelBeat.INSTANCE;
	
	@Override
	public void performCommand(Member m, TextChannel channel, Message message, Guild guild) {
		pixelbeat.entityManager.getGuildEntity(guild.getIdLong()).setChannelId(channel.getIdLong());
		EmbedBuilder builder = new EmbedBuilder();
		builder.setImage("attachment://trackinfo.png");
		try {
			MusicController controller = PixelBeat.INSTANCE.playerManager.getController(guild.getIdLong());
			AudioTrack audiotrack = controller.getPlayer().getPlayingTrack();
			channel.sendFile(Images.tracktopng(audiotrack.getInfo().title,audiotrack.getPosition(),audiotrack.getDuration(),audiotrack.getInfo().author,guild.getIdLong(),controller.getQueue().currentplaying.getWhoQueued()), "trackinfo.png").embed(builder.build()).queue();	
		}catch (NullPointerException e) {
			channel.sendFile(Images.tracktopng(null,0,0,null,guild.getIdLong(),null), "trackinfo.png").embed(builder.build()).queue();	
		}
	}
}
