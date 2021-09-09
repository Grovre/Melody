package de.melody.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.Random;

import javax.security.auth.login.LoginException;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;

import de.melody.LiteSQL;
import de.melody.Secure;
import de.melody.commands.server.ConfigCommand;
import de.melody.commands.server.info.InfoCommand;
import de.melody.commands.server.info.InviteCommand;
import de.melody.commands.server.info.PingCommand;
import de.melody.commands.server.music.BackCommand;
import de.melody.commands.server.music.FastforwardCommand;
import de.melody.commands.server.music.JoinCommand;
import de.melody.commands.server.music.LeaveCommand;
import de.melody.commands.server.music.LoopCommand;
import de.melody.commands.server.music.PauseCommand;
import de.melody.commands.server.music.PlayCommand;
import de.melody.commands.server.music.PlaylistCommand;
import de.melody.commands.server.music.QueueCommand;
import de.melody.commands.server.music.ResumeCommand;
import de.melody.commands.server.music.RewindCommand;
import de.melody.commands.server.music.SeekCommand;
import de.melody.commands.server.music.ShuffelCommand;
import de.melody.commands.server.music.SkipCommand;
import de.melody.commands.server.music.StayCommand;
import de.melody.commands.server.music.StopCommand;
import de.melody.commands.server.music.TrackinfoCommand;
import de.melody.commands.server.music.VolumeCommand;
import de.melody.commands.server.slash.PrefixCommand;
import de.melody.entities.EntityManager;
import de.melody.entities.GuildEntity;
import de.melody.entities.UserEntity;
import de.melody.listeners.CommandListener;
import de.melody.listeners.ReactListener;
import de.melody.music.MusicController;
import de.melody.music.MusicUtil;
import de.melody.music.PlayerManager;
import de.melody.speechpackets.MessageFormatter;
import de.melody.utils.Emoji;
import de.melody.utils.SpotifyUtils;
import de.melody.utils.Utils;
import de.nebalus.botbuilder.command.CommandManager;
import de.nebalus.botbuilder.core.BotBuilder;
import de.nebalus.botbuilder.core.BotCore;
import de.nebalus.botbuilder.console.ConsoleLogger;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

public class Melody implements BotCore{
	public static Melody INSTANCE;
	public ShardManager shardMan;
	private MessageFormatter messageformatter;
	private Thread loop;
	private Thread auto_save;
	public AudioPlayerManager audioPlayerManager;
	public PlayerManager playerManager;
	public EntityManager entityManager;
	public LiteSQL database;
	private BotBuilder botbuilder;
	
	public SpotifyUtils spotifyutils;
	
	public int uptime; 
	public long playedmusictime;
	
