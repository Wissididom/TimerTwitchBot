package tv.twitch.bot;

import java.util.HashMap;
import java.util.Map;

public class ParsedMessage {
	private String tags;
	private String username;
	private String host;
	private String command;
	private String channel;
	private String message;
	
	private ParsedMessage(String tags, String username, String host, String command, String channel, String message) {
		this.tags = tags;
		this.username = username;
		this.host = host;
		this.command = command;
		this.channel = channel;
		this.message = message;
	}
	
	public static ParsedMessage parseMessage(String message) {
		if (message.startsWith("@")) {
			String[] components = message.split(" ");
			String tags = components[0];
			if (!components[1].contains("!"))
				return new ParsedMessage("", "", "", "", "", "");
			String username = components[1].split("!")[1];
			String host = username.substring(username.indexOf('@') + 1);
			username = username.substring(0, username.indexOf('@'));
			if (components.length < 3)
				return new ParsedMessage("", username, host, "", "", "");
			String command = components[2];
			if (components.length < 4)
				return new ParsedMessage("", username, host, command, "", "");
			String channel = components[3];
			if (components.length < 5)
				return new ParsedMessage("", username, host, command, channel, "");
			String onlyMessage = components[4].substring(1);
			for (int i = 5; i < components.length; i++)
				onlyMessage += " " + components[i];
			return new ParsedMessage(tags, username, host, command, channel, onlyMessage);
		} else {
			String[] components = message.split(" ");
			if (!components[0].contains("!"))
				return new ParsedMessage("", "", "", "", "", "");
			String username = components[0].split("!")[1];
			String host = username.substring(username.indexOf('@') + 1);
			username = username.substring(0, username.indexOf('@'));
			if (components.length < 2)
				return new ParsedMessage("", username, host, "", "", "");
			String command = components[1];
			if (components.length < 3)
				return new ParsedMessage("", username, host, command, "", "");
			String channel = components[2];
			if (components.length < 4)
				return new ParsedMessage("", username, host, command, channel, "");
			String onlyMessage = components[3].substring(1);
			for (int i = 4; i < components.length; i++)
				onlyMessage += " " + components[i];
			return new ParsedMessage("", username, host, command, channel, onlyMessage);
		}
	}
	
	public String getTags() {
		return this.tags;
	}
	
	public Map<String, String> getTagsAsMap() {
		String tags = this.tags.startsWith("@") ? this.tags.substring(1) : this.tags;
		Map<String, String> result = new HashMap<String, String>();
		for (String tag : tags.split(";")) {
			String key = tag.substring(0, tag.indexOf('='));
			String value = tag.substring(tag.indexOf('=') + 1);
			result.put(key, value);
		}
		return result;
	}
	
	public String getUsername() {
		return this.username;
	}
	
	public String getHost() {
		return this.host;
	}
	
	public String getCommand() {
		return this.command;
	}
	
	public String getChannel() {
		return this.channel;
	}
	
	public String getMessage() {
		return this.message;
	}
}
