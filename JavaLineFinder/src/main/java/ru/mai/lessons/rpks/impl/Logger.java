package ru.mai.lessons.rpks.impl;

import java.util.concurrent.atomic.AtomicLong;

public class Logger {
    private final long totalBytes;
    private final AtomicLong processedBytes = new AtomicLong(0);
    private final long startTime;

    public Logger(long totalBytes) {
        this.totalBytes = totalBytes;
        this.startTime = System.currentTimeMillis();
    }

    public void addBytes(long bytes) {
        processedBytes.addAndGet(bytes);
    }

    public void log() {
        long elapsedTime = System.currentTimeMillis() - startTime;
        double progress = ((double) processedBytes.get() / totalBytes) * 100;
        System.out.printf("Elapsed time: %dms, progress: %.1f%%\n", elapsedTime, progress);
    }
}
