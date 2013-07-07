import java.awt.List;
import java.io.Console;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

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

	public static void main(String[] args) throws IOException {
		System.out.println("Starting connect...");
		final PrintWriter out = new PrintWriter(new FileWriter("C:\\Users\\TheConnMan\\Downloads\\outputfile.txt"));

		System.out.println("What do you want to track? Separate by hitting the return key, end with 'done'");

		ArrayList<String> track = new ArrayList<String>();
		Scanner scanner = new Scanner (System.in);
		while (1==1) {
			String input = scanner.next();
			if (input.equalsIgnoreCase("done")) {
					break;
			} else {
				track.add(input);
			}
		}
		String[] track2 = new String[track.size()];
		for (int i=0; i < track.size(); i++) {
			track2[i]=track.get(i);
		}
		ConfigurationBuilder builder = new ConfigurationBuilder();
		builder.setOAuthAccessToken(ACCESS_TOKEN);
		builder.setOAuthAccessTokenSecret(ACCESS_TOKEN_SECRET);
		builder.setOAuthConsumerKey(CONSUMER_KEY);
		builder.setOAuthConsumerSecret(CONSUMER_SECRET);

		StatusListener listener = new StatusListener() {
			@Override
			public void onStatus(Status status) {
				System.out.println(status.getUser().getName() + " : "
						+ status.getText());
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
		// sample() method internally creates a thread which manipulates
		// TwitterStream and calls these adequate listener methods continuously.
		OAuthAuthorization auth = new OAuthAuthorization(builder.build());
		TwitterStream twitterStream = new TwitterStreamFactory()
				.getInstance(auth);
		twitterStream.addListener(listener);
		//twitterStream.sample();
		FilterQuery query = new FilterQuery();
		//String track[] = {"bomb", "Boston", "gunshot", "police"};
		query.track(track2);
		twitterStream.filter(query);
	}
}
