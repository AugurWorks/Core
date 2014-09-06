package alfred;

import java.io.File;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.log4j.Logger;

import alfred.Net.NetType;

public class AlfredDirectoryListener extends FileAlterationListenerAdaptor {
    private static final Logger log = Logger.getLogger(AlfredDirectoryListener.class);
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
            log.error("Interrupted while terminating. Will shutdown now.");
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
            log.error("Error thrown on file create", t);
        }
    }

    private void onFileCreateUnsafe(File changedFile) {
        log.info("File created " + changedFile);
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

                    log.info("Starting training for file " + fileName + " with time limit of 1 hour.");
                    long startTime = System.currentTimeMillis();
                    RectNetFixed net = RectNetFixed.trainFile(fileName,
                                                              true,
                                                              fileName + "." + NetType.SAVE.getSuffix().toLowerCase(),
                                                              false,
                                                              timeoutSeconds * 1000);
                    log.info("Training complete for file " + net + " after " + TimeUtils.formatTimeSince(startTime));

                    log.info("Saving net for file " + fileName);
                    RectNetFixed.saveNet(fileName + "." + NetType.SAVE.getSuffix().toLowerCase(), net);
                    log.info("Net saved for file " + fileName);

                    log.info("Writing augout file for " + fileName);
                    RectNetFixed.writeAugoutFile(fileName + "." + NetType.AUGOUT.getSuffix().toLowerCase(), net);
                    log.info("Augout written for " + fileName);
                } catch (Throwable t) {
                    log.error("Exception caught during evaluation of " + fileName, t);
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
