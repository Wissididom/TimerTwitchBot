package tv.twitch.bot;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class TimerTwitchBot {
	protected String botUsername;
	protected String channelName;
	protected String oauthToken;
	private ITwitchMessageHandler messageHandler;
	
	public static void main(String[] args) throws IOException {
		new TimerTwitchBot(System.getProperty("user.dir") + File.separator + "config.properties").start();
	}
	
	public TimerTwitchBot(File configFile) {
		if (!configFile.exists()) {
			Properties p = new Properties();
			p.setProperty("bot_username", "x");
			p.setProperty("channel_name", "x");
			p.setProperty("oauth_token", "x");
			try {
				p.store(new FileOutputStream(configFile), null);
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
		}
		try {
			Properties p = new Properties();
			p.load(new FileInputStream(configFile));
			this.botUsername = p.getProperty("bot_username");
			this.channelName = p.getProperty("channel_name");
			this.oauthToken = p.getProperty("oauth_token");
			if (this.oauthToken.contains("oauth:"))
				this.oauthToken = this.oauthToken.replace("oauth:", "");
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		this.messageHandler = new DefaultTwitchMessageHandler(this.oauthToken);
		((DefaultTwitchMessageHandler) this.messageHandler).putChatCommand("!usage", (String oauth, OutputStream os, String message) -> Commands.usage(os, message));
		((DefaultTwitchMessageHandler) this.messageHandler).putChatCommand("!timer", (String oauth, OutputStream os, String message) -> Commands.starttimer(os, message));
		((DefaultTwitchMessageHandler) this.messageHandler).putChatCommand("!showtimer", (String oauth, OutputStream os, String message) -> Commands.showtimer(os, message));
		((DefaultTwitchMessageHandler) this.messageHandler).putChatCommand("!stoptimer", (String oauth, OutputStream os, String message) -> Commands.stoptimer(os, message));
		((DefaultTwitchMessageHandler) this.messageHandler).putChatCommand("!quitbot", (String oauth, OutputStream os, String message) -> Commands.quitbot(os, message));
	}
	
	public TimerTwitchBot(String configFile) {
		this(new File(configFile));
	}
	
	public void start() throws IOException {
		SSLSocket irc = (SSLSocket) SSLSocketFactory.getDefault().createSocket("irc.chat.twitch.tv", 6697);
		final InputStream is = irc.getInputStream();
		final OutputStream os = irc.getOutputStream();
		TimerTwitchBot.send(os, "PASS oauth:" + this.oauthToken);
		TimerTwitchBot.send(os, "NICK " + this.botUsername);
		TimerTwitchBot.send(os, "JOIN #" + this.channelName);
		TimerTwitchBot.send(os, "CAP REQ :twitch.tv/membership twitch.tv/tags twitch.tv/commands");
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				os.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}));
		while (true) {
			byte[] data = new byte[1024];
			is.read(data, 0, 1024);
			String rawMessage = new String(data, StandardCharsets.UTF_8);
			for (String line : rawMessage.split("\r?\n")) {
				if (line.trim().equals(""))
					continue;
				System.out.println("line:" + line);
				if (line.startsWith("PING :tmi.twitch.tv")) {
					TimerTwitchBot.sendPong(os);
				} else {
					if (!line.contains(" "))
						continue;
					this.messageHandler.handle(os, line);
				}
			}
		}
	}
	
	public ITwitchMessageHandler getTwitchMessageHandler() {
		return this.messageHandler;
	}
	
	public TimerTwitchBot setTwitchMessageHandler(ITwitchMessageHandler messageHandler) {
		this.messageHandler = messageHandler;
		return this;
	}
	
	public static void send(OutputStream ircOutputStream, String message) throws IOException {
		ircOutputStream.write((message + "\r\n").getBytes(StandardCharsets.UTF_8));
		// ircOutputStream.flush();
	}
	
	private static void sendPong(OutputStream ircOutputStream) throws IOException {
		TimerTwitchBot.send(ircOutputStream, "PONG :tmi.twitch.tv");
	}
	
	public static void sendChat(OutputStream ircOutputStream, String channel, String message) throws IOException {
		TimerTwitchBot.send(ircOutputStream, "PRIVMSG " + channel + " :" + message);
	}
	
	public static void sendWhisper(OutputStream ircOutputStream, String channel, String username, String message) throws IOException {
		TimerTwitchBot.sendWhisper(ircOutputStream, channel, username, message, false);
	}
	
	public static void sendWhisper(OutputStream ircOutputStream, String channel, String username, String message, boolean isKnownBot) throws IOException {
		if (isKnownBot)
			TimerTwitchBot.send(ircOutputStream, "PRIVMSG " + channel + " :/w " + username + " " + message);
		else
			TimerTwitchBot.send(ircOutputStream, "PRIVMSG " + channel + " :I'm not a Known Bot so i cannot send whispers");
	}
	
	public static boolean wasSentByModerator(String message) {
		ParsedMessage pm = ParsedMessage.parseMessage(message);
		String badges = pm.getTagsAsMap().get("badges");
		return badges != null && badges.contains("moderator");
	}
	
	public static boolean wasSentByBroadcaster(String message) {
		ParsedMessage pm = ParsedMessage.parseMessage(message);
		String badges = pm.getTagsAsMap().get("badges");
		return badges != null && badges.contains("broadcaster");
	}
	
	public static boolean wasSentByOp(String message) {
		ParsedMessage pm = ParsedMessage.parseMessage(message);
		String badges = pm.getTagsAsMap().get("badges");
		return badges != null && (badges.contains("broadcaster") || badges.contains("moderator"));
	}
}
