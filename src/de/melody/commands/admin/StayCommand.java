package de.melody.commands.admin;

import de.melody.core.Constants;
import de.melody.core.Melody;
import de.melody.entities.GuildEntity;
import de.melody.speechpackets.MessageFormatter;
import de.melody.tools.commandbuilder.CommandPermission;
import de.melody.tools.commandbuilder.CommandType;
import de.melody.tools.commandbuilder.ServerCommand;
import de.melody.tools.messenger.Messenger;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;


public class StayCommand implements ServerCommand{

	private Melody melody = Melody.INSTANCE;
	private MessageFormatter mf = melody._messageformatter;
	
	@Override
	public void performCommand(Member member, TextChannel channel, Message message, Guild guild, GuildEntity guildentity) {
		if(guildentity.is24_7()) {
			guildentity.set24_7(false);
			Messenger.sendMessageEmbed(channel, mf.format(guild, "command.staymode.disabled")).queue();
		}else {
			guildentity.set24_7(true);
			Messenger.sendMessageEmbed(channel, mf.format(guild, "command.staymode.enabled")).queue();
		}
	}

	@Override
	public String[] getCommandPrefix() {
		return new String[] {"24/7","247"};
	}
	
	@Override
	public CommandType getCommandType() {
		return CommandType.CHAT;
	}

	@Override
	public String getCommandDescription() {
		return "Toggles "+Constants.BUILDNAME+" to stay 24/7 in the voice channel";
	}

	@Override
	public void performSlashCommand(Member member, MessageChannel channel, Guild guild, GuildEntity guildentity, SlashCommandEvent event) {
		
	}

	@Override
	public OptionData[] getCommandOptions() {
		return null;
	}
	@Override
	public CommandPermission getMainPermmision() {
		return CommandPermission.ADMIN;
	}
}