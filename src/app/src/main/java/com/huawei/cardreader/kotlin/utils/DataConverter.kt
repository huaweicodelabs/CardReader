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

package com.huawei.cardreader.kotlin.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.room.TypeConverter
import java.io.ByteArrayOutputStream

/**
 * The type Data converter.
 */
object DataConverter {
    /**
     * Bit map to string string.
     *
     * @param bitmap the bitmap
     * @return the string
     */
    @TypeConverter
    fun bitMapToString(bitmap: Bitmap): String? {
        var baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        var b = baos.toByteArray()
        var temp: String? = null
        try {
            temp = Base64.encodeToString(b, Base64.DEFAULT)
        } catch (e: NullPointerException) {
        } catch (e: OutOfMemoryError) {
            baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos)
            b = baos.toByteArray()
            temp = Base64.encodeToString(b, Base64.DEFAULT)
        }
        return temp
    }

    /**
     * String to bit map bitmap.
     *
     * @param encodedString the encoded string
     * @return the bitmap
     */
    @TypeConverter
    fun stringToBitMap(encodedString: String?): Bitmap {
        val encodeByte =
            Base64.decode(encodedString, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.size)
    }

    fun enCodeString(forEncode: String): String {
        val encodeValue =
            Base64.encode(forEncode.toByteArray(), Base64.DEFAULT)
        return String(encodeValue)
    }

    fun deCodeString(forDecode: String): String {
        val decodeValue =
            Base64.decode(forDecode, Base64.DEFAULT)
        return String(decodeValue)
    }
}