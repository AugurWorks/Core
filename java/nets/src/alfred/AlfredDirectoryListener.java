package alfred;

import java.io.File;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;

import alfred.Net.NetType;

public class AlfredDirectoryListener extends FileAlterationListenerAdaptor {
	private final ExecutorService exec;
	private AtomicInteger jobsSubmitted = new AtomicInteger();
	private AtomicInteger jobsCompleted = new AtomicInteger();
	private AtomicInteger jobsInProgress = new AtomicInteger();
	private final Semaphore semaphore;
	
	public AlfredDirectoryListener(int numThreads) {
		this.exec = Executors.newCachedThreadPool();
		this.semaphore = new Semaphore(numThreads);
	}
	
	public int getJobsSubmitted() {
		return jobsSubmitted.get();
	}
	
	public int getJobsCompleted() {
		return jobsCompleted.get();
	}
	
	public int getJobsInProgress() {
		return jobsInProgress.get();
	}
	
	public void shutdownNow() {
		exec.shutdownNow();
	}
	
	public void shutdownAndAwaitTermination(long timeout, TimeUnit unit) {
		exec.shutdown();
		try {
			exec.awaitTermination(timeout, unit);
		} catch (InterruptedException e) {
			System.err.println("Interrupted while terminating. Will shutdown now.");
			throw new IllegalStateException("Interrupted while terminating. Will shutdown now.");
		}
	}

	@Override
	public void onFileCreate(File changedFile) {
		System.out.println("File created! " + changedFile);
		NetType netType = Net.NetType.fromFile(changedFile.getName());
		if (netType == NetType.TRAIN) {
			exec.submit(getTrainCallable(changedFile.getAbsolutePath()));
		}
	}
	
	private Callable<Void> getTrainCallable(final String fileName) {
		return new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				jobsSubmitted.incrementAndGet();
				try {
					semaphore.acquire();
					jobsInProgress.incrementAndGet();
					RectNetFixed.trainFile(fileName, false, fileName + "." + NetType.SAVE, false);
				} finally {
					semaphore.release();
					jobsInProgress.decrementAndGet();
					jobsCompleted.incrementAndGet();
				}
				return null;
			}
		};
	}
	
}
