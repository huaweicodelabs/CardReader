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

package com.huawei.cardreader.java.generalcardscan.processor.gcr.aadharcard;

import android.content.Context;
import android.graphics.Rect;
import android.widget.Toast;

import com.huawei.cardreader.R;
import com.huawei.cardreader.java.utils.Constants;
import com.huawei.hms.common.util.Logger;
import com.huawei.hms.mlsdk.text.MLText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TreeMap;

/**
 * The type Aadhaar processing.
 */
public class AadhaarProcessing {
    /**
     * Process extracted text for front pic hash map.
     *
     * @param text    the text
     * @param context the context
     * @return the hash map
     */
    public HashMap<String, String> processExtractedTextForFrontPic(MLText text, Context context) {
        List<MLText.Block> blocks = text.getBlocks();
        if (blocks.size() == 0) {
            Toast.makeText(context, R.string.NO_TEXT, Toast.LENGTH_LONG).show();
            return null;
        }
        TreeMap<String, String> map = new TreeMap<>();
        for (MLText.Block block : text.getBlocks()) {
            for (MLText.TextLine line : block.getContents()) {
                Rect rect = line.getBorder();
                String y = String.valueOf(rect.exactCenterY());
                String lineTxt = line.getStringValue();
                map.put(y, lineTxt);
            }
        }
        List<String> orderedData = new ArrayList<>(map.values());
        HashMap<String, String> dataMap = new HashMap<>();

        // getting Aadhar Number
        int i;
        for (i = 0; i < orderedData.size(); i++) {
            if (orderedData.get(i).matches(context.getString(R.string.aadharRegex))) {
                dataMap.put(context.getString(R.string.aadharcard_processing), orderedData.get(i));
                break;
            }
        }
        // setting gender first
        for (i = 0; i < orderedData.size(); i++) {
            if (orderedData.get(i).contains(Constants.FEMALE)) {
                dataMap.put(Constants.GENDER, Constants.FEMALE);
                break;
            } else if (orderedData.get(i).contains(Constants.MALE)) {
                dataMap.put(Constants.GENDER, Constants.MALE);
                break;
            }
        }
        if (!dataMap.containsKey(Constants.AADHAR)) {
            if (i + 1 < orderedData.size()) {
                dataMap.put(Constants.AADHAR, orderedData.get(i + 1));
            }
        }
        // searching for father name
        for (i = 0; i < orderedData.size(); i++) {
            if (orderedData.get(i).contains(Constants.FATHER)) {
                dataMap.put(Constants.FATHERNAME, orderedData.get(i).replace
                        (Constants.FATHER, "").replace(":", ""));
                break;
            }
        }
        if (dataMap.containsKey(Constants.FATHERNAME)) {
            if (i - 2 > -1) {
                dataMap.put(Constants.NAME, orderedData.get(i - 2));
            }
        }
        // Getting Year Of Birth
        for (i = 0; i < orderedData.size(); i++) {
            try {
                if (orderedData.get(i).contains(context.getString(R.string.aadharcard_slash))) {
                    dataMap.put(Constants.YEAROFBIRTH, orderedData.get(i).substring
                            (orderedData.get(i).length() - 11));
                    break;
                }
            }catch (StringIndexOutOfBoundsException e){
                Logger.e("StringIndexOutOfBoundsException", e);
            }

        }
        // Getting Card Holder Name Based on Father Name
        if (i - 1 > -1 && !orderedData.get(i - 1).contains(Constants.FATHER)) {
            dataMap.put(Constants.NAME, orderedData.get(i - 1).toUpperCase(Locale.ROOT));
        }
        return dataMap;
    }
}
