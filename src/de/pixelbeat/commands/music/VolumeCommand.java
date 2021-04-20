package de.pixelbeat.commands.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;

import de.pixelbeat.PixelBeat;
import de.pixelbeat.commands.types.ServerCommand;
import de.pixelbeat.music.MusicController;
import de.pixelbeat.music.MusicUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class VolumeCommand implements ServerCommand{

	//private MessageFormatter mf = PixelBeat.INSTANCE.getMessageFormatter();
	
	@Override
	public void performCommand(Member m, TextChannel channel, Message message) {
		String[] args = message.getContentDisplay().split(" ");
		MusicController controller = PixelBeat.INSTANCE.playerManager.getController(channel.getGuild().getIdLong());
		AudioPlayer player = controller.getPlayer();
		MusicUtil.updateChannel(channel);
		if(args.length == 1) {
			EmbedBuilder builder = new EmbedBuilder();
			builder.setDescription("The volume from the bot: " + MusicUtil.getVolume(channel.getGuild().getIdLong()));
			MusicUtil.sendEmbled(channel.getGuild().getIdLong(), builder);
		}else {
			try {
				int amount = Integer.parseInt(args[1]);			
				if(amount <= 200) {
					if(amount >= 1) {
						player.setVolume(amount);
						EmbedBuilder builder = new EmbedBuilder();
						MusicUtil.setVolume(channel.getGuild().getIdLong(), amount);
						builder.setDescription("The volume from the bot has been set to " + amount);
						MusicUtil.sendEmbled(channel.getGuild().getIdLong(), builder);
					}else {
						MusicUtil.sendEmbledError(channel.getGuild().getIdLong(), m.getAsMention() + " the min volume you can use is 1!");
					}
				}else {
					MusicUtil.sendEmbledError(channel.getGuild().getIdLong(), m.getAsMention() + " the max volume you can use is 200!");
				}		
			}catch(NumberFormatException e) {
				MusicUtil.sendEmbledError(channel.getGuild().getIdLong(), m.getAsMention() + " Please choose a number between 1-200!");
			}
		}
	}
}
