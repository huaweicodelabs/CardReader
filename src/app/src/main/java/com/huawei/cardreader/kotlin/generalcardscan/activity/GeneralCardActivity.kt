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

package com.huawei.cardreader.kotlin.generalcardscan.activity

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.provider.MediaStore
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.huawei.cardreader.R
import com.huawei.cardreader.kotlin.generalcardscan.processor.gcr.aadharcard.AadhaarProcessing
import com.huawei.cardreader.kotlin.generalcardscan.processor.gcr.businesscard.GenericProcessing
import com.huawei.cardreader.kotlin.generalcardscan.processor.gcr.pancard.PanProcessing
import com.huawei.cardreader.kotlin.scannedcardlist.activity.ScannedCardListActivity
import com.huawei.cardreader.kotlin.scannedcardlist.roomdb.BusinessCardEntity
import com.huawei.cardreader.kotlin.scannedcardlist.roomdb.DatabaseClient
import com.huawei.cardreader.kotlin.utils.Constants
import com.huawei.cardreader.kotlin.utils.DataConverter
import com.huawei.hms.mlplugin.card.gcr.*
import java.io.IOException
import java.util.*

/**
 * The type General card activity.
 */
class GeneralCardActivity : AppCompatActivity(), View.OnClickListener {
    private var mImageUri: Uri? = null
    private val `object`: Any = false
    private lateinit var frontImg: ImageView
    private lateinit var frontSimpleImg: ImageView
    private lateinit var frontDeleteImg: ImageView
    private lateinit var frontAddView: LinearLayout
    private var aadharListResults: LinearLayout? = null
    private var businessCardList: LinearLayout? = null
    private var panCard: LinearLayout? = null
    private var emptyImg: RelativeLayout? = null
    private var progressDialog: ProgressDialog? = null
    private var bitmap: Bitmap? = null
    private var mTryImageBitmap: Bitmap? = null
    private var cardTypeEnum =
        CardType.BusinessCard
    private var imageBitmap: Bitmap? = null
    private var cardTypeToSave: String? = null
    private var dataMap: HashMap<String, String?>? = null
    private var mContactName: EditText? = null
    private var mContactNumber: TextView? = null
    private var mContactEmail: TextView? = null
    private var mContactOrganization: TextView? = null
    private var cardview: CardView? = null
    private var name: TextView? = null
    private var aadharName: TextView? = null
    private var fatherName: TextView? = null
    private var panNumber: TextView? = null
    private var dateOfBirth: TextView? = null
    private var aadhaarNumber: TextView? = null
    private var gender: TextView? = null
    private var yearOfBirth: TextView? = null
    private var contactResults: TextView? = null

