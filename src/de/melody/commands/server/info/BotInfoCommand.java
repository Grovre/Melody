package de.melody.commands.server.info;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import de.melody.Melody;
import de.melody.commands.types.ServerCommand;
import de.melody.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class BotInfoCommand implements ServerCommand{

	private int membersDeserving = 0;

	@SuppressWarnings("deprecation")
	@Override
	public void performCommand(Member m, TextChannel channel, Message message, Guild guild) {
		
		int serversRunning = channel.getJDA().getGuilds().size(); 
		for(Guild g1 : channel.getJDA().getGuilds()) {
			membersDeserving = membersDeserving + g1.getMemberCount();
		}
		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(0x23cba7);
		builder.setThumbnail(guild.getSelfMember().getUser().getAvatarUrl());
		
		Runtime r = Runtime.getRuntime();
		Properties prop = System.getProperties();
		String smallmemory = new String(r.totalMemory()+"");
		String bigmemory = new String(r.totalMemory()/ 1048576+"");
		
		builder.setDescription(Melody.INSTANCE.getMessageFormatter().format(guild.getIdLong(), "feedback.info.botinfo",
			"JDA",
			Melody.version,
			serversRunning,
			membersDeserving,
			Utils.getUserInt(),
			Utils.uptime(Melody.INSTANCE.playedmusictime),
			guild.getSelfMember().getAsMention())
				
			+" \n \n```OS: "+prop.getProperty("os.name")+"\n"
			+ "System Name: Node 3\n"
			+ "Cores: "+r.availableProcessors()+"\n"
			+ "CPU Arch: "+prop.getProperty("os.arch")+"\n"
			+ "Memory Usage: "+bigmemory+"."+smallmemory.substring(bigmemory.length())+"MB\n"
			+ "Uptime: "+Utils.uptime(Melody.INSTANCE.uptime)+"```");
		
		
			
		builder.setFooter("Made by Nebalus#1665 with <3");
		channel.sendMessage(builder.build()).queue();
		membersDeserving = 0;
	}
	
	public int getCooldown() {
		return 5;
	}
	
	public String botstart() {
		Date date = new Date(Melody.INSTANCE.startuptime);
		String DateFormat = new SimpleDateFormat("EEE, d MMM HH:mm:ss yyyy").format(date);
		return DateFormat;
	}
}