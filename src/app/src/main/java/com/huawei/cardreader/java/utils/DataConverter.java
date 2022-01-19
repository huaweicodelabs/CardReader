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

package com.huawei.cardreader.java.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import androidx.room.TypeConverter;

import java.io.ByteArrayOutputStream;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * The type Data converter.
 */
public class DataConverter {
    /**
     * Bit map to string string.
     *
     * @param bitmap the bitmap
     * @return the string
     */
    @TypeConverter
    public static String bitMapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        String temp = null;
        try {
            temp = Base64.encodeToString(b, Base64.DEFAULT);
        } catch (NullPointerException e) {
            throw e;
        } catch (OutOfMemoryError e) {
            baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
            b = baos.toByteArray();
            temp = Base64.encodeToString(b, Base64.DEFAULT);
        }
        return temp;
    }

    /**
     * String to bit map bitmap.
     *
     * @param encodedString the encoded string
     * @return the bitmap
     */
    @TypeConverter
    public static Bitmap stringToBitMap(String encodedString) {
        byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
    }

    public static String enCodeString(String forEncode) {
        byte[] encodeValue = Base64.encode(forEncode.getBytes(UTF_8), Base64.DEFAULT);
        return new String(encodeValue, UTF_8);
    }
    public static String deCodeString(String forDecode) {
        byte[] decodeValue = Base64.decode(String.valueOf(forDecode), Base64.DEFAULT);
        return new String(decodeValue, UTF_8);
    }
}
