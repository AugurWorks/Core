package server;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import twitter4j.FilterQuery;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.auth.OAuthAuthorization;
import twitter4j.conf.ConfigurationBuilder;

// Requires:
// Twitter4j library
// The Apache HttpClient library simplifies handling HTTP requests. 
// To use this library download the binaries with dependencies from 
// http://hc.apache.org/ and add then to your project class path.
public class TwitterReader implements Runnable {
	private static final String ACCESS_TOKEN = "1135747976-OyBreDVCujPbbfPLLUnV19kJ21qVTwvyxbKMnyn";
	private static final String ACCESS_TOKEN_SECRET = "yWa2a6HOCdWRd5K7Svb6KXb9G7MCd65xIUvDT8rCYo";
	private static final String CONSUMER_KEY = "iLQm2VraHR2MLe2mwS2mvg";
	private static final String CONSUMER_SECRET = "BNx6ij82BtzMVoEEvk6Y4xBDRjdAV8arWysSU7YmGA";
	private final FileWriter fw;
	private final BufferedWriter bw;
	private final File f;
	private final DateFormat dateFormat = new SimpleDateFormat(
			"yyyy/MM/dd HH:mm:ss");
	private final Calendar cal = Calendar.getInstance();
	private final TwitterStream twitterStream;

	private final int MAX_STATUSES;
	private String filename;
	private int statusesCounted = 0;

	public TwitterReader(String filename, int max) throws IOException {
		this.filename = filename;
		this.f = new File(this.filename);
		if (!f.exists()) {
			f.createNewFile();
		}
		this.fw = new FileWriter(f.getAbsoluteFile());
		this.bw = new BufferedWriter(fw);
		this.MAX_STATUSES = max;
		ConfigurationBuilder builder = new ConfigurationBuilder();
		builder.setOAuthAccessToken(ACCESS_TOKEN);
		builder.setOAuthAccessTokenSecret(ACCESS_TOKEN_SECRET);
		builder.setOAuthConsumerKey(CONSUMER_KEY);
		builder.setOAuthConsumerSecret(CONSUMER_SECRET);

		OAuthAuthorization auth = new OAuthAuthorization(builder.build());
		twitterStream = new TwitterStreamFactory().getInstance(auth);
	}
	
	@Override
	public void run() {
		printHeader();
		System.out.println("Starting connect...");
		System.out.println("File output to: " + this.filename);

		StatusListener listener = new StatusListener() {
			@Override
			public synchronized void onStatus(Status status) {
				printItem(status);
			}

			@Override
			public void onDeletionNotice(
					StatusDeletionNotice statusDeletionNotice) {
			}

			@Override
			public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
			}

			@Override
			public void onException(Exception ex) {
				ex.printStackTrace();
			}

			@Override
			public void onScrubGeo(long arg0, long arg1) {
			}

			@Override
			public void onStallWarning(StallWarning arg0) {
			}
		};

		twitterStream.addListener(listener);

		FilterQuery query = new FilterQuery();
		String track[] = { "opec", "big data", "bieber" };
		query.track(track);
		twitterStream.filter(query);
	}

	private synchronized void quit() {
		printFooter();
		try {
			bw.flush();
			bw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		twitterStream.cleanUp();
		String destination = System.getProperty("user.dir")
				+ "/src/server/twitter.rss";
		File destinationFile = new File(destination);
		this.f.renameTo(destinationFile);
	}

	private synchronized void printHeader() {
		try {
			String buildDate = dateFormat.format(cal.getTime());
			String s = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?> \n"
					+ "<rss version=\"2.0\"> \n" + "<channel>\n"
					+ "<title>Twitter?</title> \n"
					+ "<description>AugurWorks First Guess</description>\n"
					+ "<link></link> \n" + "<lastBuildDate>" + buildDate
					+ "</lastBuildDate> \n" + "<pubDate>" + buildDate
					+ "</pubDate> \n";
			bw.write(s);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private synchronized void printItem(Status status) {
		try {
			if (status.getUser().getLang().equals("en")) {
				String s = "<item> \n" + "<author>"
						+ status.getUser().getScreenName() + "</author>\n"
						+ "<text>" + status.getText() + "</text>\n"
						+ "<pubDate>"
						+ dateFormat.format(status.getCreatedAt())
						+ "</pubDate>" + "</item>\n";
				bw.write(s);
				statusesCounted++;
				if (statusesCounted >= MAX_STATUSES) {
					quit();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private synchronized void printFooter() {
		try {
			String s = "</channel>\n</rss>";
			bw.write(s);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}