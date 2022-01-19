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

package com.huawei.cardreader.kotlin.userauthentication.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.AsyncTask
import android.os.Bundle
import android.os.CancellationSignal
import android.provider.Settings
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.huawei.cardreader.R
import com.huawei.cardreader.kotlin.scannedcarddetails.activity.ScannedCardDetailsActivity
import com.huawei.cardreader.kotlin.scannedcardlist.roomdb.DatabaseClient.Companion.getInstance
import com.huawei.cardreader.kotlin.userauthentication.activity.AuthenticationPage
import com.huawei.cardreader.kotlin.utils.BioAuthUtils.checkAuthenticationType
import com.huawei.cardreader.kotlin.utils.BioAuthUtils.isFaceRecognitionSecure
import com.huawei.cardreader.kotlin.utils.BioAuthUtils.isFingerPrintSecure
import com.huawei.cardreader.kotlin.utils.BioAuthUtils.isKeyguardSecure
import com.huawei.cardreader.kotlin.utils.BioAuthUtils.showToast
import com.huawei.cardreader.kotlin.utils.Constants
import com.huawei.hms.support.api.fido.bioauthn.*
import java.util.*

/**
 * The type Authentication page.
 */
class AuthenticationPage : AppCompatActivity(), View.OnClickListener {
    private var isAuthenticationClicked = false
    private var bioAuthnPrompt: BioAuthnPrompt? = null
    private var qrCodeImage: Bitmap? = null
    private var id = 0
    private var qrCodeCardType: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authentication_page)
        this.title = getString(R.string.AUTHENTICATION_PAGE_TITLE)
        Objects.requireNonNull(supportActionBar)?.setDisplayHomeAsUpEnabled(true)
        initialize()
    }

    private fun initialize() {
        val lockImage =
            findViewById<ImageView>(R.id.LockImage)
        lockImage.setOnClickListener(this)
        val fingerPrintImage =
            findViewById<ImageView>(R.id.FingerprintImage)
        fingerPrintImage.setOnClickListener(this)
        val faceRecognizeImage =
            findViewById<ImageView>(R.id.FacerecignitionImage)
        faceRecognizeImage.setOnClickListener(this)
        bioAuthnPrompt = createBioAuthnPrompt()
        qrCodeImage =
            intent.getParcelableExtra(Constants.QRCODEIMAGE)
        id = intent.getIntExtra(Constants.ID, 0)
        qrCodeCardType=intent.getStringExtra(Constants.QRCODECARDTYPE)
    }

    override fun onStop() {
        super.onStop()
        if (!isAuthenticationClicked) {
            Constants.IS_DELETED_FROM_PROFILEDETAILS = false
        }
    }

    override fun onResume() {
        super.onResume()
        isAuthenticationClicked = false
    }

    override fun onClick(view: View) {
        isAuthenticationClicked = when (view.id) {
            R.id.LockImage -> {
                checkAuthWithDeviceCredentials()
                true
            }
            R.id.FingerprintImage -> {
                checkAuthwithFingerPrint()
                true
            }
            R.id.FacerecignitionImage -> {
                checkAuthWithFaceRecognition()
                true
            }
            else -> false
        }
    }

    /* Checks for faceRecognition */
    private fun checkAuthWithFaceRecognition() {
        // check camera permission
        val permissionCheck =
            ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            showToast(
                this@AuthenticationPage,
                getString(R.string.FACE_AUTH_CAMERA_PERMISSION_NOT_ENABLED)
            )
            ActivityCompat.requestPermissions(
                this@AuthenticationPage,
                arrayOf(Manifest.permission.CAMERA),
                1
            )
            return
        }
        // call back
        val callback: BioAuthnCallback = object : BioAuthnCallback() {
            override fun onAuthError(
                errMsgId: Int,
                errString: CharSequence
            ) {
                showToast(
                    this@AuthenticationPage,
                    getString(R.string.FACE_AUTH_ERROR) + errMsgId + getString(R.string.errorMessage)
                            + errString
                            + if (errMsgId == 1012) getString(R.string.FACE_CAMERA_MAY_NOT_BE) else ""
                )
            }

            override fun onAuthHelp(
                helpMsgId: Int,
                helpString: CharSequence
            ) {
                showToast(
                    this@AuthenticationPage,
                    getString(R.string.FACE_AUTH_HELP) + helpMsgId + getString(R.string.helpString) + helpString
                )
            }

            override fun onAuthSucceeded(result: BioAuthnResult) {
                showToast(
                    this@AuthenticationPage,
                    getString(R.string.FACE_AUTH_SUCCESS)
                )
                if (Constants.IS_DELETED_FROM_PROFILEDETAILS) {
                    deleteCard(id)
                } else {
                    val intent = Intent(
                        applicationContext,
                        ScannedCardDetailsActivity::class.java
                    )
                    intent.putExtra(Constants.ID, id)
                    intent.putExtra(Constants.QRCODEIMAGE, qrCodeImage)
                    intent.putExtra(Constants.QRCODECARDTYPE, qrCodeCardType)

                    startActivity(intent)
                    finish()
                }
            }

            override fun onAuthFailed() {
                showToast(this@AuthenticationPage, getString(R.string.FACE_AUTH_FAIL))
            }
        }
        checkAuthenticationType(this)
        if (isFaceRecognitionSecure()) {
            // Cancellation Signal
            val cancellationSignal = CancellationSignal()
            val faceManager = FaceManager(this)
            // Checks whether 3D facial authentication can be used.
            val errorCode = faceManager.canAuth()
            if (errorCode != 0) {
                showToast(
                    this, getString(R.string.FACE_CANNOT_AUTH)
                            + errorCode
                )
                return
            }
            val flags = 0
            faceManager.auth(null, cancellationSignal, flags, callback, null)
        } else {
            Toast.makeText(
                this, R.string.FACEREC_SUPPORT_ERROR,
                Toast.LENGTH_LONG
            )
                .show()
        }
    }

    /* Creating Fingerprint result call back */
    private fun createBioAuthnPrompt(): BioAuthnPrompt {
        val fpResultCallback: BioAuthnCallback = object : BioAuthnCallback() {
            override fun onAuthError(
                errMsgId: Int,
                errString: CharSequence
            ) {
                showToast(
                    this@AuthenticationPage,
                    getString(R.string.FP_AUTH_ERROR) + errMsgId +
                            getString(R.string.errorMessage) + errString
                )
            }

            override fun onAuthSucceeded(result: BioAuthnResult) {
                if (Constants.IS_DELETED_FROM_PROFILEDETAILS) {
                    deleteCard(id)
                } else {
                    val intent = Intent(
                        applicationContext,
                        ScannedCardDetailsActivity::class.java
                    )
                    intent.putExtra(Constants.ID, id)
                    intent.putExtra(Constants.QRCODEIMAGE, qrCodeImage)
                    intent.putExtra(Constants.QRCODECARDTYPE, qrCodeCardType)
                    startActivity(intent)
                    finish()
                }
            }

            override fun onAuthFailed() {
                showToast(this@AuthenticationPage, getString(R.string.FP_AUTH_FAILED))
            }
        }
        return BioAuthnPrompt(
            this, ContextCompat.getMainExecutor(this),
            fpResultCallback
        )
    }

    /* checks for Fingerprint Authentications */
    private fun checkAuthwithFingerPrint() {
        checkAuthenticationType(this)
        if (isFingerPrintSecure()) {
            val bioAuthnManager = BioAuthnManager(this)
            val errorCode = bioAuthnManager.canAuth()
            if (errorCode != 0) {
                Toast.makeText(
                    this, R.string.FP_AUTH_CHECK + errorCode,
                    Toast.LENGTH_LONG
                ).show()
                return
            }
            val builder =
                BioAuthnPrompt.PromptInfo.Builder()
                    .setTitle(getString(R.string.FP_PROMPT_HEAD))
                    .setSubtitle(getString(R.string.FP_PROMPT_TITLE))
                    .setDescription(getString(R.string.FP_PROMPT_DESC))
                    .setDeviceCredentialAllowed(true)
            val info = builder.build()
            bioAuthnPrompt!!.auth(info)
        } else {
            Toast.makeText(
                this, R.string.FP_DEVICE_SUPPORT_ERROR,
                Toast.LENGTH_LONG
            )
                .show()
        }
    }

    /* check for PIN Lock Authentications */
    private fun checkAuthWithDeviceCredentials() {
        checkAuthenticationType(this)
        if (isKeyguardSecure()) {
            val mKeyguardManager =
                getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
            val i = mKeyguardManager.createConfirmDeviceCredentialIntent(
                getString(R.string.PIN_LOCK_TITLE),
                getString(R.string.PIN_LOCK_DESC)
            )
            try {
                startActivityForResult(i, PIN_LOCK_REQ_CODE)
            } catch (e: NullPointerException) {
                val intent = Intent(Settings.ACTION_SECURITY_SETTINGS)
                startActivityForResult(
                    intent,
                    SECURITY_SETTING_REQUEST_CODE
                )
            }
        } else {
            Toast.makeText(
                this, R.string.AUTH_SUGGESTION_MSG,
                Toast.LENGTH_LONG
            )
                .show()
        }
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PIN_LOCK_REQ_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Toast.makeText(this, R.string.PIN_SUCCESS, Toast.LENGTH_LONG).show()
                if (Constants.IS_DELETED_FROM_PROFILEDETAILS) {
                    deleteCard(id)
                } else {
                    val intent = Intent(
                        applicationContext,
                        ScannedCardDetailsActivity::class.java
                    )
                    intent.putExtra(Constants.ID, id)
                    intent.putExtra(Constants.QRCODEIMAGE, qrCodeImage)
                    intent.putExtra(Constants.QRCODECARDTYPE, qrCodeCardType)
                    startActivity(intent)
                    finish()
                }
            } else {
                Toast.makeText(
                    this, R.string.PIN_FAILURE,
                    Toast.LENGTH_LONG
                )
                    .show()
            }
        }
    }

    private fun deleteCard(id: Int) {
        @SuppressLint("StaticFieldLeak")
        class DeleteTask :
            AsyncTask<Void?, Void?, Void?>() {
            protected override fun doInBackground(vararg voids: Void?): Void? {
                getInstance(applicationContext)!!.appDatabase
                    ?.businessCardEntityDao()
                    ?.deleteById(id)
                return null
            }

            override fun onPostExecute(aVoid: Void?) {
                super.onPostExecute(aVoid)
                Toast.makeText(
                    applicationContext,
                    R.string.successFullyDeleted,
                    Toast.LENGTH_LONG
                )
                    .show()
                Constants.IS_DELETED_FROM_PROFILEDETAILS = false
                finish()
            }
        }

        val st = DeleteTask()
        st.execute()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val itemId = item.itemId
        if (itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        private const val PIN_LOCK_REQ_CODE = 11
        private const val SECURITY_SETTING_REQUEST_CODE = 111
    }
}