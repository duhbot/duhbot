package org.duh102.duhbot.functions;

import java.util.*;

import org.pircbotx.hooks.*;
import org.pircbotx.hooks.events.*;

import org.duh102.duhbot.db.*;

public class LogBotListener extends ListenerAdapter implements DuhbotFunction, ListeningPlugin {
	public LogBotListener() {
		LogDB.createTables();
	}

	public DuhbotFunction factory() {
		return (DuhbotFunction) (new LogBotListener());
	}

	public Map<String, String> getHelpFunctions() {
		HashMap<String, String> helpFunctions = new HashMap<String, String>();
		helpFunctions.put(HelpFunction.properHelpFunction("Default"), "This function has no user-accessible functions");
		return helpFunctions;
	}

	@Override
	public ListenerAdapter getAdapter() {
		return this;
	}

	public String getPluginName() {
		return "logger";
	}

	// Action/Message
	public void onAction(ActionEvent event) {
		LogDB.logAction(event);
	}

	public void onMessage(MessageEvent event) {
		LogDB.logMessage(event);
	}

	// Join/Part
	public void onJoin(JoinEvent event) {
		LogDB.logJoin(event);
	}

	public void onPart(PartEvent event) {
		LogDB.logPart(event);
	}

	// Nick Change
	public void onNickChange(NickChangeEvent event) {
		LogDB.logNickChange(event);
	}

	// Notice
	public void onNotice(NoticeEvent event) {
		LogDB.logNotice(event);
	}

	// Kick
	public void onKick(KickEvent event) {
		LogDB.logKick(event);
	}

	// Quit
	public void onQuit(QuitEvent event) {
		LogDB.logQuit(event);
	}

	// MOTD
	public void onMotd(MotdEvent event) {
		LogDB.logMOTD(event);
	}

	// Topic
	public void onTopic(TopicEvent event) {
		LogDB.logTopic(event);
	}

	// Userlist
	public void onUserList(UserListEvent event) {
		LogDB.logUserList(event);
	}

	// Mode setting
	public void onMode(ModeEvent event) {
		LogDB.logMode(event);
	}

	public void onUserMode(UserModeEvent event) {
		LogDB.logUserMode(event);
	}
}
