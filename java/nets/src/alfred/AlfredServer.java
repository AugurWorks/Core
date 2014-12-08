package alfred;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.apache.log4j.BasicConfigurator;

import com.google.common.base.Throwables;


public class AlfredServer {

    private static final int DEFAULT_NUM_THREADS = 16;
    private static final int DEFAULT_TIMEOUT_SECONDS = 3600;

    public static void main(String[] args) {
        BasicConfigurator.configure();
        AlfredServerArgs serverArgs = validateArguments(args);
        if (serverArgs == null) {
            return;
        }
        FileAlterationMonitor fileAlterationMonitor = new FileAlterationMonitor();
        FileAlterationObserver fileAlterationObserver = new FileAlterationObserver(serverArgs.fileToWatch);
        fileAlterationMonitor.addObserver(fileAlterationObserver);
        AlfredDirectoryListener alfredListener = new AlfredDirectoryListener(serverArgs.numThreads, serverArgs.timeoutSeconds);
        fileAlterationObserver.addListener(alfredListener);
        try {
            fileAlterationObserver.initialize();
            fileAlterationMonitor.start();
        } catch (Exception e) {
            System.err.println("Unable to initialize file observer. Server will exit now.");
            return;
        }
        System.out.println(String.format("Alfred Server started on %s with %s threads and a job timeout of %s seconds",
                serverArgs.fileToWatch, serverArgs.numThreads, serverArgs.timeoutSeconds));
        if (serverArgs.serverPort == null) {
            System.out.println("Will listen for input on System.in");
            listenOnSystemIn(alfredListener);
        } else {
            System.out.println("Will listen for input on port " + serverArgs.serverPort);
            listenOnPort(alfredListener, serverArgs.serverPort);
        }
        try {
            fileAlterationMonitor.stop();
            fileAlterationObserver.destroy();
        } catch (Throwable e) {
            // closing things. ignore errors.
        }
        System.out.println("Alfred Server stopped.");
        System.exit(0);
    }

    private static CommandLine parseArgs(String[] args) {
        Options options = getOptions();
        CommandLine cmd;
        CommandLineParser parser = new BasicParser();
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.err.println("Unable to parse command line.");
            e.printStackTrace();
            usage();
            throw Throwables.propagate(e);
        }
        return cmd;
    }

    private static Options getOptions() {
        Options options = new Options();

        Option dirOption = new Option("d", "directory", true, "Directory to listen on.");
        dirOption.setRequired(true);
        Option threadOption = new Option("t", "num-threads", true, "Number of threads for alfred to use. " +
                "Default " + DEFAULT_NUM_THREADS + ".");
        Option portOption = new Option("p", "port", true, "Port to listen on. Will listen on System.in if unspecified.");
        Option timeoutOption = new Option("s", "timeout", true, "Timeout in seconds for a net to train. " +
                "Default " + DEFAULT_TIMEOUT_SECONDS + ".");

        options.addOption(dirOption);
        options.addOption(threadOption);
        options.addOption(portOption);
        options.addOption(timeoutOption);

        return options;
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
                exec.submit(new AlfredCommunicationHandler(alfredListener, reader, writer, socket));
            }
        } catch (Throwable e) {
            System.err.println("Exception thrown while listening on port " + port);
            e.printStackTrace();
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
                            System.err.println("Interrupted in shutdown polling thread");
                            e.printStackTrace();
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
        public final int timeoutSeconds;

        public AlfredServerArgs(File fileToWatch, int numThreads, Integer serverPort, int timeoutSeconds) {
            this.fileToWatch = fileToWatch;
            this.numThreads = numThreads;
            this.serverPort = serverPort;
            this.timeoutSeconds = timeoutSeconds;
        }
    }

    private static AlfredServerArgs validateArguments(String[] args) {
        CommandLine cmd = parseArgs(args);
        if (!cmd.hasOption("d")) {
            System.err.println("Could not parse command line");
            usage();
            throw new IllegalArgumentException("Could not parse command line");
        }
        String directory = cmd.getOptionValue("d");
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
        int numThreads = DEFAULT_NUM_THREADS;
        if (cmd.hasOption("t")) {
            String threads = cmd.getOptionValue("t");
            try {
                numThreads = Integer.parseInt(threads);
            } catch (NumberFormatException e) {
                System.err.println("Could not parse " + threads + " as an integer.");
                e.printStackTrace();
                throw new IllegalArgumentException("Could not parse " + threads + " as an integer.", e);
            }
        }
        Integer serverPort = null;
        if (cmd.hasOption("p")) {
            String port = cmd.getOptionValue("p");
            try {
                serverPort = Integer.parseInt(port);
            } catch (NumberFormatException e) {
                System.err.println("Could not parse " + port + " as an integer.");
                e.printStackTrace();
                throw new IllegalArgumentException("Could not parse " + port + " as an integer.", e);
            }
        }
        int timeoutSeconds = DEFAULT_TIMEOUT_SECONDS;
        if (cmd.hasOption("s")) {
            String timeout = cmd.getOptionValue("s");
            try {
                timeoutSeconds = Integer.parseInt(timeout);
            } catch (NumberFormatException e) {
                System.err.println("Could not parse " + timeout + " as an integer.");
                e.printStackTrace();
                throw new IllegalArgumentException("Could not parse " + timeout + " as an integer.", e);
            }
        }
        return new AlfredServerArgs(f, numThreads, serverPort, timeoutSeconds);
    }

    private static void usage() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("alfred", getOptions());
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
