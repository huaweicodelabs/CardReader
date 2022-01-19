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

package com.huawei.cardreader.kotlin.generalcardscan.processor.gcr.businesscard

import android.content.Context
import android.widget.Toast
import com.huawei.cardreader.R
import com.huawei.cardreader.kotlin.utils.Constants
import com.huawei.hms.mlsdk.text.MLText
import java.util.*

/**
 * The type Generic processing.
 */
class GenericProcessing {
    private var phoneIndex = 0

    /**
     * Process text hash map.
     *
     * @param text    the text
     * @param context the context
     * @return the hash map
     */
    fun processText(
        text: MLText,
        context: Context
    ): HashMap<String, String?>? {
        val blocks = text.blocks
        if (blocks.size == 0) {
            Toast.makeText(context, R.string.NO_TEXT, Toast.LENGTH_LONG).show()
            return null
        }
        val textBuilder = StringBuilder()
        val map =
            TreeMap<String, String>()
        for (block in text.blocks) {
            for (line in block.contents) {
                val rect = line.border
                val y = rect.exactCenterY().toString()
                val lineTxt = line.stringValue
                map[y] = lineTxt
            }
        }
        val orderedData: List<String> =
            ArrayList(map.values)
        val dataMap =
            HashMap<String, String?>()
        var i: Int
        //Phone Number
        i = 0
        while (i < orderedData.size) {
            if (orderedData[i].matches(Constants.REGEX.toRegex())) {
                dataMap[Constants.PHONENUMBER] = orderedData[i]
                phoneIndex = i
            }
            i++
        }
        //Card Holder Name
        if (phoneIndex >= 0) {
            i = 0
            while (i < orderedData.size) {
                if (i == phoneIndex - 1) {
                    dataMap[Constants.NAME] = orderedData[i].toUpperCase()
                }
                i++
            }
        }
        //Getting Email address
        i = 0
        while (i < orderedData.size) {
            if (orderedData[i].matches(Constants.EMAILREGEX.toRegex())) {
                dataMap[Constants.EMAIL] = orderedData[i]
            }
            i++
        }
        if (i - 1 > -1 && !orderedData[i - 1]
                .contains(Constants.SOLUTIONS)
        ) dataMap[Constants.ORG] = orderedData[i - 1]
        for ((_, value) in map) {
            textBuilder.append(value).append(
                context.getString(R.string.slashn)
                        + context.getString(R.string.slashn)
            )
        }
        dataMap[Constants.TEXT] = textBuilder.toString()
        return dataMap
    }
}