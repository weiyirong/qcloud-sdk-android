package com.tencent.cos.xml.transfer;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by rickenwang on 2019-12-02.
 * <p>
 * Copyright (c) 2010-2020 Tencent Cloud. All rights reserved.
 */
public enum TransferStatus {

    /**
     * This state represents a transfer that constraints are not met, and
     * has not yet queued.
     */
    CONSTRAINED,

    /**
     * This state represents a transfer that has been queued, but has not yet
     * started
     * <br>
     */
    WAITING,

    /**
     * This state represents a transfer that is currently uploading or
     * downloading data
     */
    IN_PROGRESS,

    /**
     * This state represents a transfer that is paused manual
     */
    PAUSED,

    /**
     * This state represents a transfer that has been resumed and queued for
     * execution, but has not started to actively transfer data.
     * <br>
     */
    RESUMED_WAITING,

    /**
     * This state represents a transfer that is completed
     */
    COMPLETED,

    /**
     * This state represents a transfer that is canceled
     */
    CANCELED,

    /**
     * This state represents a transfer that has failed
     */
    FAILED,
    /**
     * This is an internal value used to detect if the current transfer is in an
     * unknown state
     */
    UNKNOWN;

    private static final Map<String, TransferStatus> MAP;
    static {
        MAP = new HashMap<String, TransferStatus>();
        for (final TransferStatus state : TransferStatus.values()) {
            MAP.put(state.toString(), state);
        }
    }


    /**
     * Returns the transfer state from string
     *
     * @param stateAsString state of the transfer represented as string.
     * @return the {@link TransferStatus}
     */
    public static TransferStatus getState(String stateAsString) {
        if (MAP.containsKey(stateAsString)) {
            return MAP.get(stateAsString);
        }

        return UNKNOWN;
    }
}


