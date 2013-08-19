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
public class TwitterReader {
	private static final String ACCESS_TOKEN = "1135747976-OyBreDVCujPbbfPLLUnV19kJ21qVTwvyxbKMnyn";
	private static final String ACCESS_TOKEN_SECRET = "yWa2a6HOCdWRd5K7Svb6KXb9G7MCd65xIUvDT8rCYo";
	private static final String CONSUMER_KEY = "iLQm2VraHR2MLe2mwS2mvg";
	private static final String CONSUMER_SECRET = "BNx6ij82BtzMVoEEvk6Y4xBDRjdAV8arWysSU7YmGA";
	private final FileWriter fw;
	private final BufferedWriter bw;
	private final File f;
	private final DateFormat dateFormat = new SimpleDateFormat(
			"yyyy/MM/dd HH:mm:ss.SSS");
	private final Calendar cal = Calendar.getInstance();
	private final TwitterStream twitterStream;
	private boolean isDone = false;

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
	
	public void go() {
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
		String track[] = { "oil price", 
				   "crude oil",
				   "gold price",
				   "commodity price",
				   "commodity index",
				   "gold index",
				   "energy price",
				   "energy demand",
				   "energy supply",
				   "syria",
				   "egypt",
				   "iran",
				   "israel",
				   "stock market",
				   "commodity futures",
				   "gas pump prices",
				   "bernanke",
				   "federal reserve",
				   "natural disaster",
				   "hurricane us",
				   "tornado us",
				   "earth quake us",
				   "earth quake middle east",
				   "inflation",
				   "national debt",
				   "epa regulations",
				   "natural gas",
				   "oil refinery",
				   "oil pipeline",
				   "suez canal",
				   "offshore drilling",
				   "renewable energy",
				   "carbon tax",
				   "greenhouse gas",
				   "fracking",
				   "oil spill" };
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
				+ "/src/server/www/twitter.rss";
		File destinationFile = new File(destination);
		this.f.renameTo(destinationFile);
		this.isDone = true;
	}

	private synchronized void printHeader() {
		try {
			String buildDate = dateFormat.format(cal.getTime());
			String s = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?> \n"
					+ "<rss version=\"2.0\"> \n" + "<channel>\n"
					+ "<title>Twitter</title> \n"
					+ "<description>AugurWorks First Guess</description>\n"
					+ "<link>localhost:8000/twitter.rss</link> \n" + "<lastBuildDate>" + buildDate
					+ "</lastBuildDate> \n" + "<pubDate>" + buildDate
					+ "</pubDate> \n"
					+ "<ttl>1800</ttl>\n";
			bw.write(s);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private synchronized void printItem(Status status) {
		try {
			if (status.getUser().getLang().equals("en")) {
				String s = "<item> \n" + "<title>"
						+ status.getUser().getScreenName() + "</title>\n"
						+ "<description>" + status.getText() + "</description>\n"
						+ "<link>http://www.twitter.com/" + status.getUser().getScreenName() + "/status/" + status.getId() + "</link>\n"
						+ "<guid>" + status.getId() + "</guid>"
						+ "<pubDate>"
						+ dateFormat.format(status.getCreatedAt())
						+ "</pubDate>\n" + "</item>\n";
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
	
	public boolean isDone() {
		return isDone;
	}
}