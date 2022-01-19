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

package com.huawei.cardreader.java.generalcardscan.processor.gcr.businesscard;

import android.content.Context;
import android.graphics.Rect;
import android.widget.Toast;

import com.huawei.cardreader.R;
import com.huawei.cardreader.java.utils.Constants;
import com.huawei.hms.mlsdk.text.MLText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TreeMap;

/**
 * The type Generic processing.
 */
public class GenericProcessing {
    private int phoneIndex;

    /**
     * Process text hash map.
     *
     * @param text    the text
     * @param context the context
     * @return the hash map
     */
    public HashMap<String, String> processText(MLText text, Context context) {
        List<MLText.Block> blocks = text.getBlocks();
        if (blocks.size() == 0) {
            Toast.makeText(context, R.string.NO_TEXT, Toast.LENGTH_LONG).show();
            return null;
        }
        StringBuilder textBuilder = new StringBuilder();
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
        int i;
        // Phone Number
        for (i = 0; i < orderedData.size(); i++) {
            if (orderedData.get(i).matches(Constants.REGEX)) {
                dataMap.put(Constants.PHONENUMBER, orderedData.get(i));
                phoneIndex = i;
            }
        }
        // Card Holder Name
        if (phoneIndex >= 0) {
            for (i = 0; i < orderedData.size(); i++) {
                if (i == (phoneIndex - 1)) {
                    dataMap.put(Constants.NAME, orderedData.get(i).toUpperCase(Locale.ROOT));
                }
            }
        }
        // Getting Email address
        for (i = 0; i < orderedData.size(); i++) {
            if (orderedData.get(i).matches(Constants.EMAILREGEX)) {
                dataMap.put(Constants.EMAIL, orderedData.get(i));
            }
        }
        if ((i-1)> -1 && !orderedData.get(i-1).contains(Constants.SOLUTIONS)) {
            dataMap.put(Constants.ORG, orderedData.get(i - 1));
        }
        for (TreeMap.Entry<String, String> entry : map.entrySet()) {
            textBuilder.append(entry.getValue()).append(context.getString(R.string.slashn)
                    + context.getString(R.string.slashn));
        }
        dataMap.put(Constants.TEXT, textBuilder.toString());
        return dataMap;
    }
}
