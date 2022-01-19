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

package com.huawei.cardreader.kotlin.generalcardscan.processor.gcr.pancard

import android.content.Context
import android.widget.Toast
import com.huawei.cardreader.R
import com.huawei.cardreader.kotlin.utils.Constants
import com.huawei.hms.mlsdk.text.MLText
import java.util.*

/**
 * The type Pan processing.
 */
class PanProcessing {
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
        val dataMap =
            HashMap<String, String?>()
        val orderedData: List<String> =
            ArrayList(map.values)
        var i: Int
        //Getting Date Of Birth
        i = 0
        while (i < orderedData.size) {
            if (orderedData[i].contains(context.getString(R.string.pancardprocessing_slash))) {
                dataMap[Constants.DATEOFBIRTH] = orderedData[i]
                break
            }
            i++
        }
        //Getting Father Name
        if (i - 1 > -1) dataMap[Constants.FATHERNAME] = orderedData[i - 1]
        //Getting Pan Card Holder Name
        if (i - 2 > -1) dataMap[Constants.NAME] = orderedData[i - 2]
        //Getting Pan Card Number
        i = 0
        while (i < orderedData.size) {
            if (orderedData.get(i).matches("\\w\\w\\w\\w\\w\\d\\d\\d\\d.*".toRegex())) {
                dataMap[Constants.PANNUMBER] = orderedData[i]
                break
            }
            i++
        }
        return dataMap
    }
}