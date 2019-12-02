package com.tencent.cos.xml.transfer.constraint;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Created by rickenwang on 2019-11-25.
 * <p>
 * Copyright (c) 2010-2020 Tencent Cloud. All rights reserved.
 */
@Entity
public class Constraints {

    public static Constraints NONE = new Builder().build();

    public NetworkType networkType;

    @PrimaryKey private int fixId;


    public Constraints() {}

    private Constraints(Builder builder) {

        networkType = builder.getNetworkType();
        fixId = 1;
    }

    /**
     *
     * @param networkType
     * @return
     */
    public boolean isSatisfyNetwork(NetworkType networkType) {
        return networkType == NetworkType.ANY || networkType == this.networkType;
    }

    public boolean isSatisfy(Constraints constraints) {

        return false;
    }

    public static Constraints getNONE() {
        return NONE;
    }

    public int getFixId() {
        return fixId;
    }

    public void setFixId(int fixId) {
        this.fixId = fixId;
    }

    public NetworkType getNetworkType() {
        return networkType;
    }

    public void setNetworkType(NetworkType networkType) {
        this.networkType = networkType;
    }

    public static class Builder {

        private NetworkType networkType = NetworkType.ANY;

        public NetworkType getNetworkType() {
            return networkType;
        }

        public Builder networkType(NetworkType networkType) {
            this.networkType = networkType;
            return this;
        }

        public Constraints build() {
            return new Constraints(this);
        }
    }
}
