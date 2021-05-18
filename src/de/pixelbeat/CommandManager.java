package de.pixelbeat;

import java.util.concurrent.ConcurrentHashMap;

import de.pixelbeat.commands.ConfigCommand;
import de.pixelbeat.commands.info.BotInfoCommand;
import de.pixelbeat.commands.info.GithubCommand;
import de.pixelbeat.commands.info.InviteCommand;
import de.pixelbeat.commands.info.PingCommand;
import de.pixelbeat.commands.music.JoinCommand;
import de.pixelbeat.commands.music.LeaveCommand;
import de.pixelbeat.commands.music.LoopCommand;
import de.pixelbeat.commands.music.PauseCommand;
import de.pixelbeat.commands.music.PlayCommand;
import de.pixelbeat.commands.music.PlaylistCommand;
import de.pixelbeat.commands.music.QueueCommand;
import de.pixelbeat.commands.music.ResumeCommand;
import de.pixelbeat.commands.music.ShuffelCommand;
import de.pixelbeat.commands.music.StayCommand;
import de.pixelbeat.commands.music.NextCommand;
import de.pixelbeat.commands.music.StopCommand;
import de.pixelbeat.commands.music.TrackinfoCommand;
import de.pixelbeat.commands.music.VolumeCommand;
import de.pixelbeat.commands.types.ServerCommand;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class CommandManager {

	public ConcurrentHashMap<String, ServerCommand> commands;
	
	public CommandManager() {
		this.commands = new ConcurrentHashMap<>();
		
		this.commands.put("join",new JoinCommand());
		this.commands.put("j",new JoinCommand());
		
		this.commands.put("p",new PlayCommand());
		this.commands.put("play",new PlayCommand());
		
		this.commands.put("playlist",new PlaylistCommand());
		this.commands.put("pl",new PlaylistCommand());
		
		this.commands.put("v",new VolumeCommand());
		this.commands.put("volume",new VolumeCommand());
		this.commands.put("vol",new VolumeCommand());
		
		this.commands.put("pause",new PauseCommand());
		
		this.commands.put("resume",new ResumeCommand());
		this.commands.put("unpause",new ResumeCommand());
		
		this.commands.put("stop",new StopCommand());
		
		this.commands.put("l",new LeaveCommand());
		this.commands.put("leave",new LeaveCommand());
		
		this.commands.put("trackinfo",new TrackinfoCommand());
		this.commands.put("ti",new TrackinfoCommand());
		this.commands.put("nowplaying",new TrackinfoCommand());
		
		this.commands.put("queue",new QueueCommand());
		this.commands.put("q",new QueueCommand());
	
		this.commands.put("skip", new NextCommand());
		this.commands.put("s", new NextCommand());
		this.commands.put("next", new NextCommand());
		this.commands.put("n", new NextCommand());
		
		this.commands.put("botinfo",new BotInfoCommand());
		this.commands.put("ping",new PingCommand());
		this.commands.put("config",new ConfigCommand());
		this.commands.put("invite",new InviteCommand());
		this.commands.put("github",new GithubCommand());
		
		this.commands.put("shuffel",new ShuffelCommand());
		
		this.commands.put("loop",new LoopCommand());
		
		//Funktioniert nicht auf einen raspberry pi
		//this.commands.put("speed",new SpeedCommand());
		
		this.commands.put("24/7", new StayCommand());
		this.commands.put("247", new StayCommand());
	}
	
	public boolean perform(String command, Member m, TextChannel channel, Message message, Guild guild) {	
		ServerCommand cmd;
		if((cmd = this.commands.get(command.toLowerCase())) != null) {
			cmd.performCommand(m, channel, message, guild);
			return true;
		}
		
		return false;
	}
}