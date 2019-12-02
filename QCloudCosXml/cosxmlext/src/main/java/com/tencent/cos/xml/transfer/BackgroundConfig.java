package com.tencent.cos.xml.transfer;

import android.os.Parcel;
import android.os.Parcelable;

import com.tencent.cos.xml.transfer.constraint.Constraints;

/**
 * Created by rickenwang on 2019-11-25.
 * <p>
 * Copyright (c) 2010-2020 Tencent Cloud. All rights reserved.
 */
public class BackgroundConfig extends TransferConfig implements Parcelable {

    private Constraints constraints;

    private BackgroundConfig(Builder builder) {
        super(builder);
        this.constraints = builder.constraints;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeLong(divisionForCopy);
        dest.writeLong(sliceSizeForCopy);
        dest.writeLong(divisionForUpload);
        dest.writeLong(sliceSizeForUpload);
    }

    private BackgroundConfig(Parcel in) {

        this((Builder) new Builder()
                .setDividsionForCopy(in.readLong())
                .setSliceSizeForCopy(in.readLong())
                .setDivisionForUpload(in.readLong())
                .setSliceSizeForUpload(in.readLong()));

    }

    public static final Parcelable.Creator<BackgroundConfig> CREATOR
            = new Parcelable.Creator<BackgroundConfig>() {
        public BackgroundConfig createFromParcel(Parcel in) {
            return new BackgroundConfig(in);
        }

        public BackgroundConfig[] newArray(int size) {
            return new BackgroundConfig[size];
        }
    };

    public Constraints getConstraints() {
        return constraints;
    }

    public static class Builder extends TransferConfig.Builder {

        private Constraints constraints = Constraints.NONE;

        public Builder() {}

        public Builder setConstrains(Constraints constrains) {
            this.constraints = constrains;
            return this;
        }

        @Override
        public BackgroundConfig build() {
            return new BackgroundConfig(this);
        }
    }


}
