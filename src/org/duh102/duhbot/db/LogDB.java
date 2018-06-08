package org.duh102.duhbot.db;

import java.sql.*;
import java.util.*;

import org.pircbotx.*;
import org.pircbotx.hooks.events.*;

public class LogDB {
	public static final int EVENT_ACTION = 0, EVENT_JOIN = 10, EVENT_KICK = 20, EVENT_MESSAGE = 30, EVENT_MODE = 40,
			EVENT_MOTD = 50, EVENT_NICKCHANGE = 60, EVENT_NOTICE = 70, EVENT_PART = 80, EVENT_QUIT = 90,
			EVENT_TOPIC = 100, EVENT_USERLIST = 110, EVENT_USERMODE = 120;
	public static final String NULL_CHANNEL = "<null>", NULL_USER = "<null>", NULL_DATA = "";

	public static final String DB_FILE = "irc_log.db";

	public static void createTables() {
		Connection conn = getDBConnection();
		if (conn != null) {
			try {
				Statement stat = conn.createStatement();
				stat.executeUpdate(
						"create table if not exists logEntry (server varchar(128), channel varchar(128) default 'null', logID INTEGER PRIMARY KEY AUTOINCREMENT, timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, type INTEGER, data0 varchar(1024), data1 varchar(1024));");
				conn.close();
				System.err.printf("Inserted something\n");
			} catch (java.sql.SQLException sqle) {
				sqle.printStackTrace();
			}
		}
	}

	public static Connection getDBConnection() {
		Connection conn = null;
		try {
			try {
				Class.forName("org.sqlite.JDBC");
			} catch (java.lang.ClassNotFoundException cnfe) {
				cnfe.printStackTrace();
			}
			conn = DriverManager.getConnection("jdbc:sqlite:" + DB_FILE);
		} catch (java.sql.SQLException sqle) {
			sqle.printStackTrace();
		}
		return conn;
	}

	// Generic
	public static void addLogEntry(String server, String channel, int typeID, String data1, String data2) {
		Connection conn = getDBConnection();
		if (conn != null) {
			try {
				PreparedStatement ps = conn.prepareStatement(
						"insert into logEntry (server, channel, type, data0, data1) values (?, ?, ?, ?, ?);");
				ps.setString(1, server);
				ps.setString(2, channel);
				ps.setInt(3, typeID);
				ps.setString(4, data1);
				ps.setString(5, data2);
				@SuppressWarnings("unused")
				int numInserted = ps.executeUpdate();
				conn.close();
			} catch (java.sql.SQLException sqle) {
				sqle.printStackTrace();
			}
		}
	}

	public static String formatUser(User user) {
		return user.getNick() + "," + user.getLogin() + "@" + user.getHostmask();
	}

	public static String userListToString(Set<User> userSet) {
		ArrayList<User> users = new ArrayList<User>(userSet);
		StringBuilder list = new StringBuilder("[" + formatUser(users.get(0)) + "]");
		for (int i = 1; i < users.size(); i++) {
			list.append(", [" + formatUser(users.get(i)) + "]");
		}
		return list.toString();
	}

	public static void logAction(ActionEvent event) {
		addLogEntry(event.getBot().getServerHostname().toLowerCase(), event.getChannel().getName().toLowerCase(),
				EVENT_ACTION, formatUser(event.getUser()), event.getAction());
	}

	public static void logJoin(JoinEvent event) {
		addLogEntry(event.getBot().getServerHostname().toLowerCase(), event.getChannel().getName().toLowerCase(),
				EVENT_JOIN, formatUser(event.getUser()), NULL_DATA);
	}

	public static void logKick(KickEvent event) {
		addLogEntry(event.getBot().getServerHostname().toLowerCase(), event.getChannel().getName().toLowerCase(),
				EVENT_KICK, formatUser(event.getUser()) + "~" + formatUser(event.getRecipient()), event.getReason());
	}

	public static void logMessage(MessageEvent event) {
		addLogEntry(event.getBot().getServerHostname().toLowerCase(), event.getChannel().getName().toLowerCase(),
				EVENT_MESSAGE, formatUser(event.getUser()), event.getMessage());
	}

	public static void logMode(ModeEvent event) {
		addLogEntry(event.getBot().getServerHostname().toLowerCase(), event.getChannel().getName().toLowerCase(),
				EVENT_MODE, formatUser(event.getUser()), event.getMode());
	}

	public static void logMOTD(MotdEvent event) {
		addLogEntry(event.getBot().getServerHostname().toLowerCase(), NULL_CHANNEL, EVENT_MOTD, NULL_USER,
				event.getMotd());
	}

	public static void logNickChange(NickChangeEvent event) {
		addLogEntry(event.getBot().getServerHostname().toLowerCase(), NULL_CHANNEL, EVENT_NICKCHANGE,
				formatUser(event.getUser()), event.getOldNick() + "," + event.getNewNick());
	}

	public static void logNotice(NoticeEvent event) {
		addLogEntry(event.getBot().getServerHostname().toLowerCase(), event.getChannel().getName().toLowerCase(),
				EVENT_NOTICE, formatUser(event.getUser()), event.getNotice());
	}

	public static void logPart(PartEvent event) {
		addLogEntry(event.getBot().getServerHostname().toLowerCase(), event.getChannel().getName().toLowerCase(),
				EVENT_PART, formatUser(event.getUser()), event.getReason());
	}

	public static void logQuit(QuitEvent event) {
		addLogEntry(event.getBot().getServerHostname().toLowerCase(), NULL_CHANNEL, EVENT_QUIT,
				formatUser(event.getUser()), event.getReason());
	}

	public static void logTopic(TopicEvent event) {
		addLogEntry(event.getBot().getServerHostname().toLowerCase(), event.getChannel().getName().toLowerCase(),
				EVENT_TOPIC, event.getUser().toString(),
				event.getTopic().replaceAll("\\|", "\\|\\|") + "|" + event.getDate() + "|" + event.isChanged());
	}

	public static void logUserList(UserListEvent event) {
		addLogEntry(event.getBot().getServerHostname().toLowerCase(), event.getChannel().getName().toLowerCase(),
				EVENT_USERLIST, userListToString(event.getUsers()), NULL_DATA);
	}

	public static void logUserMode(UserModeEvent event) {
		addLogEntry(event.getBot().getServerHostname().toLowerCase(), NULL_CHANNEL, EVENT_USERMODE,
				formatUser(event.getUser()) + "~" + formatUser(event.getRecipient()), event.getMode());
	}
}