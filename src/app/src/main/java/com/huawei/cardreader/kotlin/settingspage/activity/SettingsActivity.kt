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

package com.huawei.cardreader.kotlin.settingspage.activity

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import com.huawei.cardreader.kotlin.BaseActivity
import com.huawei.cardreader.R
import com.huawei.cardreader.kotlin.utils.Constants
import com.huawei.hms.common.util.Logger
import java.util.*

/**
 * The type Settings activity.
 */
class SettingsActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.settings_activity)
        Objects.requireNonNull(supportActionBar)?.setDisplayHomeAsUpEnabled(true)
        val mVersion = findViewById<TextView>(R.id.version)
        mVersion.text = versionName
        val changePwd = findViewById<TextView>(R.id.ChangePWD)
        changePwd.setOnClickListener { view: View? ->
            startActivityForResult(
                Intent(Settings.ACTION_SECURITY_SETTINGS),
                0
            )
        }
    }

    /**
     * get App versionName
     *
     * @return version name
     */
    val versionName: String
        get() {
            val packageManager = this.packageManager
            try {
                val packageInfo = packageManager.getPackageInfo(this.packageName, 0)
                return packageInfo.versionName
            } catch (e: PackageManager.NameNotFoundException) {
                Logger.e("NameNotFoundException", e.toString())
            }
            return Constants.DEFAULT_VERSION
        }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}