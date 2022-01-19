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

package com.huawei.cardreader.kotlin.scannedcardlist.roomdb

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/**
 * The interface Business card entity dao.
 */
@Dao
interface BusinessCardEntityDao {
    /**
     * Gets all.
     *
     * @return the all
     */
    @get:Query("SELECT * FROM businesscardentity")
    val all: List<BusinessCardEntity?>?

    /**
     * Insert.
     *
     * @param task the task
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(task: BusinessCardEntity?)

    /**
     * Get list.
     *
     * @param idnum the idnum
     * @return the list
     */
    @Query("SELECT * FROM businesscardentity where id = :idnum")
    operator fun get(idnum: Int): List<BusinessCardEntity?>?

    /**
     * Delete by id.
     *
     * @param id the id
     */
    @Query("DELETE FROM businesscardentity where id = :id")
    fun deleteById(id: Int)

    /**
     * Update.
     *
     * @param contactname the contactname
     * @param contactno   the contactno
     * @param email       the email
     * @param idnum       the idnum
     */
    @Query("UPDATE businesscardentity SET name = :contactname, mobile_no= :contactno,emailid= :email WHERE id =:idnum")
    fun update(
        contactname: String?,
        contactno: String?,
        email: String?,
        idnum: Int
    )
}