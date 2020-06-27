package com.importsource.chaos.client.assaults;

/**
 * A way to interfere with the application. Implementations will be either {@link ChaosMonkeyRuntimeAssault}
 * or {@link ChaosMonkeyRequestAssault}, depending if the interference is on the request or runtime level.
 *
 * Implementing this interface directly is discouraged, and will generally be treated as a Request-level assault
 * @author Thorsten Deelmann
 * @author hezhuofan
 */
public interface ChaosMonkeyAssault {

    boolean isActive();

    void attack();

}