    /**
     * The Radio listener.
     */
    var radioListener =
        RadioGroup.OnCheckedChangeListener { group: RadioGroup?, checkedId: Int ->
            when (checkedId) {
                R.id.businesscard -> {
                    updateCardType(CardType.BusinessCard)
                    cardTypeToSave = Constants.BUSINESS_CARD
                    emptyImg!!.visibility = View.VISIBLE
                }
                R.id.panard -> {
                    updateCardType(CardType.PanCard)
                    cardTypeToSave = Constants.PANCARD
                    emptyImg!!.visibility = View.VISIBLE
                }
                R.id.aadharcard -> {
                    updateCardType(CardType.AadharCard)
                    cardTypeToSave = Constants.AADHARCARD
                    emptyImg!!.visibility = View.VISIBLE
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_generalcard)
        Objects.requireNonNull(supportActionBar)?.setDisplayHomeAsUpEnabled(true)
        initComponent()
    }

    /**
     * updating card type.
     *
     * @param type of CardType
     */
    private fun updateCardType(type: CardType) {
        if (cardTypeEnum != type) {
            cardview!!.visibility = View.GONE
            showFrontDeleteImage()
        }
        cardTypeEnum = type
    }

    /**
     * Init component.
     */
    fun initComponent() {
        frontImg = findViewById(R.id.avatar_img)
        frontSimpleImg = findViewById(R.id.avatar_sample_img)
        frontDeleteImg = findViewById(R.id.avatar_delete)
        frontAddView = findViewById(R.id.avatar_add)
        frontAddView.setOnClickListener(this)
        frontDeleteImg.setOnClickListener(this)
        val cardType = findViewById<RadioGroup>(R.id.card_type)
        cardType.setOnCheckedChangeListener(radioListener)
        mContactName = findViewById(R.id.contactname)
        mContactNumber = findViewById(R.id.contactnumber)
        mContactEmail = findViewById(R.id.contactemail)
        mContactOrganization = findViewById(R.id.contactogranization)
        cardTypeToSave = Constants.BUSINESS_CARD
        aadharListResults = findViewById(R.id.aadharlistresults)
        businessCardList = findViewById(R.id.businesscardlist)
        panCard = findViewById(R.id.pancard)
        name = findViewById(R.id.name)
        aadharName = findViewById(R.id.Aadharname)
        fatherName = findViewById(R.id.fname)
        panNumber = findViewById(R.id.pan)
        dateOfBirth = findViewById(R.id.dob)
        yearOfBirth = findViewById(R.id.yob)
        aadhaarNumber = findViewById(R.id.aadharnum)
        gender = findViewById(R.id.gender)
        contactResults = findViewById(R.id.contactresults)
        cardview = findViewById(R.id.allresultslist)
        emptyImg = findViewById(R.id.emptyimg)
    }

    override fun onClick(view: View) {
        val id = view.id
        when (id) {
            R.id.avatar_add -> detectPhoto(`object`, callback)
            R.id.avatar_delete -> showFrontDeleteImage()
            R.id.back -> finish()
            else -> {
            }
        }
    }

    /**
     * Save card.
     *
     * @param v the v
     */
    fun saveCard(v: View?) {
        if (dataMap != null && !dataMap!!.isEmpty() && dataMap!!.size > 1 ||
            contactResults != null && contactResults!!.text != null &&
            !contactResults!!.text.toString().isEmpty()
        ) {
            save()
        } else {
            Toast.makeText(applicationContext, R.string.pleasescancard, Toast.LENGTH_LONG)
                .show()
        }
    }

    private fun save() {
        class SaveTask :
            AsyncTask<Void?, Void?, Void?>() {
            override fun onPreExecute() {
                super.onPreExecute()
                progressDialog = ProgressDialog(this@GeneralCardActivity)
                progressDialog!!.setMessage(getString(R.string.saving))
                progressDialog!!.isIndeterminate = false
                progressDialog!!.setCancelable(false)
                progressDialog!!.show()
            }

            protected override fun doInBackground(vararg voids: Void?): Void? {
                val userDetails = BusinessCardEntity()
                if (cardTypeToSave.equals(Constants.BUSINESS_CARD, ignoreCase = true)) {
                    userDetails.name=
                        DataConverter.enCodeString(
                            mContactName!!.text.toString()
                        )

                    userDetails.mobileno=
                        DataConverter.enCodeString(
                            mContactNumber!!.text.toString()
                        )

                    userDetails.emailid=
                        DataConverter.enCodeString(
                            mContactEmail!!.text.toString()
                        )

                    userDetails.companyname=
                        DataConverter.enCodeString(
                            mContactOrganization!!.text.toString()
                        )

                    userDetails.address = ""
                    userDetails.jobtitle = ""
                    userDetails.website = ""
                    userDetails.cardType = cardTypeToSave
                    userDetails.image = DataConverter.bitMapToString(bitmap!!)
                } else if (cardTypeToSave.equals(Constants.AADHARCARD, ignoreCase = true)) {
                    userDetails.name=DataConverter.enCodeString(name!!.text.toString())
                    userDetails.aadharid=
                        DataConverter.enCodeString(
                            aadhaarNumber!!.text.toString().trim { it <= ' ' }
                        )

                    userDetails.gender=DataConverter.enCodeString(gender!!.text.toString())
                    userDetails.fathername=
                        DataConverter.enCodeString(
                            fatherName!!.text.toString()
                        )

                    userDetails.dob=
                        DataConverter.enCodeString(
                            yearOfBirth!!.text.toString().trim { it <= ' ' }.replace
                                (":", "")
                        )

                    userDetails.address=""
                    userDetails.cardType=cardTypeToSave
                    userDetails.image=DataConverter.bitMapToString(bitmap!!)
                } else {
                    userDetails.name=DataConverter.enCodeString(name!!.text.toString())
                    userDetails.pannumber=
                        DataConverter.enCodeString(
                            panNumber!!.text.toString()
                        )

                    userDetails.fathername=
                        DataConverter.enCodeString(
                            fatherName!!.text.toString()
                        )

                    userDetails.dob=DataConverter.enCodeString(dateOfBirth!!.text.toString())
                    userDetails.address=""
                    userDetails.cardType=cardTypeToSave
                    userDetails.image=DataConverter.bitMapToString(bitmap!!)
                }
                DatabaseClient.getInstance(applicationContext)?.appDatabase
                    ?.businessCardEntityDao()
                    ?.insert(userDetails)
                return null
            }

            override fun onPostExecute(aVoid: Void?) {
                super.onPostExecute(aVoid)
                progressDialog!!.dismiss()
                dataMap!!.clear()
                contactResults!!.text = ""
                frontImg!!.visibility = View.GONE
                frontSimpleImg!!.visibility = View.VISIBLE
                frontAddView!!.visibility = View.VISIBLE
                frontDeleteImg!!.visibility = View.GONE
                cardview!!.visibility = View.GONE
                val intent =
                    Intent(applicationContext, ScannedCardListActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                intent.putExtra(Constants.CARD_TYPE_TO_SAVE, cardTypeToSave)
                startActivity(intent)
            }
        }

        val st = SaveTask()
        st.execute()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        intent: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, intent)
        if (requestCode == REQUEST_IMAGE_SELECT_FROM_ALBUM && resultCode == Activity.RESULT_OK) {
            if (intent != null) {
                mImageUri = intent.data
            }
            tryReloadAndDetectInImage()
        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            tryReloadAndDetectInImage()
        }
    }

    // take a picture
    private fun detectPhoto(`object`: Any, callback: MLGcrCapture.Callback) {
        val cardConfig =
            MLGcrCaptureConfig.Factory().setLanguage(getString(R.string.en)).create()
        val uiConfig = MLGcrCaptureUIConfig.Factory()
            .setTipText(resources.getString(R.string.capture_tip))
            .setOrientation(MLGcrCaptureUIConfig.ORIENTATION_AUTO).create()
        val ocrManager = MLGcrCaptureFactory.getInstance().getGcrCapture(
            cardConfig,
            uiConfig
        )
        ocrManager.capturePhoto(this, `object`, callback)
    }

    // local image
    private fun detectLocalImage(bitmap: Bitmap?, callback: MLGcrCapture.Callback) {
        val config = MLGcrCaptureConfig.Factory().create()
        val ocrManager = MLGcrCaptureFactory.getInstance().getGcrCapture(config)
        ocrManager.captureImage(bitmap, null, callback)
    }

    /* Callback Function*/
    private val callback: MLGcrCapture.Callback = object : MLGcrCapture.Callback {
        override fun onResult(result: MLGcrCaptureResult, o: Any): Int {
            if (result == null) {
                return MLGcrCaptureResult.CAPTURE_CONTINUE
            }
            if (cardTypeEnum == CardType.BusinessCard) {
                dataMap = GenericProcessing().processText(result.text, applicationContext)
                BusinessOutput(dataMap)
                businessCardList!!.visibility = View.VISIBLE
                aadharListResults!!.visibility = View.GONE
                panCard!!.visibility = View.GONE
                contactResults!!.visibility = View.GONE
                if (businessCardList!!.visibility == View.VISIBLE) {
                    emptyImg!!.visibility = View.GONE
                }
            } else if (cardTypeEnum == CardType.PanCard) {
                dataMap = PanProcessing().processText(result.text, applicationContext)
                panCard!!.visibility = View.VISIBLE
                businessCardList!!.visibility = View.GONE
                aadharListResults!!.visibility = View.GONE
                if (businessCardList!!.visibility == View.GONE) {
                    emptyImg!!.visibility = View.GONE
                }
            } else if (cardTypeEnum == CardType.AadharCard) {
                dataMap = AadhaarProcessing().processExtractedTextForFrontPic(
                    result.text,
                    applicationContext
                )
                presentFrontOutput(dataMap)
                panCard!!.visibility = View.GONE
                businessCardList!!.visibility = View.GONE
                aadharListResults!!.visibility = View.VISIBLE
                if (businessCardList!!.visibility == View.GONE) {
                    emptyImg!!.visibility = View.GONE
                }
            }
            val a = DataConverter.bitMapToString(result.cardBitmap)
            bitmap = DataConverter.stringToBitMap(a)
            showFrontImage(result.cardBitmap)
            presentOutput(dataMap)
            // If the results don't match
            return MLGcrCaptureResult.CAPTURE_STOP
        }

        override fun onCanceled() {}
        override fun onFailure(retCode: Int, bitmap: Bitmap) {}
        override fun onDenied() {}
    }

    /**
     * The enum Card type.
     */
    enum class CardType {
        /**
         * Business card card type.
         */
        BusinessCard,

        /**
         * Pan card card type.
         */
        PanCard,

        /**
         * Aadhar card card type.
         */
        AadharCard
    }

    /* Pan Card Output*/
    private fun presentOutput(dataMap: HashMap<String, String?>?) {
        if (dataMap != null) {
            name!!.setText(dataMap[getString(R.string.panOutName)], TextView.BufferType.EDITABLE)
            fatherName!!.setText(
                dataMap[getString(R.string.panOutFatherName)],
                TextView.BufferType.EDITABLE
            )
            panNumber!!.setText(
                dataMap[getString(R.string.panOutNumber)],
                TextView.BufferType.EDITABLE
            )
            dateOfBirth!!.setText(
                dataMap[getString(R.string.panOutDob)],
                TextView.BufferType.EDITABLE
            )
        }
    }

    /* Aadhar Card Output*/
    private fun presentFrontOutput(dataMap: HashMap<String, String?>?) {
        if (dataMap != null) {
            aadharName!!.setText(
                dataMap[getString(R.string.aadharOutName)],
                TextView.BufferType.EDITABLE
            )
            aadhaarNumber!!.setText(
                dataMap[getString(R.string.aadharOutNumber)],
                TextView.BufferType.EDITABLE
            )
            gender!!.setText(
                dataMap[getString(R.string.aadharOutGender)],
                TextView.BufferType.EDITABLE
            )
            fatherName!!.setText(
                dataMap[getString(R.string.aadharOutFatherName)],
                TextView.BufferType.EDITABLE
            )
            dateOfBirth!!.setText(
                dataMap[getString(R.string.aadharOutDob)],
                TextView.BufferType.EDITABLE
            )
            if (dataMap[getString(R.string.aadharOutYob)] != null) {
                yearOfBirth!!.setText(
                    Objects.requireNonNull(
                        dataMap[getString(R.string.aadharOutYob)]
                    )?.replace(getString(R.string.aadharOutBirth), ""), TextView.BufferType.EDITABLE
                )
            }
        }
    }

    /* Business Card Output*/
    private fun BusinessOutput(dataMap: HashMap<String, String?>?) {
        if (dataMap != null) {
            contactResults!!.text = dataMap[getString(R.string.businessOutText)]
            mContactName!!.setText(dataMap[getString(R.string.businessOutName)])
            mContactNumber!!.text = dataMap[getString(R.string.businessOutPhoneNumber)]
            mContactEmail!!.text = dataMap[getString(R.string.businessOutEmail)]
            mContactOrganization!!.text = dataMap[getString(R.string.businessOutOrganization)]
        }
    }

    private fun showFrontImage(bitmap: Bitmap) {
        frontImg!!.visibility = View.VISIBLE
        frontImg!!.setImageBitmap(bitmap)
        frontSimpleImg!!.visibility = View.GONE
        frontAddView!!.visibility = View.GONE
        frontDeleteImg!!.visibility = View.VISIBLE
        cardview!!.visibility = View.VISIBLE
    }

    private fun showFrontDeleteImage() {
        frontImg!!.visibility = View.GONE
        frontSimpleImg!!.visibility = View.VISIBLE
        frontAddView!!.visibility = View.VISIBLE
        frontDeleteImg!!.visibility = View.GONE
        cardview!!.visibility = View.GONE
    }

    /* Detect an Image*/
    private fun tryReloadAndDetectInImage() {
        if (mImageUri == null) {
            return
        }
        try {
            mTryImageBitmap = MediaStore.Images.Media.getBitmap(contentResolver, mImageUri)
        } catch (error: IOException) {
        }
        detectLocalImage(mTryImageBitmap, callback)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (imageBitmap != null && !imageBitmap!!.isRecycled) {
            imageBitmap!!.recycle()
            imageBitmap = null
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        private const val REQUEST_IMAGE_SELECT_FROM_ALBUM = 1000
        private const val REQUEST_IMAGE_CAPTURE = 1001
    }
}