package alfred;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import com.google.common.base.Throwables;


public class AlfredServer {
	
	private static final Logger log = Logger.getLogger(AlfredServer.class);
	
	public static void main(String[] args) {
		BasicConfigurator.configure();
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
			log.error("Unable to initialize file observer. Server will exit now.");
			return;
		}
		log.info(String.format("Alfred Server started on %s with %s threads.",  serverArgs.fileToWatch, serverArgs.numThreads));
		if (serverArgs.serverPort == null) {
			log.info("Will listen for input on System.in");
			listenOnSystemIn(alfredListener);
		} else {
			log.info("Will listen for input on port " + serverArgs.serverPort);
			listenOnPort(alfredListener, serverArgs.serverPort);
		}
        try {
            fileAlterationMonitor.stop();
            fileAlterationObserver.destroy();
        } catch (Throwable e) {
            // closing things. ignore errors.
        }
		log.info("Alfred Server stopped.");
		System.exit(0);
	}
	
	private static void listenOnPort(AlfredDirectoryListener alfredListener, int port) {
		ServerSocket listener = null;
		ExecutorService exec = Executors.newFixedThreadPool(16);
        try {
        	listener = new ServerSocket(port);
        	exec.submit(getShutdownPollThread(alfredListener, exec, listener));
            while (true) {
                Socket socket = listener.accept();
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter writer = new PrintWriter(socket.getOutputStream());
                exec.submit(new AlfredCommunicationHandler(alfredListener, reader, writer));
            }
        } catch (Throwable e) {
			log.error("Exception thrown while listening on port " + port, e);
		} finally {
            IOUtils.closeQuietly(listener);
            exec.shutdownNow();
        }
	}
	
	private static Runnable getShutdownPollThread(final AlfredDirectoryListener listener, 
											      final ExecutorService exec, 
											      final ServerSocket socket) {
		return new Runnable() {
			@Override
			public void run() {	
				while (true) {
					if (listener.isShutdown()) {
						exec.shutdown();
						IOUtils.closeQuietly(socket);
						break;
					} else {
						try {
							Thread.sleep(10000L);
						} catch (InterruptedException e) {
							log.error("Interrupted in shutdown polling thread", e);
							throw Throwables.propagate(e);
						}
					}
				}
			}
		};
	}
	
	private static void listenOnSystemIn(AlfredDirectoryListener alfredListener) {
		BufferedReader commands = new BufferedReader(new InputStreamReader(System.in));
		PrintWriter writer = new PrintWriter(System.out);
		new AlfredCommunicationHandler(alfredListener, commands, writer).run();
	}
	
	private static class AlfredServerArgs {
		public final File fileToWatch;
		public final int numThreads;
		public final Integer serverPort;
		
		public AlfredServerArgs(File fileToWatch, int numThreads, Integer serverPort) {
			this.fileToWatch = fileToWatch;
			this.numThreads = numThreads;
			this.serverPort = serverPort;
		}
	}
	
	private static AlfredServerArgs validateArguments(String[] args) {
		if (args.length != 2 && args.length != 3) {
			usage();
			return null;
		}
		String directory = args[0];
		File f = new File(directory);
		if (f.exists() && !f.isDirectory()) {
			log.error("File " + directory + " already exists.");
			throw new IllegalArgumentException("File " + directory + " already exists.");
		} else if (!f.exists()) { 
			boolean success = f.mkdirs();
			if (!success) {
				log.error("Directory " + directory + " cannot be created.");
				throw new IllegalArgumentException("Directory " + directory + " cannot be created.");
			}
		}
		int numThreads;
		try {
			numThreads = Integer.parseInt(args[1]);
		} catch (NumberFormatException e) {
			log.error("Could not parse " + args[1] + " as an integer.");
			throw new IllegalArgumentException("Could not parse " + args[1] + " as an integer.");
		}
		Integer serverPort;
		if (args.length == 2) {
			serverPort = null;
		} else {
			try {
				serverPort = Integer.parseInt(args[2]);
			} catch (NumberFormatException e) {
				log.error("Could not parse " + args[2] + " as an integer.");
				throw new IllegalArgumentException("Could not parse " + args[2] + " as an integer.");
			}
		}
		return new AlfredServerArgs(f, numThreads, serverPort);
	}
	
	private static void usage() {
		log.info("Usage: AlfredServer <directory to poll> <number of threads for server> [<port to listen on>]");
	}
	
	public enum Command {
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
	
	public static String commands() {
		StringBuilder sb = new StringBuilder("Commands are:\n");
		for (Command command : Command.values()) {
			sb.append("  ").append(command.name()).append(": ").append(command.getHelpText());
			sb.append("\n");
		}
		return sb.toString();
	}
}
