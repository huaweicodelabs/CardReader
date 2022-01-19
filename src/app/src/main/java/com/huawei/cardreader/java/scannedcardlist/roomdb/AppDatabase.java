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

import androidx.room.Database;
import androidx.room.RoomDatabase;

/**
 * The type App database.
 */
@Database(entities = {BusinessCardEntity.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    /**
     * Business card entity dao business card entity dao.
     *
     * @return the business card entity dao
     */
    public abstract BusinessCardEntityDao businessCardEntityDao();
}