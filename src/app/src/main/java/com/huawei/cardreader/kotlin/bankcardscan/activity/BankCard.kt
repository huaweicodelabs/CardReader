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

package com.huawei.cardreader.kotlin.bankcardscan.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.os.AsyncTask
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.huawei.cardreader.R
import com.huawei.cardreader.kotlin.scannedcardlist.activity.ScannedCardListActivity
import com.huawei.cardreader.kotlin.scannedcardlist.roomdb.BusinessCardEntity
import com.huawei.cardreader.kotlin.scannedcardlist.roomdb.DatabaseClient
import com.huawei.cardreader.kotlin.utils.Constants
import com.huawei.cardreader.kotlin.utils.DataConverter
import com.huawei.hms.mlplugin.card.bcr.MLBcrCapture
import com.huawei.hms.mlplugin.card.bcr.MLBcrCaptureConfig
import com.huawei.hms.mlplugin.card.bcr.MLBcrCaptureFactory
import com.huawei.hms.mlplugin.card.bcr.MLBcrCaptureResult
import java.util.*

/**
 * The type Bank card.
 */
class BankCard : AppCompatActivity(), View.OnClickListener {
    private var bankCardFrontImg: ImageView? = null
    private var bankCardFrontSimpleImg: ImageView? = null
    private lateinit var bankCardFrontDeleteImg: ImageView
    private lateinit var numberImageView: ImageView
    private lateinit var bankCardFrontAddView: LinearLayout
    private var results: LinearLayout? = null
    private var showResult: TextView? = null
    private var lastFrontResult: String? = ""
    private var currentImage: Bitmap? = null
    private var scannedResult: MLBcrCaptureResult? = null
    private var bitmap: Bitmap? = null
    private var save: Button? = null
    private var emptyImg: RelativeLayout? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bank_card)
        Objects.requireNonNull(supportActionBar)?.setDisplayHomeAsUpEnabled(true)
        val window = window
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )
        initComponent()
    }

    /**
     * Init component.
     */
    private fun initComponent() {
        bankCardFrontImg = findViewById(R.id.avatar_img)
        bankCardFrontSimpleImg = findViewById(R.id.avatar_sample_img)
        bankCardFrontDeleteImg = findViewById(R.id.avatar_delete)
        bankCardFrontAddView = findViewById(R.id.avatar_add)
        showResult = findViewById(R.id.show_result)
        save = findViewById(R.id.btn_save)
        results = findViewById(R.id.results)
        emptyImg = findViewById(R.id.emptyimg)
        bankCardFrontAddView.setOnClickListener(this)
        bankCardFrontDeleteImg.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        val id = view.id
        when (id) {
            R.id.avatar_add -> startCaptureActivity()
            R.id.avatar_delete -> {
                showFrontDeleteImage()
                lastFrontResult = ""
            }
            else -> {
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (currentImage != null && !currentImage!!.isRecycled) {
            currentImage!!.recycle()
            currentImage = null
        }
    }

    /**
     * Start Capture Activity.
     */
    private fun startCaptureActivity() {
        val config = MLBcrCaptureConfig.Factory()
            .setOrientation(MLBcrCaptureConfig.ORIENTATION_AUTO)
            .setResultType(MLBcrCaptureConfig.RESULT_ALL)
            .create()
        val bcrCapture = MLBcrCaptureFactory.getInstance().getBcrCapture(config)
        bcrCapture.captureFrame(this, callback)
    }

    /**
     * Format Card Results.
     *
     * @param result of type MLBcrCaptureResult
     * @return String of type bankcard
     */
    private fun formatCardResult(result: MLBcrCaptureResult): String {
        scannedResult = result
        return Constants.BANKCARDNUMBER.toString() +
                result.number +
                System.lineSeparator() +
                Constants.ISSUER +
                result.issuer +
                System.lineSeparator() +
                Constants.EXPIRE +
                result.expire +
                System.lineSeparator() +
                Constants.CARDTYPE +
                result.type +
                System.lineSeparator() +
                Constants.ORGANIZATION +
                result.organization +
                System.lineSeparator()
    }

    private val callback: MLBcrCapture.Callback = object : MLBcrCapture.Callback {
        override fun onSuccess(result: MLBcrCaptureResult) {
            if (result == null) {
                return
            }
            bitmap = result.originalBitmap
            showSuccessResult(bitmap, result)
        }

        override fun onCanceled() {}
        override fun onFailure(retCode: Int, bitmap: Bitmap) {
            showResult!!.text = getString(R.string.REC_FAILED)
        }

        override fun onDenied() {}
    }

    /**
     * Show Success Result.
     *
     * @param bitmap       of type Bitmap
     * @param idCardResult MLBcrCaptureResult
     */
    private fun showSuccessResult(bitmap: Bitmap?, idCardResult: MLBcrCaptureResult) {
        showFrontImage(bitmap)
        lastFrontResult = formatCardResult(idCardResult)
        showResult!!.text = lastFrontResult
        val lastBackResult = ""
        showResult!!.append(lastBackResult)
        numberImageView = findViewById(R.id.number)
        numberImageView.setImageBitmap(idCardResult.numberBitmap)
    }

    private fun showFrontImage(bitmap: Bitmap?) {
        bankCardFrontImg!!.visibility = View.VISIBLE
        bankCardFrontImg!!.setImageBitmap(bitmap)
        bankCardFrontSimpleImg!!.visibility = View.GONE
        bankCardFrontAddView!!.visibility = View.GONE
        bankCardFrontDeleteImg!!.visibility = View.VISIBLE
        save!!.visibility = View.VISIBLE
        results!!.visibility = View.VISIBLE
        emptyImg!!.visibility = View.GONE
    }

    private fun showFrontDeleteImage() {
        bankCardFrontImg!!.visibility = View.GONE
        bankCardFrontSimpleImg!!.visibility = View.VISIBLE
        bankCardFrontAddView!!.visibility = View.VISIBLE
        bankCardFrontDeleteImg!!.visibility = View.GONE
        save!!.visibility = View.GONE
        results!!.visibility = View.GONE
    }

    /**
     * Save card.
     *
     * @param v the v
     */
    fun saveCard(v: View?) {
        if (lastFrontResult != null && !lastFrontResult!!.isEmpty()) {
            save()
        } else {
            Toast.makeText(applicationContext, R.string.pleasescancard, Toast.LENGTH_LONG)
                .show()
        }
    }

    private fun save() {
        @SuppressLint("StaticFieldLeak")
        class SaveTask :
            AsyncTask<Void?, Void?, Void?>() {
            protected override fun doInBackground(vararg voids: Void?): Void? {
                val userDetails = BusinessCardEntity()
                userDetails.name=getString(R.string.xxx)
                userDetails.accountnumber=DataConverter.enCodeString(scannedResult!!.number)
                userDetails.issuer=scannedResult!!.issuer
                userDetails.expirydate=DataConverter.enCodeString(scannedResult!!.expire)
                userDetails.banktype=scannedResult!!.type
                userDetails.bankorganization=DataConverter.enCodeString(scannedResult!!.organization
                )
                userDetails.cardType=getString(R.string.cardType_bankCard)
                userDetails.image = DataConverter.bitMapToString(bitmap!!)
                DatabaseClient.getInstance(applicationContext)?.appDatabase
                    ?.businessCardEntityDao()
                    ?.insert(userDetails)
                return null
            }

            override fun onPostExecute(aVoid: Void?) {
                super.onPostExecute(aVoid)
                bankCardFrontImg!!.visibility = View.GONE
                bankCardFrontSimpleImg!!.visibility = View.VISIBLE
                bankCardFrontAddView!!.visibility = View.VISIBLE
                bankCardFrontDeleteImg!!.visibility = View.GONE
                showResult!!.visibility = View.GONE
                numberImageView!!.visibility = View.GONE
                val intent =
                    Intent(applicationContext, ScannedCardListActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                intent.putExtra(Constants.CARD_TYPE_TO_SAVE, getString(R.string.cardType_bankCard))
                startActivity(intent)
            }
        }

        val st = SaveTask()
        st.execute()
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