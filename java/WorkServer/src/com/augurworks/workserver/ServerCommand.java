package com.augurworks.workserver;

public enum ServerCommand {
	REQUEST_SHUTDOWN("shutdown"),
	STATUS("status");
	;
	
	private String commandString;
	
	private ServerCommand(String commandString) {
		this.commandString = commandString;
	}
	
	public static ServerCommand fromCommand(String s) {
		for (ServerCommand serverCommand : values()) {
			if (serverCommand.commandString.equalsIgnoreCase(s)) {
				return serverCommand;
			}
		}
		throw new IllegalArgumentException("Command \"" + s + "\" not recognized.");
	}
}
