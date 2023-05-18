package de.minetrain.minechat.utils;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;

public class CallCounter {
    private Deque<Instant> callTimes; // Stores the timestamps of method calls
    
    public CallCounter() {
        callTimes = new ArrayDeque<>();
    }
    
    /**
     * Returns the number of times the method has been called in the last 60 seconds.
     * @return the number of method calls in the last 60 seconds
     */
    public int getCallCount() {
        cleanupCallTimes();
        return callTimes.size();
    }
    
    /**
     * Records the current call time and cleans up any outdated call times.
     * @return the number of method calls in the last 60 seconds
     */
    public int recordCallTime() {
        cleanupCallTimes();
        callTimes.addLast(Instant.now());
        return callTimes.size();
    }
    
    /**
     * Removes any call times that are older than 60 seconds.
     */
    private void cleanupCallTimes() {
        Instant currentTime = Instant.now();
        Instant timeLimit = currentTime.minus(Duration.ofSeconds(60));
        
        while (!callTimes.isEmpty() && callTimes.getFirst().isBefore(timeLimit)) {
            callTimes.removeFirst();
        }
    }
}

