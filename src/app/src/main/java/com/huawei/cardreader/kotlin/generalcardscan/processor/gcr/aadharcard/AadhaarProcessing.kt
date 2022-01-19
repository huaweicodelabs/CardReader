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

package com.huawei.cardreader.kotlin.generalcardscan.processor.gcr.aadharcard

import android.content.Context
import android.widget.Toast
import com.huawei.cardreader.R
import com.huawei.cardreader.kotlin.utils.Constants
import com.huawei.hms.mlsdk.text.MLText
import java.util.*

/**
 * The type Aadhaar processing.
 */
class AadhaarProcessing {
    /**
     * Process extracted text for front pic hash map.
     *
     * @param text    the text
     * @param context the context
     * @return the hash map
     */
    fun processExtractedTextForFrontPic(
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
        val orderedData: List<String> =
            ArrayList(map.values)
        val dataMap =
            HashMap<String, String?>()

        //getting Aadhar Number
        var i: Int
        i = 0
        while (i < orderedData.size) {
            if (orderedData[i].matches(context.getString(R.string.aadharRegex).toRegex())) {
                dataMap[context.getString(R.string.aadharcard_processing)] = orderedData[i]
                break
            }
            i++
        }
        //setting gender first
        i = 0
        while (i < orderedData.size) {
            if (orderedData[i].contains(Constants.FEMALE)) {
                dataMap[Constants.GENDER] = Constants.FEMALE
                break
            } else if (orderedData[i].contains(Constants.MALE)) {
                dataMap[Constants.GENDER] = Constants.MALE
                break
            }
            i++
        }
        if (!dataMap.containsKey(Constants.AADHAR)) {
            if (i + 1 < orderedData.size) {
                dataMap[Constants.AADHAR] = orderedData[i + 1]
            }
        }
        //searching for father name
        i = 0
        while (i < orderedData.size) {
            if (orderedData[i].contains(Constants.FATHER)) {
                dataMap[Constants.FATHERNAME] = orderedData[i]
                    .replace(Constants.FATHER, "")
                    .replace(":", "")
                break
            }
            i++
        }
        if (dataMap.containsKey(Constants.FATHERNAME)) {
            if (i - 2 > -1) {
                dataMap[Constants.NAME] = orderedData[i - 2]
            }
        }
        //Getting Year Of Birth
        i = 0
        while (i < orderedData.size) {
            try {
                if (orderedData[i].contains(context.getString(R.string.aadharcard_slash))) {
                    dataMap[Constants.YEAROFBIRTH] =
                        orderedData[i].substring(orderedData[i].length - 11)
                    break
                }
            } catch (e: StringIndexOutOfBoundsException) {
            }
            i++
        }
        //Getting Card Holder Name Based on Father Name
        if (i - 1 > -1 && !orderedData[i - 1]
                .contains(Constants.FATHER)
        ) {
            dataMap[Constants.NAME] = orderedData[i - 1].toUpperCase()
        }
        return dataMap
    }
}