package server;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class PrefsReader {
	public static String readPref(String prefsFile, String prefName, String defaultValue) {
		try {
			Properties prop = new Properties();
			InputStream is = new FileInputStream(prefsFile);
			prop.load(is);
			return prop.getProperty(prefName, defaultValue);
		} catch (Exception e) {
			throw new IllegalArgumentException("Error reading " + prefsFile +
					" for pref " + prefName + " with default value " + defaultValue);
		}
	}
}
