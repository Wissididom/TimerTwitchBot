package tv.twitch.bot;

import java.io.IOException;
import java.io.OutputStream;

public interface ITwitchMessageHandler {
	public void handle(OutputStream os, String message) throws IOException;
	public void handleClearChat(OutputStream os, String message) throws IOException;
	public void handleClearMsg(OutputStream os, String message) throws IOException;
	public void handleGlobalUserState(OutputStream os, String message) throws IOException;
	public void handleHostTarget(OutputStream os, String message) throws IOException;
	public void handleNotice(OutputStream os, String message) throws IOException;
	public void handlePrivMsg(OutputStream os, String message) throws IOException;
	public void handleReconnect(OutputStream os, String message) throws IOException;
	public void handleRoomState(OutputStream os, String message) throws IOException;
	public void handleUserNotice(OutputStream os, String message) throws IOException;
	public void handleUserState(OutputStream os, String message) throws IOException;
}
