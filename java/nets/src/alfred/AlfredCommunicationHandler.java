package alfred;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import alfred.AlfredServer.Command;

public class AlfredCommunicationHandler implements Runnable {
	private static final Logger log = Logger.getLogger(AlfredCommunicationHandler.class);
	private final AlfredDirectoryListener alfredListener;
	private final BufferedReader reader;
	private final PrintWriter writer;
	
	public AlfredCommunicationHandler(AlfredDirectoryListener listener, 
			BufferedReader reader, PrintWriter writer) {
		this.alfredListener = listener;
		this.reader = reader;
		this.writer = writer;
	}

	@Override
	public void run() {
		try {
			boolean shutdownRequested = false;
			while (!shutdownRequested) {
				writer.flush();
				String nextLine = reader.readLine();
				if (nextLine.trim().isEmpty()) {
					continue;
				}
				Command command = Command.fromString(nextLine);
				if (command == null) {
					writer.write(AlfredServer.commands());
					continue;
				}
				if (command == Command.SHUTDOWN || command == Command.SHUTDOWN_NOW) {
					shutdownRequested = true;
				}
				handleCommand(command, nextLine, alfredListener);
			}
		} catch (Throwable t) {
			log.error("Exception caught while communicating with client", t);
		} finally {
			IOUtils.closeQuietly(reader);
			IOUtils.closeQuietly(writer);
		}
	}
	
	private void handleCommand(Command command, String line, AlfredDirectoryListener alfredListener) {
		switch(command) {
		case SHUTDOWN:
			String[] split = line.split(" ");
			if (split.length < 2) {
				log.error("Could not parse time to wait for shutdown. Will shutdown now.");
				writer.write("Could not parse time to wait for shutdown. Will shutdown now.");
			} else {
				String minutesToWaitString = split[1];
				try {
					int minutesToWait = Integer.parseInt(minutesToWaitString);
					alfredListener.shutdownAndAwaitTermination(minutesToWait, TimeUnit.MINUTES);
					return;
				} catch (NumberFormatException e) {
					log.error("Could not parse time to wait for shutdown. Will shutdown now.");
					writer.write("Could not parse time to wait for shutdown. Will shutdown now.");
				}
			}
		case SHUTDOWN_NOW:
			alfredListener.shutdownNow();
			return;
		case STATUS:
			StringBuilder sb = new StringBuilder("Server Status:\n");
			sb.append("  Jobs in progress : " + alfredListener.getJobsInProgress()).append("\n");
			sb.append("  Jobs submitted   : " + alfredListener.getJobsSubmitted()).append("\n");
			sb.append("  Jobs completed   : " + alfredListener.getJobsCompleted()).append("\n");
			writer.write(sb.toString());
			return;
		default: 
			return;
		}
	}
}
