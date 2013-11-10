package com.augurworks.workserver;

import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class ServerStatus {
	private final AtomicInteger numJobsInProgress;
	private final AtomicInteger numJobsCompleted;
	private AtomicBoolean shutdownRequested;
	private final File jobDirectory;
	
	public ServerStatus(File jobDir) {
		this.jobDirectory = jobDir;
		numJobsInProgress = new AtomicInteger(0);
		numJobsCompleted = new AtomicInteger(0);
		shutdownRequested = new AtomicBoolean(false);
	}
	
	public boolean shutdownRequested() {
		return shutdownRequested.get();
	}
	
	public void requestShutdown() {
		shutdownRequested.getAndSet(true);
	}
	
	public int completeJob() {
		numJobsInProgress.decrementAndGet();
		return numJobsCompleted.getAndIncrement();
	}
	
	public int startJob() {
		return numJobsInProgress.getAndIncrement();
	}
	
	public File getJobDirectory() {
		return jobDirectory;
	}

	@Override
	public String toString() {
		return "ServerStatus [numJobsInProgress=" + numJobsInProgress
				+ ", numJobsCompleted=" + numJobsCompleted + ", jobDirectory="
				+ jobDirectory + "]";
	}
}
