package com.importsource.chaos.client.assaults;

/**
 * @author hezhuofan
 */
public class LatencyAssaultExecutor implements ChaosMonkeyLatencyAssaultExecutor {
    @Override
    public void execute(long durationInMillis) {
        try {
            Thread.sleep(durationInMillis);
        } catch (InterruptedException e) {
            // do nothing
        }
    }
}
