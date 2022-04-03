package tv.twitch.bot;

import java.io.OutputStream;

public class TwitchMessageHandlerAdapter implements ITwitchMessageHandler {
	@Override
	public void handle(OutputStream os, String message) {}
	@Override
	public void handleClearChat(OutputStream os, String message) {}
	@Override
	public void handleClearMsg(OutputStream os, String message) {}
	@Override
	public void handleGlobalUserState(OutputStream os, String message) {}
	@Override
	public void handleHostTarget(OutputStream os, String message) {}
	@Override
	public void handleNotice(OutputStream os, String message) {}
	@Override
	public void handlePrivMsg(OutputStream os, String message) {}
	@Override
	public void handleReconnect(OutputStream os, String message) {}
	@Override
	public void handleRoomState(OutputStream os, String message) {}
	@Override
	public void handleUserNotice(OutputStream os, String message) {}
	@Override
	public void handleUserState(OutputStream os, String message) {}
}
