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

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.AttributeSet;

/**
 * The type BlurredImageView.
 */
public class BlurredImageView extends androidx.appcompat.widget.AppCompatImageView {
    private static final int BLUR_RADIUS = 25;

    /**
     * Instantiates a new Blurred image view.
     *
     * @param context the context
     */
    public BlurredImageView(Context context) {
        super(context);
    }

    /**
     * Instantiates a new Blurred image view.
     *
     * @param context the context
     * @param attrs   the attrs
     */
    public BlurredImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Instantiates a new Blurred image view.
     *
     * @param context  the context
     * @param attrs    the attrs
     * @param defStyle the def style
     */
    public BlurredImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setImageResource(int resId) {
        setBlurredImage(resId);
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        setBlurredImage(bm);
    }

    /**
     * Sets blurred image.
     *
     * @param resId the res id
     */
    public void setBlurredImage(int resId) {
        setBlurredImage(BitmapFactory.decodeResource(getResources(), resId));
    }

    /**
     * Sets blurred image.
     *
     * @param bitmap the bitmap
     */
    public void setBlurredImage(Bitmap bitmap) {
        Bitmap blurred = bitmap;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            blurred = blurBitmapRenderscript(bitmap);
        }
        super.setImageBitmap(blurred);
    }

    /**
     * Rednereing  blurred image.
     *
     * @param bitmap the bitmap
     * @return Bitmap
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private Bitmap blurBitmapRenderscript(Bitmap bitmap) {
        Bitmap outBitmap =
                Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        RenderScript rs = RenderScript.create(getContext().getApplicationContext());
        ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        Allocation allIn = Allocation.createFromBitmap(rs, bitmap);
        Allocation allOut = Allocation.createFromBitmap(rs, outBitmap);
        blurScript.setRadius(BLUR_RADIUS);
        blurScript.setInput(allIn);
        blurScript.forEach(allOut);
        allOut.copyTo(outBitmap);
        bitmap.recycle();
        rs.destroy();
        return outBitmap;
    }
}