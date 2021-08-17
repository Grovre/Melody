package de.melody.listeners;

import java.util.List;
import java.util.concurrent.TimeUnit;

import de.melody.Melody;
import de.melody.entities.GuildEntity;
import de.melody.speechpackets.MessageFormatter;
import de.melody.utils.ConsoleLogger;
import de.melody.utils.Emojis;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class CommandListener extends ListenerAdapter{

	private Melody melody = Melody.INSTANCE;
	private MessageFormatter mf = melody.getMessageFormatter();
	
	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		String message = event.getMessage().getContentDisplay();
		if(event.isFromType(ChannelType.TEXT)) {
			List<User> MentionedUsers = event.getMessage().getMentionedUsers();
			TextChannel channel = event.getTextChannel();
			Guild guild = event.getGuild();
			GuildEntity ge = melody.entityManager.getGuildEntity(guild.getIdLong());
			
			if(!event.getAuthor().isBot()) {
				if(message.startsWith(ge.getPrefix())) {
					int count = 0;
					for (int i = 0; i < ge.getPrefix().length(); i++) {
						count++;
					}
					String[] args = message.substring(count).split(" ");
					if(ge.canRevokeCommand()) {
						event.getMessage().delete().queueAfter(5, TimeUnit.SECONDS);
					}
					if(args.length > 0){
						if(!melody.getCmdMan().performServer(args[0], event.getMember(), channel, event.getMessage(), event.getGuild())) {
							channel.sendMessage(event.getJDA().getEmoteById(Emojis.ANIMATED_THINKING_EMOJI).getAsMention()+" "+mf.format(guild.getIdLong(), "feedback.info.unknown-command",ge.getPrefix())).queue();
						}
					}
				}else if(MentionedUsers.contains(channel.getJDA().getSelfUser())) {
					event.getChannel().sendMessage(mf.format(guild.getIdLong(), "feedback.info.prefix",ge.getPrefix())).queue();
				}
			}
		}else if(event.isFromType(ChannelType.PRIVATE)) {
			ConsoleLogger.info("Test", message);
		}
	}	
}
	


