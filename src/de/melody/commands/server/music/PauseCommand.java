package de.melody.commands.server.music;

import java.util.List;

import de.melody.CommandManager.CommandType;
import de.melody.commands.types.ServerCommand;
import de.melody.core.Melody;
import de.melody.music.MusicUtil;
import de.melody.speechpackets.MessageFormatter;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;


public class PauseCommand implements ServerCommand{

	private Melody melody = Melody.INSTANCE;
	private MessageFormatter mf = melody.getMessageFormatter();
	
	@Override
	public void performCommand(Member m, TextChannel channel, Message message, Guild guild) {
		GuildVoiceState state;
		if((state = m.getGuild().getSelfMember().getVoiceState()) != null && state.getChannel() != null) {
			melody.playerManager.getController(guild.getIdLong()).getPlayer().setPaused(true);
		}else
			MusicUtil.sendEmbledError(guild, mf.format(guild, "feedback.music.bot-not-in-vc"));
	}

	@Override
	public List<String> getCommandPrefix() {
		return List.of("pause");
	}
	@Override
	public CommandType getCommandType() {
		return CommandType.MUSIC_COMMAND;
	}
	@Override
	public boolean isSlashCommandCompatible() {
		return false;
	}
	@Override
	public String getCommandDescription() {
		return null;
	}

	@Override
	public void performSlashCommand(Member member, MessageChannel channel, Guild guild, SlashCommandEvent event) {
		
	}
}
