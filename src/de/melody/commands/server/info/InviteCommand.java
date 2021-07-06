package de.melody.commands.server.info;

import de.melody.Melody;
import de.melody.commands.types.ServerCommand;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class InviteCommand implements ServerCommand{

	@Override
	public void performCommand(Member m, TextChannel channel, Message message, Guild guild) {
		channel.sendMessage(Melody.INSTANCE.getMessageFormatter().format(channel.getGuild().getIdLong(), "feedback.info.invite", m.getAsMention(), "https://nebalus.ga/melody/invite")).queue();
	}
}
