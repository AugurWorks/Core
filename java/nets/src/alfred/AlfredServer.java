package alfred;

import java.io.File;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;


public class AlfredServer {
	public static void main(String[] args) {
		AlfredServerArgs serverArgs = validateArguments(args);
		if (serverArgs == null) {
			return;
		}
		FileAlterationMonitor fileAlterationMonitor = new FileAlterationMonitor();
		FileAlterationObserver fileAlterationObserver = new FileAlterationObserver(serverArgs.fileToWatch);
		fileAlterationMonitor.addObserver(fileAlterationObserver);
		AlfredDirectoryListener alfredListener = new AlfredDirectoryListener(serverArgs.numThreads);
		fileAlterationObserver.addListener(alfredListener);
		try {
			fileAlterationObserver.initialize();
			fileAlterationMonitor.start();
		} catch (Exception e) {
			System.err.println("Unable to initialize file observer. Server will exit now.");
			return;
		}
		Scanner commands = new Scanner(System.in);
		System.out.println("Alfred Server started.");
		boolean shutdownRequested = false;
		while (!shutdownRequested) {
			String nextLine = commands.nextLine();
			Command command = Command.fromString(nextLine);
			if (command == null) {
				commands();
				continue;
			}
			if (command == Command.SHUTDOWN || command == Command.SHUTDOWN_NOW) {
				shutdownRequested = true;
			}
			handleCommand(command, nextLine, alfredListener);
		}
        try {
            commands.close();
            fileAlterationMonitor.stop();
            fileAlterationObserver.destroy();
        } catch (Exception e) {
            // closing things. ignore errors.
        }
		System.out.println("Alfred Server stopped.");
		System.exit(0);
	}
	
	private static void handleCommand(Command command, String line, AlfredDirectoryListener alfredListener) {
		switch(command) {
		case SHUTDOWN:
			String[] split = line.split(" ");
			if (split.length < 2) {
				System.err.println("Could not parse time to wait for shutdown. Will shutdown now.");
			} else {
				String minutesToWaitString = split[1];
				try {
					int minutesToWait = Integer.parseInt(minutesToWaitString);
					alfredListener.shutdownAndAwaitTermination(minutesToWait, TimeUnit.MINUTES);
					return;
				} catch (NumberFormatException e) {
					System.err.println("Could not parse time to wait for shutdown. Will shutdown now.");
				}
			}
		case SHUTDOWN_NOW:
			alfredListener.shutdownNow();
			return;
		case STATUS:
			System.out.println("Server Status:");
			System.out.println("  Jobs in progress : " + alfredListener.getJobsInProgress());
			System.out.println("  Jobs submitted   : " + alfredListener.getJobsSubmitted());
			System.out.println("  Jobs completed   : " + alfredListener.getJobsCompleted());
			return;
		default: 
			return;
		}
	}
	
	private static class AlfredServerArgs {
		public final File fileToWatch;
		public final int numThreads;
		
		public AlfredServerArgs(File fileToWatch, int numThreads) {
			this.fileToWatch = fileToWatch;
			this.numThreads = numThreads;
		}
	}
	
	private static AlfredServerArgs validateArguments(String[] args) {
		if (args.length != 2) {
			usage();
			return null;
		}
		String directory = args[0];
		File f = new File(directory);
		if (f.exists() && !f.isDirectory()) {
			System.err.println("File " + directory + " already exists.");
			throw new IllegalArgumentException("File " + directory + " already exists.");
		} else if (!f.exists()) { 
			boolean success = f.mkdirs();
			if (!success) {
				System.err.println("Directory " + directory + " cannot be created.");
				throw new IllegalArgumentException("Directory " + directory + " cannot be created.");
			}
		}
		int numThreads;
		try {
			numThreads = Integer.parseInt(args[1]);
		} catch (NumberFormatException e) {
			System.err.println("Could not parse " + args[1] + " as an integer.");
			throw new IllegalArgumentException("Could not parse " + args[1] + " as an integer.");
		}
		return new AlfredServerArgs(f, numThreads);
	}
	
	private static void usage() {
		System.out.println("Usage: AlfredServer <directory to poll> <number of threads for server>");
	}
	
	private enum Command {
		SHUTDOWN_NOW("shutdown now", "Causes the server to shutdown."),
		SHUTDOWN("shutdown", "Causes the server to shut down, waiting " +
				"up to the given number of minutes for task completion."),
		STATUS("status", "Prints the status of the server."),
		;
		
		private String helpText;
		private String command;
		
		private Command(String command, String helpText) {
			this.command = command;
			this.helpText = helpText;
		}
		
		public String getHelpText() {
			return helpText;
		}
		
		public String getCommand() {
			return command;
		}
		
		public static Command fromString(String input) {
			for (Command command : values()) {
				if (input.toLowerCase().startsWith(command.getCommand())) {
					return command;
				}
			}
			return null;
		}
	}
	
	private static void commands() {
		System.out.println("Commands are:");
		for (Command command : Command.values()) {
			System.out.println("  " + command.name() + ": " + command.getHelpText());
		}
	}
}
