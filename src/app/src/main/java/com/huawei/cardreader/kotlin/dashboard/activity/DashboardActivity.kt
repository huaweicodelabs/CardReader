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

package com.huawei.cardreader.kotlin.dashboard.activity

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.OnRequestPermissionsResultCallback
import androidx.core.content.ContextCompat
import com.huawei.cardreader.R
import com.huawei.cardreader.kotlin.scannedcardlist.activity.ScannedCardListActivity
import com.huawei.cardreader.kotlin.settingspage.activity.SettingsActivity
import com.huawei.cardreader.kotlin.utils.Constants
import java.util.*

/**
 * The type Dashboard activity.
 */
class DashboardActivity : AppCompatActivity(), OnRequestPermissionsResultCallback,
    View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dash_board)
        if (!allPermissionsGranted()) {
            runtimePermissions
        }
        val indianIDLayout = findViewById<LinearLayout>(R.id.IDcardsView)
        val bankCardLayout = findViewById<LinearLayout>(R.id.BankCardsView)
        val settings = findViewById<LinearLayout>(R.id.Text)
        indianIDLayout.setOnClickListener { view: View? ->
            val intent =
                Intent(applicationContext, ScannedCardListActivity::class.java)
            intent.putExtra(
                Constants.CARD_TYPE_TO_SAVE,
                Constants.GENERALCARDS
            )
            intent.putExtra(
                Constants.SCREENTYPE,
                Constants.GENERALCARDS
            )
            intent.putExtra(
                Constants.SCANNEDCARDNUMBER,
                Constants.SAMPLNUMBER
            )
            startActivity(intent)
        }
        bankCardLayout.setOnClickListener { view: View? ->
            val intent =
                Intent(applicationContext, ScannedCardListActivity::class.java)
            intent.putExtra(
                Constants.CARD_TYPE_TO_SAVE,
                Constants.BANKCARD
            )
            intent.putExtra(
                Constants.SCREENTYPE,
                Constants.BANKCARD
            )
            intent.putExtra(
                Constants.SCANNEDCARDNUMBER,
                Constants.SAMPLNUMBER
            )
            startActivity(intent)
        }
        settings.setOnClickListener { view: View? ->
            val i = Intent(this@DashboardActivity, SettingsActivity::class.java)
            startActivity(i)
        }
    }

    private val requiredPermissions: Array<String?>
        private get() = try {
            var info: PackageInfo? = null
            try {
                info = this.packageManager
                    .getPackageInfo(this.packageName, PackageManager.GET_PERMISSIONS)
            } catch (e: PackageManager.NameNotFoundException) {

            }
            val ps = info!!.requestedPermissions
            if (ps != null && ps.size > 0) {
                ps
            } else {
                arrayOfNulls(0)
            }
        } catch (e: RuntimeException) {
            throw e
        }

    private fun allPermissionsGranted(): Boolean {
        for (permission in requiredPermissions) {
            if (!isPermissionGranted(this, permission)) {
                return false
            }
        }
        return true
    }

    private val runtimePermissions: Unit
        private get() {
            val allNeededPermissions: MutableList<String?> =
                ArrayList()
            for (permission in requiredPermissions) {
                if (!isPermissionGranted(this, permission)) {
                    allNeededPermissions.add(permission)
                }
            }
            if (!allNeededPermissions.isEmpty()) {
                ActivityCompat.requestPermissions(
                    this, allNeededPermissions.toTypedArray(),
                    PERMISSION_REQUESTS
                )
            }
        }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode != PERMISSION_REQUESTS) {
            return
        }
        var isNeedShowDiag = false
        for (i in permissions.indices) {
            if ((permissions[i] == Manifest.permission.CAMERA && grantResults[i]
                        != PackageManager.PERMISSION_GRANTED)
                || permissions[i] == Manifest.permission.READ_EXTERNAL_STORAGE &&
                grantResults[i] != PackageManager.PERMISSION_GRANTED
                || (permissions[i] == Manifest.permission.RECORD_AUDIO && grantResults[i]
                        != PackageManager.PERMISSION_GRANTED)
            ) {
                isNeedShowDiag = true
            }
        }
        if (isNeedShowDiag && !ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.CALL_PHONE
            )
        ) {
            val dialog =
                AlertDialog.Builder(this)
                    .setMessage(this.getString(R.string.camera_permission_rationale))
                    .setPositiveButton(
                        this.getString(R.string.settings)
                    ) { dialog12: DialogInterface?, which: Int ->
                        val intent =
                            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        intent.data = Uri.parse(
                            getString(R.string.intentPackage) +
                                    this@DashboardActivity.packageName
                        )
                        this@DashboardActivity.startActivityForResult(intent, 200)
                        this@DashboardActivity.startActivity(intent)
                    }
                    .setNegativeButton(
                        this.getString(R.string.cancel)
                    ) { dialog1: DialogInterface?, which: Int -> finish() }
                    .create()
            dialog.show()
        }
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 200) {
            if (!allPermissionsGranted()) {
                runtimePermissions
            }
        }
    }

    override fun onClick(view: View) {}

    companion object {
        private const val PERMISSION_REQUESTS = 1
        private fun isPermissionGranted(
            context: Context,
            permission: String?
        ): Boolean {
            return if (ContextCompat.checkSelfPermission(context, permission!!)
                == PackageManager.PERMISSION_GRANTED
            ) {
                true
            } else false
        }
    }
}