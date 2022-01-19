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

package com.huawei.cardreader.kotlin.splashscreen.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat.OnRequestPermissionsResultCallback
import com.huawei.cardreader.kotlin.BaseActivity
import com.huawei.cardreader.R
import com.huawei.cardreader.kotlin.dashboard.activity.DashboardActivity
import com.huawei.cardreader.kotlin.useragreement.activity.AgreementPageView

/**
 * The type Splash screen activity.
 */
class SplashScreenActivity : BaseActivity(), OnRequestPermissionsResultCallback,
    View.OnClickListener {
    private lateinit var sharedPreferences: SharedPreferences
    private var firstTime: Boolean? = null

    @RequiresApi(api = Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        sharedPreferences = getSharedPreferences(
            getString(R.string.splash),
            Context.MODE_PRIVATE
        )
        firstTime = sharedPreferences.getBoolean(getString(R.string.firstTime), true)
        if (firstTime as Boolean) {
            Handler().postDelayed({
                val editor = sharedPreferences.edit()
                firstTime = false
                editor.putBoolean(getString(R.string.firstTime), firstTime!!)
                editor.apply()
                val i = Intent(this@SplashScreenActivity,
                    AgreementPageView::class.java)
                startActivity(i)
                finish()
            }, 2000)
        } else {
            Handler().postDelayed({

                // This method will be executed once the timer is over
                val i = Intent(this@SplashScreenActivity,
                    DashboardActivity::class.java)
                startActivity(i)
                finish()
            }, 2000)
        }
    }

    override fun onClick(view: View) {}
}