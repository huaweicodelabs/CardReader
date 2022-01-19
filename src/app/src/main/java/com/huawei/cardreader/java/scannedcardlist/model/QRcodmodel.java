/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.huawei.cardreader.java.scannedcardlist.model;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import com.huawei.cardreader.java.scannedcardlist.roomdb.BusinessCardEntity;

import java.util.List;

/**
 * The type Q rcodmodel.
 */
public class QRcodmodel implements Parcelable {
    private Bitmap bitmap;
    private List<BusinessCardEntity> businesscardUserDetailsList;

    /**
     * Instantiates a new Q rcodmodel.
     */
    public QRcodmodel() {
    }


    /**
     * Gets bitmap.
     *
     * @return the bitmap
     */
    public Bitmap getBitmap() {
        return bitmap;
    }

    /**
     * Sets bitmap.
     *
     * @param bitmap the bitmap
     */
    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    /**
     * Gets businesscard user details list.
     *
     * @return the businesscard user details list
     */
    public List<BusinessCardEntity> getBusinesscardUserDetailsList() {
        return businesscardUserDetailsList;
    }

    /**
     * Sets businesscard user details list.
     *
     * @param businesscardUserDetailsList the businesscard user details list
     */
    public void setBusinesscardUserDetailsList(List<BusinessCardEntity> businesscardUserDetailsList) {
        this.businesscardUserDetailsList = businesscardUserDetailsList;
    }

    /**
     * Instantiates a new Q rcodmodel.
     *
     * @param in the in
     */
    protected QRcodmodel(Parcel in) {
        bitmap = in.readParcelable(Bitmap.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(bitmap, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }


    /**
     * The constant CREATOR.
     */
    public static final Creator<QRcodmodel> CREATOR = new Creator<QRcodmodel>() {
        @Override
        public QRcodmodel createFromParcel(Parcel in) {
            return new QRcodmodel(in);
        }

        @Override
        public QRcodmodel[] newArray(int size) {
            return new QRcodmodel[size];
        }
    };

}
