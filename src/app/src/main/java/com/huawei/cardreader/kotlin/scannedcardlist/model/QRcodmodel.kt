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

package com.huawei.cardreader.kotlin.scannedcardlist.model

import android.graphics.Bitmap
import android.os.Parcel
import android.os.Parcelable
import com.huawei.cardreader.kotlin.scannedcardlist.roomdb.BusinessCardEntity

/**
 * The type Q rcodmodel.
 */
class QRcodmodel : Parcelable {
    /**
     * Gets bitmap.
     *
     * @return the bitmap
     */
    /**
     * Sets bitmap.
     *
     * @param bitmap the bitmap
     */
    var bitmap: Bitmap? = null
    /**
     * Gets businesscard user details list.
     *
     * @return the businesscard user details list
     */
    /**
     * Sets businesscard user details list.
     *
     * @param businesscardUserDetailsList the businesscard user details list
     */
    var businesscardUserDetailsList: List<BusinessCardEntity>? = null

    /**
     * Instantiates a new Q rcodmodel.
     */
    constructor() {}

    /**
     * Instantiates a new Q rcodmodel.
     *
     * @param in the in
     */
    protected constructor(`in`: Parcel) {
        bitmap = `in`.readParcelable(Bitmap::class.java.classLoader)
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeParcelable(bitmap, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object {
        /**
         * The constant CREATOR.
         */
        val CREATOR: Parcelable.Creator<QRcodmodel?> = object : Parcelable.Creator<QRcodmodel?> {
            override fun createFromParcel(`in`: Parcel): QRcodmodel? {
                return QRcodmodel(`in`)
            }

            override fun newArray(size: Int): Array<QRcodmodel?> {
                return arrayOfNulls(size)
            }
        }
    }
}