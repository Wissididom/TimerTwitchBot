package tv.twitch.bot;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Commands {
	private static String admin = "wissididom";
	private static Timer timer = null;
	private static boolean firstRun = true;
	private static boolean showTime = false;
	
	private static String[] cloneAndRemoveFirst(String[] arr) {
		String[] result = new String[arr.length - 1];
		for (int i = 1; i < arr.length; i++)
			result[i - 1] = arr[i];
		return result;
	}
	
	private static String getUsage(String command) {
		command = command.replace("!", "").toLowerCase();
		switch (command) {
			case "usage":
				return "Usage: !" + command + " <command>";
			case "timer":
			case "starttimer":
				return "Usage: e. g. !" + command + " 0d0h5m0s (each component optional but at least one of them)";
			case "showtimer":
				return "Usage: !" + command;
			case "stoptimer":
				return "Usage: !" + command;
			case "quitbot":
				return "Usage: !" + command;
			default:
				return "I don't know that command so i cannot show it's usage!";
		}
	}
	
	public static void usage(OutputStream ircOutputStream, String rawMessage) throws IOException {
		ParsedMessage pm = ParsedMessage.parseMessage(rawMessage);
		String message = pm.getMessage();
		String usage = null;
		if (message.contains(" ")) {
			usage = message.substring(0, message.indexOf(' ')) + " <command>";
		} else {
			usage = message.trim() + " <command>";
			TimerTwitchBot.sendChat(ircOutputStream, pm.getChannel(), "@" + pm.getUsername() + " Usage: " + usage);
			return;
		}
		String[] parts = message.substring(message.indexOf(' ') + 1).split(" ");
		if (parts.length > 1)
			TimerTwitchBot.sendChat(ircOutputStream, pm.getChannel(), String.join(" ", Commands.cloneAndRemoveFirst(parts)) + " " + Commands.getUsage(parts[0]));
		else if (parts.length == 1)
			TimerTwitchBot.sendChat(ircOutputStream, pm.getChannel(), "@" + pm.getUsername() + " " + Commands.getUsage(parts[0]));
		else
			TimerTwitchBot.sendChat(ircOutputStream, pm.getChannel(), "@" + pm.getUsername() + " Usage: " + usage);
	}
	
	public static void starttimer(OutputStream ircOutputStream, String rawMessage) throws IOException {
		ParsedMessage pm = ParsedMessage.parseMessage(rawMessage);
		if (!pm.getUsername().equalsIgnoreCase(Commands.admin) && !TimerTwitchBot.wasSentByOp(rawMessage)) {
			TimerTwitchBot.sendChat(ircOutputStream, pm.getChannel(), "@" + pm.getUsername() + " I'll only obey Operators (Streamer and Mods)");
			return;
		}
		final String message = pm.getMessage();
		String[] parts = message.substring(message.indexOf(' ') + 1).split(" ");
		if (parts.length > 0) {
			int tempSeconds = 0;
			Pattern pattern = Pattern.compile("\\d+(?:d|h|m|s)");
			Matcher matcher = pattern.matcher(parts[0]);
			while (matcher.find()) {
				String timeComponent = matcher.group();
				int time = Integer.parseInt(timeComponent.substring(0, timeComponent.length() - 1));
				char ending = timeComponent.charAt(timeComponent.length() - 1);
				switch (ending) {
					case 'd':
						tempSeconds += time * 86400; // days
						break;
					case 'h':
						tempSeconds += time * 3600; // hours
						break;
					case 'm':
						tempSeconds += time * 60; // minutes
						break;
					case 's':
						tempSeconds += time; // seconds
						break;
				}
			}
			final int seconds = tempSeconds;
			Commands.timer = new Timer();
			timer.scheduleAtFixedRate(new TimerTask() {
				private int remainingSeconds = seconds;
				
				@Override
				public void run() {
					try {
						if (Commands.showTime) {
							int d = (int) Math.floor(seconds / (3600 * 24));
							int h = (int) Math.floor(seconds % (3600 * 24) / 3600);
							int m = (int) Math.floor(seconds % 3600 / 60);
							int s = (int) Math.floor(seconds % 60);
							String responseString = "";
							if (d > 0)
								responseString += d + " days ";
							if (h > 0)
								responseString += h + " hours ";
							if (m > 0)
								responseString += m + " minutes ";
							if (s > 0)
								responseString += s + " seconds ";
							TimerTwitchBot.sendChat(ircOutputStream, pm.getChannel(), "@" + pm.getUsername() + " " + responseString.trim());
							Commands.showTime = false;
						}
						if (Commands.firstRun) {
							Commands.firstRun = false;
							this.remainingSeconds--;
							return;
						}
						if (this.remainingSeconds > 60 * 60) {
							if (this.remainingSeconds % 60 == 0)
								TimerTwitchBot.sendChat(ircOutputStream, pm.getChannel(), "@" + pm.getUsername() + " Remaining Time: " + (this.remainingSeconds / 60) + " hours");
							return;
						}
						switch (this.remainingSeconds) {
							case 60 * 60:
								TimerTwitchBot.sendChat(ircOutputStream, pm.getChannel(), "@" + pm.getUsername() + " Remaining Time: 1 hour");
								break;
							case 30 * 60:
								TimerTwitchBot.sendChat(ircOutputStream, pm.getChannel(), "@" + pm.getUsername() + " Remaining Time: 30 minutes");
								break;
							case 10 * 60:
								TimerTwitchBot.sendChat(ircOutputStream, pm.getChannel(), "@" + pm.getUsername() + " Remaining Time: 10 minutes");
								break;
							case 5 * 60:
								TimerTwitchBot.sendChat(ircOutputStream, pm.getChannel(), "@" + pm.getUsername() + " Remaining Time: 5 minutes");
								break;
							case 3 * 60:
								TimerTwitchBot.sendChat(ircOutputStream, pm.getChannel(), "@" + pm.getUsername() + " Remaining Time: 3 minutes");
								break;
							case 60:
								TimerTwitchBot.sendChat(ircOutputStream, pm.getChannel(), "@" + pm.getUsername() + " Remaining Time: 1 minute");
								break;
							case 30:
								TimerTwitchBot.sendChat(ircOutputStream, pm.getChannel(), "@" + pm.getUsername() + " Remaining Time: 30 seconds");
								break;
							case 15:
								TimerTwitchBot.sendChat(ircOutputStream, pm.getChannel(), "@" + pm.getUsername() + " Remaining Time: 15 seconds");
								break;
							case 10:
								TimerTwitchBot.sendChat(ircOutputStream, pm.getChannel(), "@" + pm.getUsername() + " Remaining Time: 10 seconds");
								break;
							/*case 5:
								TimerTwitchBot.sendChat(ircOutputStream, pm.getChannel(), "@" + pm.getUsername() + " Remaining Time: 5 seconds");
								break;
							case 3:
								TimerTwitchBot.sendChat(ircOutputStream, pm.getChannel(), "@" + pm.getUsername() + " Remaining Time: 3 seconds");
								break;
							case 2:
								TimerTwitchBot.sendChat(ircOutputStream, pm.getChannel(), "@" + pm.getUsername() + " Remaining Time: 2 seconds");
								break;
							case 1:
								TimerTwitchBot.sendChat(ircOutputStream, pm.getChannel(), "@" + pm.getUsername() + " Remaining Time: 1 second");
								break;*/
							case 0:
								TimerTwitchBot.sendChat(ircOutputStream, pm.getChannel(), "@" + pm.getUsername() + " Time's Up");
								Commands.timer.cancel();
								Commands.timer = null;
								break;
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
					this.remainingSeconds--;
				}
			}, 0, 1000);
			int d = (int) Math.floor(seconds / (3600 * 24));
			int h = (int) Math.floor(seconds % (3600 * 24) / 3600);
			int m = (int) Math.floor(seconds % 3600 / 60);
			int s = (int) Math.floor(seconds % 60);
			String responseString = "";
			if (d > 0)
				responseString += d + " days ";
			if (h > 0)
				responseString += h + " hours ";
			if (m > 0)
				responseString += m + " minutes ";
			if (s > 0)
				responseString += s + " seconds ";
			TimerTwitchBot.sendChat(ircOutputStream, pm.getChannel(), "@" + pm.getUsername() + " Timer for " + responseString + "started");
		} else {
			TimerTwitchBot.sendChat(ircOutputStream, pm.getChannel(), "@" + pm.getUsername() + " " + Commands.getUsage("!timer"));
		}
		System.out.println("Executed: " + message);
	}
	
	public static void showtimer(OutputStream ircOutputStream, String rawMessage) throws IOException {
		ParsedMessage pm = ParsedMessage.parseMessage(rawMessage);
		if (!pm.getUsername().equalsIgnoreCase(Commands.admin) && !TimerTwitchBot.wasSentByOp(rawMessage)) {
			TimerTwitchBot.sendChat(ircOutputStream, pm.getChannel(), "@" + pm.getUsername() + " I'll only obey Operators (Streamer and Mods)");
			return;
		}
		final String message = pm.getMessage();
		String[] parts = message.substring(message.indexOf(' ') + 1).split(" ");
		if (parts.length < 1) {
			Commands.showTime = true;
		} else {
			TimerTwitchBot.sendChat(ircOutputStream, pm.getChannel(), "@" + pm.getUsername() + " " + Commands.getUsage("!showtimer"));
		}
		System.out.println("Executed: " + message);
	}
	
	public static void stoptimer(OutputStream ircOutputStream, String rawMessage) throws IOException {
		ParsedMessage pm = ParsedMessage.parseMessage(rawMessage);
		if (!pm.getUsername().equalsIgnoreCase(Commands.admin) && !TimerTwitchBot.wasSentByOp(rawMessage)) {
			TimerTwitchBot.sendChat(ircOutputStream, pm.getChannel(), "@" + pm.getUsername() + " I'll only obey Operators (Streamer and Mods)");
			return;
		}
		final String message = pm.getMessage();
		Commands.timer.cancel();
		TimerTwitchBot.sendChat(ircOutputStream, pm.getChannel(), "@" + pm.getUsername() + " Timer stopped!");
		System.out.println("Executed: " + message);
	}
	
	public static void quitbot(OutputStream ircOutputStream, String rawMessage) throws IOException {
		ParsedMessage pm = ParsedMessage.parseMessage(rawMessage);
		final String message = pm.getMessage();
		if (pm.getUsername().equalsIgnoreCase(Commands.admin) || TimerTwitchBot.wasSentByBroadcaster(rawMessage)) {
			TimerTwitchBot.sendChat(ircOutputStream, pm.getChannel(), "@" + pm.getUsername() + " Ok, I'm leaving chat and exiting...");
			TimerTwitchBot.send(ircOutputStream, "QUIT");
			System.exit(0);
		} else {
			TimerTwitchBot.sendChat(ircOutputStream, pm.getChannel(), "@" + pm.getUsername() + " Nice Try! I'll only obey @" + pm.getChannel().replace("#", "") + "!");
		}
		System.out.println("Executed: " + message);
	}
}
