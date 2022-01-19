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

package com.huawei.cardreader.java.scannedcardlist.roomdb;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.room.Room;

import com.huawei.cardreader.R;

/**
 * The type Database client.
 */
public class DatabaseClient {
    @SuppressLint("StaticFieldLeak")
    private static DatabaseClient sInstance;
    private AppDatabase mAppDatabase;

    // creating the app database with Room database builder
    private DatabaseClient(Context mContext) {
        mAppDatabase = Room.databaseBuilder(mContext, AppDatabase.class, mContext.getString(R.string.MyToDos)).build();
    }

    /**
     * Gets instance.
     *
     * @param mCtx the m ctx
     * @return the instance
     */
    public static synchronized DatabaseClient getInstance(Context mCtx) {
        if (sInstance == null) {
            sInstance = new DatabaseClient(mCtx);
        }
        return sInstance;
    }

    /**
     * Gets app database.
     *
     * @return the app database
     */
    public AppDatabase getAppDatabase() {
        return mAppDatabase;
    }
}