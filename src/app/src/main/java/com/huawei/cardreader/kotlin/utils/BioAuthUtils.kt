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

import android.app.AlertDialog
import android.app.KeyguardManager
import android.content.Context
import android.view.View
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.huawei.cardreader.R
import com.huawei.hms.support.api.fido.bioauthn.BioAuthnManager
import com.huawei.hms.support.api.fido.bioauthn.FaceManager

/**
 * The type Bio auth utils.
 */
object BioAuthUtils {
    /**
     * The constant isKeyguardSecure.
     */
    private var isKeyguardSecure = false

    /**
     * The constant isFingerPrintSecure.
     */
    private var isFingerPrintSecure = false

    /**
     * The constant isFaceRecognitionSecure.
     */
    private var isFaceRecognitionSecure = false

    /**
     * Is device authenticated boolean.
     *
     * @return the boolean
     */
    /**
     * Sets is device authenticated.
     *
     * @param isDeviceAuthenticated the is device authenticated
     */
    /**
     * The constant isDeviceAuthenticated.
     * 'false' if device is not authenticated using any of the PINLock or FingerPrint or
     * FaceRecognition
     * 'true' if device is authenticated using any one of the above
     */
    var isDeviceAuthenticated = false

    /**
     * Is keyguard secure boolean.
     *
     * @return the boolean
     */
    fun isKeyguardSecure(): Boolean {
        return isKeyguardSecure
    }

    /**
     * Sets is keyguard secure.
     *
     * @param isKeyguardSecure the is keyguard secure
     */
    fun setIsKeyguardSecure(isKeyguardSecure: Boolean) {
        BioAuthUtils.isKeyguardSecure = isKeyguardSecure
    }

    /**
     * Is finger print secure boolean.
     *
     * @return the boolean
     */
    fun isFingerPrintSecure(): Boolean {
        return isFingerPrintSecure
    }

    /**
     * Sets is finger print secure.
     *
     * @param isFingerPrintSecure the is finger print secure
     */
    fun setIsFingerPrintSecure(isFingerPrintSecure: Boolean) {
        BioAuthUtils.isFingerPrintSecure = isFingerPrintSecure
    }

    /**
     * Is face recognition secure boolean.
     *
     * @return the boolean
     */
    fun isFaceRecognitionSecure(): Boolean {
        return isFaceRecognitionSecure
    }

    /**
     * Sets is face recognition secure.
     *
     * @param isFaceRecognitionSecure the is face recognition secure
     */
    fun setIsFaceRecognitionSecure(isFaceRecognitionSecure: Boolean) {
        BioAuthUtils.isFaceRecognitionSecure = isFaceRecognitionSecure
    }

    /**
     * Check authentication type.
     *
     * @param context the context
     */
    fun checkAuthenticationType(context: Context) {
        isKeyguardSecure = checkIsKeyguardSecured(context)
        isFingerPrintSecure = checkIsFingerprintSecured(context)
        isFaceRecognitionSecure = checkIsFaceRecognitionSecured(context)
    }

    /**
     * show Guide
     *
     * @param view      the View
     * @param MessageID the int
     */
    fun showGuide(view: View?, MessageID: Int) {
        Snackbar.make(view!!, MessageID, Snackbar.LENGTH_LONG)
            .show()
    }

    /**
     * Check is face recognition secured boolean.
     *
     * @param context the context
     * @return the boolean
     */
    fun checkIsFaceRecognitionSecured(context: Context): Boolean {
        val errorCode: Int
        errorCode = try {
            val faceManager = FaceManager(context)
            faceManager.canAuth()
        } catch (e: NullPointerException) {
            Toast.makeText(context, R.string.FACEREC_SUPPORT_ERROR, Toast.LENGTH_LONG).show()
            return false
        }
        if (errorCode != 0) {
            Toast.makeText(
                context,
                context.getString(R.string.FACEREC_AUTH_CHECK) + errorCode,
                Toast.LENGTH_LONG
            )
                .show()
            return false
        }
        isDeviceAuthenticated = true
        return true
    }

    /**
     * Check is fingerprint secured boolean.
     *
     * @param context the context
     * @return the boolean
     */
    fun checkIsFingerprintSecured(context: Context): Boolean {
        return try {
            val bioAuthnManager = BioAuthnManager(context)
            val errorCode = bioAuthnManager.canAuth()
            if (errorCode != 0) {
                Toast.makeText(
                    context,
                    context.getString(R.string.FP_AUTH_CHECK) + errorCode,
                    Toast.LENGTH_LONG
                )
                    .show()
                return false
            }
            isDeviceAuthenticated = true
            true
        } catch (e: NullPointerException) {
            Toast.makeText(context, R.string.FP_DEVICE_SUPPORT_ERROR, Toast.LENGTH_LONG).show()
            false
        }
    }

    /**
     * Check is keyguard secured boolean.
     *
     * @param context the context
     * @return the boolean
     */
    fun checkIsKeyguardSecured(context: Context): Boolean {
        return try {
            val mKeyguardManager =
                context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
            if (mKeyguardManager.isKeyguardSecure) {
                isDeviceAuthenticated = true
                return true
            } else {
                showToast(context, context.getString(R.string.PINLOCK_SUGGESTION_MSG))
            }
            false
        } catch (e: NullPointerException) {
            false
        }
    }

    /**
     * Show toast.
     *
     * @param context the context
     * @param msg     the msg
     */
    fun showToast(context: Context?, msg: String?) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(R.string.AUTH_RESULT)
        builder.setMessage(msg)
        builder.setPositiveButton(R.string.ok, null)
        builder.show()
    }
}