package com.importsource.chaos.client.assaults;

import com.importsource.chaos.client.component.MetricEventPublisher;
import com.importsource.chaos.client.component.MetricType;
import com.importsource.chaos.client.configuration.ChaosMonkeySettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;

import java.util.Vector;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Benjamin Wilms
 * @author hezhuofan
 */
public class MemoryAssault implements ChaosMonkeyRuntimeAssault {

    private static final Logger LOGGER = LoggerFactory.getLogger(MemoryAssault.class);
    private static final AtomicLong stolenMemory = new AtomicLong(0);

    private final Runtime runtime;
    private final AtomicBoolean inAttack = new AtomicBoolean(false);
    private final ChaosMonkeySettings settings;
    private final MetricEventPublisher metricEventPublisher;

    public MemoryAssault(Runtime runtime, ChaosMonkeySettings settings, MetricEventPublisher metricEventPublisher) {
        this.runtime = runtime;
        this.settings = settings;
        this.metricEventPublisher = metricEventPublisher;
    }

    @Override
    public boolean isActive() {
        return settings.getAssaultProperties().isMemoryActive();
    }

    @Override
    @Async
    public void attack() {
        LOGGER.info("Chaos Monkey - memory assault");

        // metrics
        if (metricEventPublisher != null)
            metricEventPublisher.publishMetricEvent(MetricType.MEMORY_ASSAULT);

        if (inAttack.compareAndSet(false, true)) {
            try {
                LOGGER.debug("Detected java version: " + System.getProperty("java.version"));
                eatFreeMemory();
            } finally {
                inAttack.set(false);
            }
        }

        LOGGER.info("Chaos Monkey - memory assault cleaned up");
    }

    private void eatFreeMemory() {
        @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
        Vector<byte[]> memoryVector = new Vector<>();

        long stolenMemoryTotal = 0L;

        while (isActive()) {
            // overview of memory methods in java https://stackoverflow.com/a/18375641
            long freeMemory = runtime.freeMemory();
            long usedMemory = runtime.totalMemory() - freeMemory;

            if (cannotAllocateMoreMemory()) {
                LOGGER.debug("Cannot allocate more memory");
                break;
            }

            LOGGER.debug("Used memory in bytes: " + usedMemory);

            stolenMemoryTotal = stealMemory(memoryVector, stolenMemoryTotal, getBytesToSteal());
            waitUntil(settings.getAssaultProperties().getMemoryMillisecondsWaitNextIncrease());
        }

        // Hold memory level and cleanUp after, only if experiment is running
        if (isActive()) {
            LOGGER.info("Memory fill reached, now sleeping and holding memory");
            waitUntil(settings.getAssaultProperties().getMemoryMillisecondsHoldFilledMemory());
        }

        // clean Vector
        memoryVector.clear();
        // quickly run gc for reuse
        runtime.gc();

        long stolenAfterComplete = MemoryAssault.stolenMemory.addAndGet(-stolenMemoryTotal);
        metricEventPublisher.publishMetricEvent(MetricType.MEMORY_ASSAULT_MEMORY_STOLEN, stolenAfterComplete);
    }

    private boolean cannotAllocateMoreMemory() {
        double limit = runtime.maxMemory() * settings.getAssaultProperties().getMemoryFillTargetFraction();
        return runtime.totalMemory() > Math.floor(limit);
    }

    private int getBytesToSteal() {
        int amount =
                (int) (runtime.freeMemory() * settings.getAssaultProperties().getMemoryFillIncrementFraction());
        boolean isJava8 = System.getProperty("java.version").startsWith("1.8");

        // TODO: Check again when JAVA 8 can be dropped.
        // seems filling more than 256 MB per slice is bad on java 8
        // we keep running into heap errors and other OOMs.
        return isJava8 ? Math.min(SizeConverter.toBytes(256), amount) : amount;
    }

    private long stealMemory(Vector<byte[]> memoryVector, long stolenMemoryTotal,
                             int bytesToSteal) {
        memoryVector.add(createDirtyMemorySlice(bytesToSteal));

        stolenMemoryTotal += bytesToSteal;
        long newStolenTotal = MemoryAssault.stolenMemory.addAndGet(bytesToSteal);
        metricEventPublisher.publishMetricEvent(MetricType.MEMORY_ASSAULT_MEMORY_STOLEN, newStolenTotal);
        LOGGER.debug("Chaos Monkey - memory assault increase, free memory: " + SizeConverter.toMegabytes(runtime
                .freeMemory()));

        return stolenMemoryTotal;
    }

    private byte[] createDirtyMemorySlice(int size) {
        byte[] b = new byte[size];
        for (int idx = 0; idx < size; idx += 4096) { // 4096
            // is commonly the size of a memory page, forcing a commit
            b[idx] = 19;
        }

        return b;
    }

    private void waitUntil(int ms) {
        final long startNano = System.nanoTime();
        long now = startNano;
        while (startNano + TimeUnit.MILLISECONDS.toNanos(ms) > now && isActive()) {
            try {
                long elapsed = TimeUnit.NANOSECONDS.toMillis(startNano - now);
                Thread.sleep(Math.min(100, ms - elapsed));
                now = System.nanoTime();
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}