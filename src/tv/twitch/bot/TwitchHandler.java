package tv.twitch.bot;

import java.io.IOException;
import java.io.OutputStream;

public interface TwitchHandler {
	public void handle(String oauth, OutputStream os, String message) throws IOException;
}
