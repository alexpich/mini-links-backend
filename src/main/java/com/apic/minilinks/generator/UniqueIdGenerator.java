package com.apic.minilinks.generator;

import java.time.Instant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 *
 * @author alexpich
 */
@Component
public class UniqueIdGenerator {

    // Custom epoch equivalent to Nov 04, 2010, 01:42:54 UTC
    private static final long CUSTOM_EPOCH = 1288834974657L;

    // Number of bits for each section
    private static final long TIMESTAMP_BITS = 41L;
    private static final long DATA_CENTER_ID_BITS = 5L;
    private static final long MACHINE_ID_BITS = 5L;
    private static final long SEQUENCE_BITS = 12L;

    // Max values for data center ID, machine ID, and sequence number
    private static final long MAX_DATA_CENTER_ID = (1L << DATA_CENTER_ID_BITS) - 1;
    private static final long MAX_MACHINE_ID = (1L << MACHINE_ID_BITS) - 1;
    private static final long MAX_SEQUENCE = (1L << SEQUENCE_BITS) - 1;

    // Shifts for left and right sections
    private static final long TIMESTAMP_SHIFT = DATA_CENTER_ID_BITS + MACHINE_ID_BITS + SEQUENCE_BITS;
    private static final long DATA_CENTER_ID_SHIFT = MACHINE_ID_BITS + SEQUENCE_BITS;
    private static final long MACHINE_ID_SHIFT = SEQUENCE_BITS;

    private final long dataCenterId;
    private final long machineId;
    private long lastTimestamp = -1L;
    private long sequence = 0L;

    public UniqueIdGenerator(
            @Value("${minilinks.dataCenterId:0}") long dataCenterId,
            @Value("${minilinks.machineId:0}") long machineId) {
        if (dataCenterId > MAX_DATA_CENTER_ID || machineId > MAX_MACHINE_ID) {
            throw new IllegalArgumentException("Data center ID or machine ID out of range");
        }
        this.dataCenterId = dataCenterId;
        this.machineId = machineId;
    }

    public synchronized long generateUniqueId() {
        long currentTimestamp = getCurrentTimestamp();

        if (currentTimestamp < lastTimestamp) {
            throw new IllegalStateException("Clock moved backwards. Refusing to generate ID.");
        }

        if (currentTimestamp == lastTimestamp) {
            sequence = (sequence + 1) & MAX_SEQUENCE;
            if (sequence == 0) {
                // Sequence overflow, wait until next millisecond
                currentTimestamp = getNextTimestamp(lastTimestamp);
            }
        } else {
            sequence = 0L;
        }

        lastTimestamp = currentTimestamp;

        return ((currentTimestamp - CUSTOM_EPOCH) << TIMESTAMP_SHIFT)
                | (dataCenterId << DATA_CENTER_ID_SHIFT)
                | (machineId << MACHINE_ID_SHIFT)
                | sequence;
    }

    private long getCurrentTimestamp() {
        return Instant.now().toEpochMilli();
    }

    private long getNextTimestamp(long lastTimestamp) {
        long currentTimestamp = getCurrentTimestamp();
        while (currentTimestamp <= lastTimestamp) {
            currentTimestamp = getCurrentTimestamp();
        }
        return currentTimestamp;
    }

}
