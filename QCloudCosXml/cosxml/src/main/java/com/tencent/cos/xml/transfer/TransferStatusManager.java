package com.tencent.cos.xml.transfer;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by rickenwang on 2018/5/16.
 * <p>
 * Copyright (c) 2010-2017 Tencent Cloud. All rights reserved.
 */

public class TransferStatusManager {


    private Map<String, TransferState> transferStatus;

    private TransferUtility transferUtility;

    TransferStatusManager(TransferUtility transferUtility) {

        transferStatus = new HashMap<>();
        this.transferUtility = transferUtility;
    }

    /**
     * Put or update transfer status.
     * <br/>
     *
     *
     * @param id
     * @param newState
     */
    void updateState(String id, TransferState newState) {

        // store and refresh the new transfer state in transferStatusManager.
        transferStatus.put(id, newState);



        TransferRunnable transferRunnable = transferUtility.getTransferRunnable(id);
        if (transferRunnable != null) {

            // notify user the transfer status is changed.
            transferRunnable.notifyTransferStateChange(newState);

            //
            transferRunnable.getTransferObserver().setTransferState(newState);
        }

        // if the transfer task don't need to be exist anymore, remove it from TransferStatusManager.
        if (newState == TransferState.CANCELED || newState == TransferState.COMPLETED) {
            removeState(id);
        }
    }

    void removeState(String id) {

        transferStatus.remove(id);
    }

    TransferState getState(String id) {

        return transferStatus.get(id);
    }

}