	public static void main(String[] args) {		
		try {
			new Melody();
		} catch (LoginException | IllegalArgumentException | InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private Melody() throws LoginException, IllegalArgumentException, InterruptedException {
		/*
		String fonts[] = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
		for (int i = 0; i < fonts.length; i++){
			System.out.println(fonts[i]);
		}
		*/
		final Long startupMillis = System.currentTimeMillis();
		INSTANCE = this;
		database = new LiteSQL();
		spotifyutils = new SpotifyUtils(Secure.SPOTIFY_CLIENTID, Secure.SPOTIFY_CLIENTSECRET);
		messageformatter = new MessageFormatter();
	
		DefaultShardManagerBuilder builder = DefaultShardManagerBuilder.createDefault(Secure.TOKEN);
		configureMemoryUsage(builder); 
	
		builder.addEventListeners(new CommandListener() , new MusicUtil(), new ReactListener());
		builder.setActivity(Activity.playing("booting myself..."));
		
		this.audioPlayerManager = new DefaultAudioPlayerManager();
		this.playerManager = new PlayerManager();
		this.entityManager = new EntityManager();
		
		this.shardMan = builder.build();
		
		for(JDA jda : this.shardMan.getShards()) {
			jda.awaitReady();
		}
		
		this.botbuilder = new BotBuilder(this);
		botbuilder.getCommandManager().registerCommands(new JoinCommand(), new FastforwardCommand(), new RewindCommand(), new SeekCommand(),
				new PlayCommand(), new PlaylistCommand(), new VolumeCommand(), new PauseCommand(), new ResumeCommand(),
				new StopCommand(), new LeaveCommand(), new TrackinfoCommand(), new QueueCommand(), new SkipCommand(),
				new InfoCommand(), new PingCommand(), new ConfigCommand(), new InviteCommand(), new ShuffelCommand(),
				new LoopCommand(), new StayCommand(), new BackCommand(), new PrefixCommand());
		
		AudioSourceManagers.registerRemoteSources(audioPlayerManager);
		AudioSourceManagers.registerLocalSource(audioPlayerManager);
		audioPlayerManager.getConfiguration().setFilterHotSwapEnabled(true);
		
		uptime = 0;
		Utils.loadSystemData(this);
		
		runLoop();
		//shutdown();
		ConsoleLogger.info("Bot", "Melody online! ("+(System.currentTimeMillis() - startupMillis)+"ms)");
	}
	
	
	public void runLoop() {
		this.loop = new Thread(() -> {	
			Long time = System.currentTimeMillis() + 1000;
			while(true) {		
				if(System.currentTimeMillis() > time) {
					try{
						time = System.currentTimeMillis()+1000;
						MusicUtil.onRefreshAutoDisabler(shardMan);
						if(database.isConnected()) {
							scanGuilds();	
						}
						spotifyutils.update();
						onStatusUpdate();
						uptime++;
						onCaculatingPlayedMusik();
					}catch(Exception e){
						ConsoleLogger.warning("Thread", "The current action from the LoopThread has been aborted");
						e.printStackTrace();
					}
				}
			}
		});
		this.loop.setName("LoopThread");
		this.loop.start();
		
		this.auto_save = new Thread(() -> {		
			Long time = System.currentTimeMillis() + 150000;
			while(true) {
				if(System.currentTimeMillis() > time) {
					ConsoleLogger.info("Auto-Saver", "Starting to export cache");
					try{
						time = System.currentTimeMillis()+ 150000;
						boolean export = false;
						for(Entry<Long, GuildEntity> entry : entityManager.guildentity.entrySet()) {
							GuildEntity value = entry.getValue();
							if(value.getExpireTime() <= System.currentTimeMillis()) {
								entityManager.removeGuildEntity(value);
								export = true;
							}else if(value.getNeedToExport()) {
								value.export();
								export = true;
							}
						}
						for(Entry<Long, UserEntity> entry : entityManager.userentity.entrySet()) {
							UserEntity value = entry.getValue();
							if(value.getExpireTime() <= System.currentTimeMillis()) {
								entityManager.removeUserEntity(value);
								export = true;
							}else if(value.getNeedToExport()) {
								value.export();
								export = true;
							}
						}
						Utils.saveSystemData(this);
						if(export) {
							ConsoleLogger.info("Auto-Saver", "Export ended sucessfully");
						}else {
							ConsoleLogger.info("Auto-Saver", "There is nothing to export to the database");
						}
					}catch(Exception e){
						ConsoleLogger.warning("Thread", "The current action from the Auto-Saver has been aborted");
						e.printStackTrace();
					}
				}
			}
		});
		this.auto_save.setName("Auto-Saver");
		this.auto_save.setPriority(Thread.MAX_PRIORITY);
		this.auto_save.start();
	}
	
	public void onCaculatingPlayedMusik() {
		for(Guild g : shardMan.getGuilds()) {
			GuildVoiceState state;
			if(g.getSelfMember() != null && (state = g.getSelfMember().getVoiceState()) != null) {
				VoiceChannel vc;
				if((vc = state.getChannel()) != null) {
					MusicController controller = playerManager.getController(vc.getGuild().getIdLong());	
					AudioPlayer player = controller.getPlayer();
					if(player.getPlayingTrack() != null && !player.isPaused()) {
						int users = 0;
						for(Member m : vc.getMembers()) {
							if(!m.getUser().isBot()) {
								users++;
								
								//onCaculatingHeardMusic
								UserEntity ue = entityManager.getUserEntity(m.getUser());
								Long listendata = ue.getHeardTime();
								listendata++;
								ue.setHeardTime(listendata);
								//
							}
						}
						if(users > 0) {
							playedmusictime++;
						}
					}
				}
			}
		}
	}
	
	int nextStatusUpdate = 10;
	public void onStatusUpdate() {
		if(nextStatusUpdate <= 0) {
			Random rand = new Random();
			int i = rand.nextInt(3);
			shardMan.getShards().forEach(jda ->{
				switch(i) {
				case 0:
					int musicguilds = 0;
					for(Guild g : shardMan.getGuilds()) {
						if(g.getSelfMember().getVoiceState().getChannel() != null) {
							musicguilds++;
						}
					}
					jda.getPresence().setActivity(Activity.streaming("music on " +musicguilds+" server"+(musicguilds < 1 ? "s": "") +"!","https://twitch.tv/nebalus"));
					break;
				case 1:
					jda.getPresence().setActivity(Activity.listening("m!help | "+Config.BUILDVERSION));
					break;
				case 2:
					jda.getPresence().setActivity(Activity.listening("@"+jda.getSelfUser().getName()));
					break;
				}
			});
			nextStatusUpdate = 15;
		}else {
			nextStatusUpdate--;
		}
	}
	
	Integer guildScannerCooldown = 0;
	ArrayList<Long> guildCache = new ArrayList<>();
	public void scanGuilds() {
		if(guildScannerCooldown <= 10) {
			for(Guild g : shardMan.getGuilds()) {
				if(!guildCache.contains(g.getIdLong()) && !Utils.doesGuildExist(g.getIdLong())) {		
					boolean mentioned = false;
					for(TextChannel tc : g.getTextChannels()) {
						if(!mentioned) {
							try {
								tc.sendMessage("Hello everybody, i'm "+g.getJDA().getSelfUser().getAsMention()+" "+g.getJDA().getEmoteById(Emoji.HEY_GUYS).getAsMention()+"\n"
										+ " \n"
										+ " `-` My prefix on "+g.getName()+" is `m!`\n"
										+ " `-` If you do not understand how I work then you can see all my commands by typing `m!help`\n"
										+ " `-` When you dont like something in my config then you can easyly change it by typing `m!config help`\n"
										+ " \n"
										+ "**Otherwise have fun listening to the music from my service** "+ Emoji.MUSIC_NOTE+" \n"
										+ "PS: Thanks a lot for your support, that you added me to your discord server! "+g.getJDA().getEmoteById(Emoji.ANIMATED_HEARTS).getAsMention()).queue();
								mentioned = true;
								//loads the guild in the database
								entityManager.getGuildEntity(g);
								guildCache.add(tc.getIdLong());
							}catch(InsufficientPermissionException e) {}
						}
					}
				}else {
					guildCache.add(g.getIdLong());
				}	
			}
			guildScannerCooldown = 0;
		}
		guildScannerCooldown++;
	}
	
	public void shutdown() {
		new Thread(() -> {		
			String line = "";
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			try {
				while((line = reader.readLine()) != null) {
					if(line.equalsIgnoreCase("stop")) {
						if(shardMan != null) {
							database.disconnect();					
							shardMan.setStatus(OnlineStatus.OFFLINE);
							shardMan.shutdown();
							ConsoleLogger.info("Bot", "Melody offline!");
						}
						if(loop != null) {
							loop.interrupt();
						}	
						if(auto_save != null) {
							auto_save.interrupt();
						}
						reader.close();
						break;
					}else {
						ConsoleLogger.info("Command", "Use \"Stop\" to shutdown!");
					}
				}
			}catch(IOException e) {}			
		}).start();	
	}
	
	public void configureMemoryUsage(DefaultShardManagerBuilder builder) {
	    // Disable cache for member activities (streaming/games/spotify)
	    builder.disableCache(CacheFlag.ACTIVITY);

	    // Only cache members who are either in a voice channel or owner of the guild
	    builder.setMemberCachePolicy(MemberCachePolicy.VOICE.or(MemberCachePolicy.ONLINE));

	    // Disable member chunking on startup
	    builder.setChunkingFilter(ChunkingFilter.NONE);
	    
	    // Enable presence updates 
	    builder.enableIntents(GatewayIntent.GUILD_MEMBERS);
	    builder.setLargeThreshold(50);
	}
	
	public CommandManager getCmdMan() {
		return botbuilder.getCommandManager();
	}

    public MessageFormatter getMessageFormatter() {
		return messageformatter;
    }
    
    public LiteSQL getDatabase() {
    	return database;
    }

	@Override
	public ShardManager getShardManager() {
		return shardMan;
	}



}
