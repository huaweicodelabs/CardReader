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

package com.huawei.cardreader.kotlin.scannedcardlist.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.os.AsyncTask
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.huawei.cardreader.R
import com.huawei.cardreader.databinding.ActivityQrCodeGeneratorMainBinding
import com.huawei.cardreader.kotlin.bankcardscan.activity.BankCard
import com.huawei.cardreader.kotlin.generalcardscan.activity.GeneralCardActivity
import com.huawei.cardreader.kotlin.scannedcardlist.adapter.BankCardListAdapter
import com.huawei.cardreader.kotlin.scannedcardlist.adapter.BankCardListAdapter.BankAdapterCallback
import com.huawei.cardreader.kotlin.scannedcardlist.adapter.GeneralcardListAdapter
import com.huawei.cardreader.kotlin.scannedcardlist.adapter.GeneralcardListAdapter.AdapterCallback
import com.huawei.cardreader.kotlin.scannedcardlist.model.QRcodmodel
import com.huawei.cardreader.kotlin.scannedcardlist.roomdb.BusinessCardEntity
import com.huawei.cardreader.kotlin.scannedcardlist.roomdb.DatabaseClient.Companion.getInstance
import com.huawei.cardreader.kotlin.utils.Constants
import com.huawei.cardreader.kotlin.utils.Constants.CARD_TYPE_TO_SAVE
import com.huawei.cardreader.kotlin.utils.Constants.IS_DELETED
import com.huawei.cardreader.kotlin.utils.DataConverter.deCodeString
import com.huawei.hms.hmsscankit.ScanUtil
import com.huawei.hms.hmsscankit.WriterException
import com.huawei.hms.ml.scan.HmsBuildBitmapOption
import com.huawei.hms.ml.scan.HmsScan
import java.util.*

/**
 * The type Qr code generator main activity.
 */
