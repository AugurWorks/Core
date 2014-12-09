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
    private final int timeoutSeconds;
    private final Semaphore semaphore;

    public AlfredDirectoryListener(int numThreads, int timeoutSeconds) {
        this.exec = Executors.newCachedThreadPool();
        this.semaphore = new Semaphore(numThreads);
        this.timeoutSeconds = timeoutSeconds;
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

    public boolean isShutdown() {
        return exec.isTerminated();
    }

    @Override
    public void onFileCreate(File changedFile) {
        try {
            onFileCreateUnsafe(changedFile);
        } catch (Throwable t) {
            System.err.println("Error thrown on file create");
            t.printStackTrace();
        }
    }

    private void onFileCreateUnsafe(File changedFile) {
        System.out.println("File created " + changedFile);
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

                    System.out.println("Starting training for file " + fileName + " with time limit of " + timeoutSeconds + " seconds.");
                    long startTime = System.currentTimeMillis();
                    RectNetFixed net = RectNetFixed.trainFile(fileName,
                                                              true,
                                                              fileName + "." + NetType.SAVE.getSuffix().toLowerCase(),
                                                              false,
                                                              timeoutSeconds * 1000);
                    System.out.println("Training complete for file " + net + " after " + TimeUtils.formatTimeSince(startTime));

                    System.out.println("Saving net for file " + fileName);
                    RectNetFixed.saveNet(fileName + "." + NetType.SAVE.getSuffix().toLowerCase(), net);
                    System.out.println("Net saved for file " + fileName);

                    System.out.println("Writing augout file for " + fileName);
                    RectNetFixed.writeAugoutFile(fileName + "." + NetType.AUGOUT.getSuffix().toLowerCase(), net);
                    System.out.println("Augout written for " + fileName);
                } catch (Throwable t) {
                    System.err.println("Exception caught during evaluation of " + fileName);
                    t.printStackTrace();
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
