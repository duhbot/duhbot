package org.duh102.duhbot.data;

import java.util.*;

public class IRCServer {
	public boolean ssl;
	public String serverAddr;
	public int port;
	public String password;
	public ArrayList<IRCChannel> channels;

	public IRCServer(String serverAddr, int port, String password, boolean ssl) {
		this.serverAddr = serverAddr;
		this.ssl = ssl;
		this.port = port;
		this.password = password;
	}

	public IRCServer(String serverAddr, int port, String password, boolean ssl, Collection<IRCChannel> channels) {
		this(serverAddr, port, password, ssl);
		this.channels = new ArrayList<IRCChannel>(channels);
	}
}