class ScannedCardListActivity : AppCompatActivity(), AdapterCallback,
    BankAdapterCallback {
    private val type = HmsScan.QRCODE_SCAN_TYPE
    private var cardType: String? = null
    private var mActivityQrCodeGeneratorMainBinding: ActivityQrCodeGeneratorMainBinding? =
        null
    private var shouldExecuteOnResume = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivityQrCodeGeneratorMainBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_qr_code_generator_main)
        Objects.requireNonNull(supportActionBar)?.setDisplayHomeAsUpEnabled(true)
        shouldExecuteOnResume = false
        initComponent()
    }

    /* Initialize component and fetch datas from local room db**/
    private fun initComponent() {
        if (intent.getStringExtra(CARD_TYPE_TO_SAVE) != null) {
            cardType = intent.getStringExtra(CARD_TYPE_TO_SAVE)
        }
        mActivityQrCodeGeneratorMainBinding!!.relScan.setOnClickListener { view: View? ->
            openScannerActivity() }
        mActivityQrCodeGeneratorMainBinding!!.relBottomScan.setOnClickListener { view: View? ->
            openScannerActivity() }
        datafromRoomdb
    }

    /**
     * Navigate to respective activity from list of cards screen
     */
    private fun openScannerActivity() {
        if (cardType.equals(
                Constants.BANKCARD,
                ignoreCase = true
            )
        ) {
            val intent = Intent(this@ScannedCardListActivity, BankCard::class.java)
            startActivity(intent)
        } else {
            val intent =
                Intent(this@ScannedCardListActivity, GeneralCardActivity::class.java)
            startActivity(intent)
        }
    }

    /**
     * Retrive card details  data from room db
     */
    private val datafromRoomdb: Unit
        private get() {
            @SuppressLint("StaticFieldLeak")
            class GetTasks :
                AsyncTask<Void?, Void?, List<BusinessCardEntity?>?>() {
                 override fun doInBackground(vararg voids: Void?): List<BusinessCardEntity?>? {
                    return allCards
                }

                override fun onPostExecute(tasks: List<BusinessCardEntity?>?) {
                    super.onPostExecute(tasks)
                    val businessCardEntityList: MutableList<BusinessCardEntity> =
                        ArrayList()
                    val bankCardEntityList: MutableList<BusinessCardEntity> =
                        ArrayList()
                    val options = HmsBuildBitmapOption.Creator()
                        .setBitmapMargin(0).create()
                    setCardDetails(
                        tasks, businessCardEntityList, bankCardEntityList,
                        options
                    )
                }
            }

            val gt = GetTasks()
            gt.execute()
        }

    private fun setCardDetails(
        tasks: List<BusinessCardEntity?>?,
        businessCardEntityList: MutableList<BusinessCardEntity>,
        bankCardEntityList: MutableList<BusinessCardEntity>,
        options: HmsBuildBitmapOption
    ) {
        var stringBuilder: StringBuilder
        val dataset = ArrayList<QRcodmodel>()
        val dataset1 = ArrayList<QRcodmodel>()
        var qrBitmap: Bitmap?
        val width = 200
        val height = 200
        try {
            for (i in tasks!!.indices) {
                val qRcodmodel = QRcodmodel()
                val userDetails = BusinessCardEntity()
                if (tasks[i]!!.cardType.equals(
                        Constants.BUSINESS_CARD,
                        ignoreCase = true
                    )
                ) {
                    userDetails.id = tasks[i]!!.id
                    userDetails.name = tasks[i]!!.name
                    userDetails.mobileno = tasks[i]!!.mobileno
                    userDetails.companyname = tasks[i]!!.companyname
                    userDetails.emailid = tasks[i]!!.emailid
                    userDetails.address = tasks[i]!!.address
                    userDetails.website = tasks[i]!!.website
                    userDetails.jobtitle = tasks[i]!!.jobtitle
                    userDetails.image = tasks[i]!!.image
                    userDetails.cardType = tasks[i]!!.cardType
                    businessCardEntityList.add(userDetails)
                    stringBuilder = StringBuilder()
                    stringBuilder.append(resources.getString(R.string.qrName))
                        .append(deCodeString(userDetails.name!!))
                        .append(System.lineSeparator())
                    stringBuilder.append(resources.getString(R.string.qrMobileno))
                        .append(deCodeString(userDetails.mobileno!!))
                        .append(System.lineSeparator())
                    stringBuilder.append(resources.getString(R.string.qrCompanyName))
                        .append(deCodeString(userDetails.companyname!!))
                        .append(System.lineSeparator())
                    stringBuilder.append(resources.getString(R.string.qrEmailId))
                        .append(deCodeString(userDetails.emailid!!))
                        .append(System.lineSeparator())
                    stringBuilder.append(resources.getString(R.string.qrCardType))
                        .append(userDetails.cardType).append(System.lineSeparator())
                    qrBitmap =
                        ScanUtil.buildBitmap(stringBuilder.toString(), type, width, height, options)
                    qRcodmodel.businesscardUserDetailsList = businessCardEntityList
                    qRcodmodel.bitmap = qrBitmap
                    dataset.add(qRcodmodel)
                } else if (tasks[i]!!.cardType.equals(
                        Constants.AADHARCARD,
                        ignoreCase = true
                    )
                ) {
                    userDetails.id = tasks[i]!!.id
                    userDetails.name = tasks[i]!!.name
                    userDetails.aadharid = tasks[i]!!.aadharid
                    userDetails.gender = tasks[i]!!.gender
                    userDetails.fathername = tasks[i]!!.fathername
                    userDetails.dob = tasks[i]!!.dob
                    userDetails.address = tasks[i]!!.address
                    userDetails.cardType = tasks[i]!!.cardType
                    userDetails.image = tasks[i]!!.image
                    businessCardEntityList.add(userDetails)
                    stringBuilder = StringBuilder()
                    stringBuilder.append(resources.getString(R.string.qrName))
                        .append(deCodeString(userDetails.name!!))
                        .append(System.lineSeparator())
                    stringBuilder.append(resources.getString(R.string.qrCardType))
                        .append(userDetails.cardType).append(System.lineSeparator())
                    stringBuilder.append(resources.getString(R.string.qrAadhaarId))
                        .append(deCodeString(userDetails.aadharid!!))
                        .append(System.lineSeparator())
                    stringBuilder.append(resources.getString(R.string.qrDOB))
                        .append(deCodeString(userDetails.dob!!))
                        .append(System.lineSeparator())
                    qrBitmap =
                        ScanUtil.buildBitmap(stringBuilder.toString(), type, width, height, options)
                    qRcodmodel.businesscardUserDetailsList = businessCardEntityList
                    qRcodmodel.bitmap = qrBitmap
                    dataset.add(qRcodmodel)
                } else if (tasks[i]!!.cardType.equals(
                        Constants.PANCARD,
                        ignoreCase = true
                    )
                ) {
                    userDetails.id = tasks[i]!!.id
                    userDetails.name = tasks[i]!!.name
                    userDetails.pannumber = tasks[i]!!.pannumber
                    userDetails.fathername = tasks[i]!!.fathername
                    userDetails.dob = tasks[i]!!.dob
                    userDetails.cardType = tasks[i]!!.cardType
                    userDetails.image = tasks[i]!!.image
                    businessCardEntityList.add(userDetails)
                    stringBuilder = StringBuilder()
                    stringBuilder.append(resources.getString(R.string.qrName))
                        .append(deCodeString(userDetails.name!!))
                        .append(System.lineSeparator())
                    stringBuilder.append(resources.getString(R.string.qrCardType))
                        .append(userDetails.cardType).append(System.lineSeparator())
                    stringBuilder.append(resources.getString(R.string.qrPanNumber))
                        .append(deCodeString(userDetails.pannumber!!))
                        .append(System.lineSeparator())
                    stringBuilder.append(resources.getString(R.string.qrDOB))
                        .append(deCodeString(userDetails.dob!!))
                        .append(System.lineSeparator())
                    qrBitmap =
                        ScanUtil.buildBitmap(stringBuilder.toString(), type, width, height, options)
                    qRcodmodel.businesscardUserDetailsList = businessCardEntityList
                    qRcodmodel.bitmap = qrBitmap
                    dataset.add(qRcodmodel)
                } else {
                    userDetails.id = tasks[i]!!.id
                    userDetails.name = tasks[i]!!.name
                    userDetails.accountnumber = tasks[i]!!.accountnumber
                    userDetails.cardType = tasks[i]!!.cardType
                    userDetails.issuer = tasks[i]!!.issuer
                    userDetails.expirydate = tasks[i]!!.expirydate
                    userDetails.banktype = tasks[i]!!.banktype
                    userDetails.bankorganization = tasks[i]!!.bankorganization
                    userDetails.image = tasks[i]!!.image
                    bankCardEntityList.add(userDetails)
                    stringBuilder = StringBuilder()
                    stringBuilder.append(resources.getString(R.string.qrName))
                        .append(deCodeString(userDetails.name!!))
                        .append(System.lineSeparator())
                    stringBuilder.append(resources.getString(R.string.qrAcNumber))
                        .append(deCodeString(userDetails.accountnumber!!))
                        .append(System.lineSeparator())
                    stringBuilder.append(resources.getString(R.string.qrCardType))
                        .append(userDetails.cardType).append(System.lineSeparator())
                    stringBuilder.append(resources.getString(R.string.qrIssuer))
                        .append(userDetails.issuer).append(System.lineSeparator())
                    stringBuilder.append(resources.getString(R.string.qrBankType))
                        .append(userDetails.banktype).append(System.lineSeparator())
                    stringBuilder.append(resources.getString(R.string.qrExpiryDate))
                        .append(deCodeString(userDetails.expirydate!!))
                        .append(System.lineSeparator())
                    stringBuilder.append(resources.getString(R.string.qrBankOrganization))
                        .append(
                            deCodeString(userDetails.bankorganization!!)
                        ).append(System.lineSeparator())
                    qrBitmap =
                        ScanUtil.buildBitmap(stringBuilder.toString(), type, width, height, options)
                    qRcodmodel.businesscardUserDetailsList = bankCardEntityList
                    qRcodmodel.bitmap = qrBitmap
                    dataset1.add(qRcodmodel)
                }
            }
        } catch (e: WriterException) {
        }
        initRecyclerview(dataset, dataset1, cardType)
    }

    private val allCards: List<BusinessCardEntity?>?
        get() = getInstance(applicationContext)
            ?.appDatabase
            ?.businessCardEntityDao()!!.all

    /*Initialize Generlcards and Bankcard recyclerview**/
    private fun initRecyclerview(
        dataset: ArrayList<QRcodmodel>?,
        dataset1: ArrayList<QRcodmodel>?,
        cardType: String?
    ) {
        if (cardType.equals(
                Constants.BANKCARD,
                ignoreCase = true
            )
        ) {
            if (dataset1 != null && dataset1.size > 0) {
                val mBankCardListAdapter = BankCardListAdapter(this, dataset1)
                mBankCardListAdapter.setmBankAdapterCallback(this)
                mActivityQrCodeGeneratorMainBinding!!.bankcardRecyclerview.visibility = View.VISIBLE
                mActivityQrCodeGeneratorMainBinding!!.bankcardRecyclerview.setHasFixedSize(true)
                mActivityQrCodeGeneratorMainBinding!!.bankcardRecyclerview.layoutManager =
                    LinearLayoutManager(this)
                mActivityQrCodeGeneratorMainBinding!!.bankcardRecyclerview.adapter =
                    mBankCardListAdapter
            } else {
                mActivityQrCodeGeneratorMainBinding!!.liListempty.visibility = View.VISIBLE
                mActivityQrCodeGeneratorMainBinding!!.relBottomScan.visibility = View.GONE
            }
        } else {
            if (dataset != null && dataset.size > 0) {
                val mAdapter = GeneralcardListAdapter(this, dataset)
                mAdapter.setmAdapterCallback(this)
                mActivityQrCodeGeneratorMainBinding!!.recyclerView.visibility = View.VISIBLE
                mActivityQrCodeGeneratorMainBinding!!.recyclerView.setHasFixedSize(true)
                mActivityQrCodeGeneratorMainBinding!!.recyclerView.layoutManager =
                    LinearLayoutManager(this)
                mActivityQrCodeGeneratorMainBinding!!.recyclerView.adapter = mAdapter
            } else {
                mActivityQrCodeGeneratorMainBinding!!.liListempty.visibility = View.VISIBLE
                mActivityQrCodeGeneratorMainBinding!!.relBottomScan.visibility = View.GONE
            }
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

    override fun onResume() {
        super.onResume()
        if (shouldExecuteOnResume) {
            datafromRoomdb
        } else {
            shouldExecuteOnResume = true
        }
    }

    override fun onMethodCallback(id: Int) {
        @SuppressLint("StaticFieldLeak")
        class DeleteTask :
            AsyncTask<Void?, Void?, Int>() {
            protected override fun doInBackground(vararg voids: Void?): Int {
                getInstance(applicationContext)!!.appDatabase
                    ?.businessCardEntityDao()
                    ?.deleteById(id)
                return 0
            }

            override fun onPostExecute(aVoid: Int) {
                super.onPostExecute(aVoid)
                if (IS_DELETED) {
                    mActivityQrCodeGeneratorMainBinding!!.liListempty.visibility = View.VISIBLE
                    mActivityQrCodeGeneratorMainBinding!!.relBottomScan.visibility = View.GONE
                }
            }
        }

        val st = DeleteTask()
        st.execute()
    }
}