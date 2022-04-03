package tv.twitch.bot;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class DefaultTwitchMessageHandler implements ITwitchMessageHandler {
	private String oauth;
	private Map<String, TwitchHandler> chatCommands = null;
	private Map<String, TwitchHandler> ircCommands = null;
	
	public DefaultTwitchMessageHandler(String oauth, Map<String, TwitchHandler> chatCommands, Map<String, TwitchHandler> ircCommands) {
		this.oauth = oauth;
		if (chatCommands == null)
			this.chatCommands = new HashMap<String, TwitchHandler>();
		else
			this.chatCommands = chatCommands;
		if (ircCommands == null)
			this.ircCommands = new HashMap<String, TwitchHandler>();
		else
			this.ircCommands = ircCommands;
	}
	
	public DefaultTwitchMessageHandler(String oauth, Map<String, TwitchHandler> chatCommands) {
		this(oauth, chatCommands, null);
	}
	
	public DefaultTwitchMessageHandler(String oauth) {
		this(oauth, null, null);
	}
	
	@Override
	public void handle(OutputStream os, String message) throws IOException {
		ParsedMessage pm = ParsedMessage.parseMessage(message);
		String command = pm.getCommand();
		if (command.equalsIgnoreCase("CLEARCHAT"))
			this.handleClearChat(os, message);
		if (command.equalsIgnoreCase("CLEARMSG"))
			this.handleClearMsg(os, message);
		if (command.equalsIgnoreCase("GLOBALUSERSTATE"))
			this.handleGlobalUserState(os, message);
		if (command.equalsIgnoreCase("HOSTTARGET"))
			this.handleHostTarget(os, message);
		if (command.equalsIgnoreCase("NOTICE"))
			this.handleNotice(os, message);
		if (command.equalsIgnoreCase("PRIVMSG"))
			this.handlePrivMsg(os, message);
		if (command.equalsIgnoreCase("RECONNECT"))
			this.handleReconnect(os, message);
		if (command.equalsIgnoreCase("ROOMSTATE"))
			this.handleRoomState(os, message);
		if (command.equalsIgnoreCase("USERNOTICE"))
			this.handleUserNotice(os, message);
		if (command.equalsIgnoreCase("USERSTATE"))
			this.handleUserState(os, message);
	}
	
	@Override
	public void handleClearChat(OutputStream os, String message) throws IOException {
		if (this.ircCommands.containsKey("CLEARCHAT"))
			this.ircCommands.get("CLEARCHAT").handle(this.getOauth(), os, message);
		else
			System.out.println("CLEARCHAT-message:" + message);
	}
	
	@Override
	public void handleClearMsg(OutputStream os, String message) throws IOException {
		if (this.ircCommands.containsKey("CLEARMSG"))
			this.ircCommands.get("CLEARMSG").handle(this.getOauth(), os, message);
		else
			System.out.println("CLEARMSG-message:" + message);
	}
	
	@Override
	public void handleGlobalUserState(OutputStream os, String message) throws IOException {
		if (this.ircCommands.containsKey("GLOBALUSERSTATE"))
			this.ircCommands.get("GLOBALUSERSTATE").handle(this.getOauth(), os, message);
		else
			System.out.println("GLOBALUSERSTATE-message:" + message);
	}
	
	@Override
	public void handleHostTarget(OutputStream os, String message) throws IOException {
		if (this.ircCommands.containsKey("HOSTTARGET"))
			this.ircCommands.get("HOSTTARGET").handle(this.getOauth(), os, message);
		else
			System.out.println("HOSTTARGET-message:" + message);
	}
	
	@Override
	public void handleNotice(OutputStream os, String message) throws IOException {
		if (this.ircCommands.containsKey("NOTICE"))
			this.ircCommands.get("NOTICE").handle(this.getOauth(), os, message);
		else
			System.out.println("NOTICE-message:" + message);
	}
	
	@Override
	public void handlePrivMsg(OutputStream os, String rawMessage) throws IOException {
		if (this.ircCommands.containsKey("PRIVMSG")) {
			this.ircCommands.get("PRIVMSG").handle(this.getOauth(), os, rawMessage);
		} else {
			String message = rawMessage.split(" ")[4].substring(1);
			if (message.contains(" "))
				message = message.substring(0, message.indexOf(' '));
			if (this.chatCommands.containsKey(message))
				this.chatCommands.get(message).handle(this.getOauth(), os, rawMessage);
			else
				System.out.println("PRIVMSG-message:" + rawMessage);
		}
	}
	
	@Override
	public void handleReconnect(OutputStream os, String message) throws IOException {
		if (this.ircCommands.containsKey("RECONNECT"))
			this.ircCommands.get("RECONNECT").handle(this.getOauth(), os, message);
		else
			System.out.println("RECONNECT-message:" + message);
	}
	
	@Override
	public void handleRoomState(OutputStream os, String message) throws IOException {
		if (this.ircCommands.containsKey("ROOMSTATE"))
			this.ircCommands.get("ROOMSTATE").handle(this.getOauth(), os, message);
		else
			System.out.println("ROOMSTATE-message:" + message);
	}
	
	@Override
	public void handleUserNotice(OutputStream os, String message) throws IOException {
		if (this.ircCommands.containsKey("USERNOTICE"))
			this.ircCommands.get("USERNOTICE").handle(this.getOauth(), os, message);
		else
			System.out.println("USERNOTICE-message:" + message);
	}
	
	@Override
	public void handleUserState(OutputStream os, String message) throws IOException {
		if (this.ircCommands.containsKey("USERSTATE"))
			this.ircCommands.get("USERSTATE").handle(this.getOauth(), os, message);
		else
			System.out.println("USERSTATE-message:" + message);
	}
	
	public Map<String, TwitchHandler> getChatCommands() {
		return this.chatCommands;
	}
	
	public boolean hasChatCommand(String chatCommand) {
		return this.chatCommands.containsKey(chatCommand);
	}
	
	public TwitchHandler getChatCommand(String chatCommand) {
		return this.chatCommands.get(chatCommand);
	}
	
	public TwitchHandler putChatCommand(String chatCommand, TwitchHandler code) {
		return this.chatCommands.put(chatCommand, code);
	}
	
	public Map<String, TwitchHandler> getIrcCommands() {
		return this.ircCommands;
	}
	
	public boolean hasIrcCommand(String ircCommand) {
		return this.ircCommands.containsKey(ircCommand);
	}
	
	public TwitchHandler getIrcCommand(String ircCommand) {
		return this.ircCommands.get(ircCommand);
	}
	
	public TwitchHandler putIrcCommand(String ircCommand, TwitchHandler code) {
		return this.ircCommands.put(ircCommand, code);
	}
	
	public String getOauth() {
		return this.oauth;
	}
}
