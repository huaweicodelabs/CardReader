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

package com.huawei.cardreader.kotlin.useragreement.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.huawei.cardreader.R
import com.huawei.cardreader.kotlin.dashboard.activity.DashboardActivity
import com.huawei.cardreader.kotlin.utils.BioAuthUtils.checkAuthenticationType
import com.huawei.cardreader.kotlin.utils.BioAuthUtils.isDeviceAuthenticated
import com.huawei.cardreader.kotlin.utils.BioAuthUtils.setIsFaceRecognitionSecure
import com.huawei.cardreader.kotlin.utils.BioAuthUtils.setIsFingerPrintSecure
import com.huawei.cardreader.kotlin.utils.BioAuthUtils.setIsKeyguardSecure
import com.huawei.cardreader.kotlin.utils.BioAuthUtils.showGuide

/**
 * The type Agreement page view.
 */
class AgreementPageView : AppCompatActivity() {
    /**
     * The Is agree checked.
     */
    var isAgreeChecked = false
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agreement_page_view)
        this.title = getString(R.string.Agree_Page_Title)
        init()
    }

    private fun init() {
        checkAuthenticationType(this)
        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener { view: View? ->
            if (isAgreeChecked && isDeviceAuthenticated) {
                val i = Intent(this@AgreementPageView, DashboardActivity::class.java)
                startActivity(i)
                finish()
            } else if (!isAgreeChecked) {
                showGuide(view, R.string.AGREE_TO_PROCEED)
            } else {
                showGuide(view, R.string.AUTH_SUGGESTION_MSG)
            }
        }
    }

    /**
     * On agree check click.
     *
     * @param view the view
     */
    fun onAgreeCheckClick(view: View) {
        val checked = (view as CheckBox).isChecked
        if (view.getId() == R.id.AgreeCheckbox) {
            if (checked) {
                isAgreeChecked = true
            } else {
                isAgreeChecked = false
                setIsKeyguardSecure(false)
                setIsFingerPrintSecure(false)
                setIsFaceRecognitionSecure(false)
            }
        }
    }
}