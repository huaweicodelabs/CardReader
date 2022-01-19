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

import android.annotation.TargetApi
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView

/**
 * The type BlurredImageView.
 */
class BlurredImageView : AppCompatImageView {
    /**
     * Instantiates a new Blurred image view.
     *
     * @param context the context
     */
    constructor(context: Context?) : super(context!!) {}

    /**
     * Instantiates a new Blurred image view.
     *
     * @param context the context
     * @param attrs   the attrs
     */
    constructor(context: Context?, attrs: AttributeSet?) : super(
        context!!,
        attrs
    ) {
    }

    /**
     * Instantiates a new Blurred image view.
     *
     * @param context  the context
     * @param attrs    the attrs
     * @param defStyle the def style
     */
    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyle: Int
    ) : super(context!!, attrs, defStyle) {
    }

    override fun setImageResource(resId: Int) {
        setBlurredImage(resId)
    }

    override fun setImageBitmap(bm: Bitmap) {
        setBlurredImage(bm)
    }

    /**
     * Sets blurred image.
     *
     * @param resId the res id
     */
    fun setBlurredImage(resId: Int) {
        setBlurredImage(BitmapFactory.decodeResource(resources, resId))
    }

    /**
     * Sets blurred image.
     *
     * @param bitmap the bitmap
     */
    fun setBlurredImage(bitmap: Bitmap) {
        var blurred = bitmap
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            blurred = blurBitmapRenderscript(bitmap)
        }
        super.setImageBitmap(blurred)
    }

    /**
     * Rednereing  blurred image.
     *
     * @param bitmap the bitmap
     * @return Bitmap
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private fun blurBitmapRenderscript(bitmap: Bitmap): Bitmap {
        val outBitmap =
            Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        val rs = RenderScript.create(context.applicationContext)
        val blurScript =
            ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))
        val allIn = Allocation.createFromBitmap(rs, bitmap)
        val allOut = Allocation.createFromBitmap(rs, outBitmap)
        blurScript.setRadius(BLUR_RADIUS.toFloat())
        blurScript.setInput(allIn)
        blurScript.forEach(allOut)
        allOut.copyTo(outBitmap)
        bitmap.recycle()
        rs.destroy()
        return outBitmap
    }

    companion object {
        private const val BLUR_RADIUS = 25
    }
}