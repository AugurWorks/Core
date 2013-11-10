package com.augurworks.workserver;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;

public class Main {
	private final ExecutorService workers;
	private ServerStatus status;
	private ConcurrentLinkedQueue<Future<JobResponse>> inProgressJobs;
	
	public Main(int numThreads, File jobDir) {
		this.workers = Executors.newFixedThreadPool(numThreads);
		this.status = new ServerStatus(jobDir);
		this.inProgressJobs = new ConcurrentLinkedQueue<Future<JobResponse>>();
	}
	
	public void start() {
		startInputListener();
		startFileListener();
		startOutputWriter();
	}
	
	public void startFileListener() {
		Thread listener = new Thread(fileListener());
		listener.start();
	}
	
	public boolean shutdownRequested() {
		return this.status.shutdownRequested();
	}
	
	public Runnable fileListener() {
		return new Runnable() {
			@Override
			public void run() {
				while (true) {
					if (shutdownRequested()) {
						return;
					}
					Collection<File> files = FileUtils.listFiles(status.getJobDirectory(),
							TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
					for (File f : files) {
						try {
							JobType job = JobType.fromFilename(f.getAbsolutePath());
							System.out.println("Job " + job + " detected at " + f);
							submitJob(job.execute(f.getAbsolutePath(), FileUtils.readFileToString(f)));
							f.delete();
						} catch (IllegalArgumentException e) {
							// do nothing
						} catch (IOException e) {
							System.err.println("Could not read file " + f);
							e.printStackTrace();
						}
					}
					sleep(2000);
				}
			}
		};
	}
	
	public void submitJob(Callable<JobResponse> callable) {
		status.startJob();
		inProgressJobs.add(workers.submit(callable));
	}
	
	public void startOutputWriter() {
		Thread output = new Thread(outputWriter());
		output.start();
	}
	
	public Runnable outputWriter() {
		return new Runnable() {
			@Override
			public void run() {
				while (true) {
					if (shutdownRequested()) {
						return;
					}
					Iterator<Future<JobResponse>> iterator = inProgressJobs.iterator();
					while (iterator.hasNext()) {
						Future<JobResponse> job = iterator.next();
						if (job.isDone()) {
							iterator.remove();
							try {
								writeJob(job.get());
							} catch (Exception e) {
								System.err.println("Job " + job + " could not be written.");
								e.printStackTrace();
							}
							status.completeJob();
						}
					}
					sleep(100);
				}
			}
		};
	}
	
	public void writeJob(JobResponse job) {
		try {
			FileUtils.writeStringToFile(new File(job.getFilepath()), job.getContents());
		} catch (IOException e) {
			System.err.println("Could not write job " + job + " to file.");
			e.printStackTrace();
		}
	}
	
	public void startInputListener() {
		Thread listenerThread = new Thread(inputListener());
		listenerThread.start();
	}
	
	public static void main(String[] args) {
		if (args.length < 2) {
			System.out.println("Usage: <absolute path> <number of worker threads>");
			System.exit(1);
		}
		String directory = args[0];
		int nThreads = Integer.parseInt(args[1]);
		Main main = new Main(nThreads, new File(directory));
		main.start();
	}
		
	public void sleep(long l) {
		try {
			Thread.sleep(l);
		} catch (InterruptedException e) {
			System.err.println("Error occurred: " + e);
			e.printStackTrace();
		}
	}
	
	public void initiateShutdown() {
		this.status.requestShutdown();
		this.workers.shutdown();
		try {
			this.workers.awaitTermination(1L, TimeUnit.MINUTES);
		} catch (InterruptedException e) {
			System.err.println("Error occurred: " + e);
			e.printStackTrace();
		}
	}
	
	public void executeCommand(ServerCommand command) {
		switch (command) {
		case REQUEST_SHUTDOWN:
			System.out.println("Requesting shutdown...");
			initiateShutdown();
		case STATUS:
			System.out.println(status);
			return;
		default:
			return;
		}
	}
	
	public Runnable inputListener() {
		return new Runnable() {
			@Override
			public void run() {
			    BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			    try {
				    String s;
				    while ((s = in.readLine()) != null) {
				    	try {
				    		ServerCommand command = ServerCommand.fromCommand(s);
				    		executeCommand(command);
				    	} catch (IllegalArgumentException e) {
				    		System.err.println(e.getLocalizedMessage());
				    	}
				    	if (shutdownRequested()) {
			    			return;
			    		}
				    }
			    } catch (Exception e) {
			    	System.err.println("Error occurred...");
			    	e.printStackTrace();
			    }
			}
		};
	}
}
