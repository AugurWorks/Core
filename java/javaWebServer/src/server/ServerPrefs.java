package server;

public class ServerPrefs {
	private static final String fileLocation = "server.prefs";
	
	public static String getBindAddress() {
		return PrefsReader.readPref(fileLocation, "BIND_ADDRESS", "localhost");
	}
	
	public static int getPort() {
		return Integer.parseInt(PrefsReader.readPref(fileLocation, "PORT", "8000"));
	}
}
