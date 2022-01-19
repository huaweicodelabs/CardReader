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

package com.huawei.cardreader.kotlin.scannedcarddetails.activity

import android.Manifest.permission
import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfDocument.PageInfo
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.provider.ContactsContract
import android.util.DisplayMetrics
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import com.huawei.cardreader.R
import com.huawei.cardreader.databinding.ActivityProfileDetailBinding
import com.huawei.cardreader.kotlin.scannedcardlist.roomdb.BusinessCardEntity
import com.huawei.cardreader.kotlin.scannedcardlist.roomdb.DatabaseClient.Companion.getInstance
import com.huawei.cardreader.kotlin.userauthentication.activity.AuthenticationPage
import com.huawei.cardreader.kotlin.utils.Constants
import com.huawei.cardreader.kotlin.utils.Constants.ADDRESS
import com.huawei.cardreader.kotlin.utils.Constants.ID
import com.huawei.cardreader.kotlin.utils.Constants.IS_DELETED_FROM_PROFILEDETAILS
import com.huawei.cardreader.kotlin.utils.Constants.QRCODECARDTYPE
import com.huawei.cardreader.kotlin.utils.Constants.QRCODEIMAGE
import com.huawei.cardreader.kotlin.utils.Constants.SMSBODY
import com.huawei.cardreader.kotlin.utils.DataConverter.deCodeString
import com.huawei.cardreader.kotlin.utils.DataConverter.enCodeString
import com.huawei.cardreader.kotlin.utils.DataConverter.stringToBitMap
import com.huawei.cardreader.java.utils.bankcardviewutils.BankCardNumberFormat;
import com.huawei.cardreader.java.utils.bankcardviewutils.BankCardType;
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

/**
 * The type Profile detail activity.
 */
class ScannedCardDetailsActivity : AppCompatActivity() {
    private lateinit var qrCodeImage: Bitmap
    private lateinit var qrCodeCardType: String
    private var businessCardEntity: BusinessCardEntity? = null
    private var mActivityProfileDetailBinding: ActivityProfileDetailBinding? =
            null
    private var id = 0
    private var isActionUser: Boolean? = null
    private var isUpdateBtnClick: Boolean? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivityProfileDetailBinding =
                DataBindingUtil.setContentView(this, R.layout.activity_profile_detail)
        Objects.requireNonNull(supportActionBar)?.setDisplayHomeAsUpEnabled(true)
        initComponent()
    }

    /* Initialize component and fetch datas from local room db**/
    private fun initComponent() {
        mActivityProfileDetailBinding!!.businessCardModel = businessCardEntity
        mActivityProfileDetailBinding!!.activity = this@ScannedCardDetailsActivity
        if (intent.getParcelableExtra<Parcelable?>(QRCODEIMAGE) != null && intent.getIntExtra(
                        ID,
                        0
                ) != 0 && intent.getStringExtra(QRCODECARDTYPE) != null
        ) {
            qrCodeImage = intent.getParcelableExtra(QRCODEIMAGE)
            id = intent.getIntExtra(ID, 0)
            qrCodeCardType = intent.getStringExtra(QRCODECARDTYPE)
        }
        dataFromRoomDB(id)
        mActivityProfileDetailBinding!!.relPhone.setOnClickListener { view: View? ->
            if (businessCardEntity!!.mobileno != null
                    && !businessCardEntity!!.mobileno!!.isEmpty()
            ) {
                callPhone(businessCardEntity!!.mobileno)
            } else {
                callPhone(mActivityProfileDetailBinding!!.phnoEdittext.text.toString())
            }
        }
        mActivityProfileDetailBinding!!.relEmail.setOnClickListener { view: View? ->
            if (businessCardEntity!!.emailid != null
                    && !businessCardEntity!!.emailid!!.isEmpty()
            ) {
                emailsend(businessCardEntity!!.emailid)
            } else {
                emailsend(mActivityProfileDetailBinding!!.emailEdittext.text.toString())
            }
        }
        mActivityProfileDetailBinding!!.relSms.setOnClickListener { view: View? ->
            if (businessCardEntity!!.mobileno != null
                    && !businessCardEntity!!.mobileno!!.isEmpty()
            ) {
                smsSend(businessCardEntity!!.mobileno)
            } else {
                smsSend(mActivityProfileDetailBinding!!.phnoEdittext.text.toString())
            }
        }
        mActivityProfileDetailBinding!!.relQrshare.setOnClickListener { view: View? ->
            convertImageToPDF() }
        mActivityProfileDetailBinding!!.imgQrshare1.setOnClickListener { view: View? ->
            convertImageToPDF() }
        mActivityProfileDetailBinding!!.imgQrshare2.setOnClickListener { view: View? ->
            convertImageToPDF() }
    }

    /**
     * Fetching the scanned card details  from local room db
     *
     * @param id the type int
     */
    private fun dataFromRoomDB(id: Int) {
        @SuppressLint("StaticFieldLeak")
        class GetTasks :
                AsyncTask<Void?, Void?, List<BusinessCardEntity?>?>() {
            protected override fun doInBackground(vararg voids: Void?): List<BusinessCardEntity?>? {
                return getInstance(applicationContext)
                        ?.appDatabase
                        ?.businessCardEntityDao()
                        ?.get(id)
            }

            override fun onPostExecute(tasks: List<BusinessCardEntity?>?) {
                super.onPostExecute(tasks)
                setBusinessCardDetails(tasks)
            }
        }

        val gt = GetTasks()
        gt.execute()
    }

    private fun setBusinessCardDetails(tasks: List<BusinessCardEntity?>?) {
        for (i in tasks!!.indices) {
            businessCardEntity = BusinessCardEntity()
            if (tasks[i]!!.cardType.equals(
                            Constants.BUSINESS_CARD,
                            ignoreCase = true
                    )
            ) {
                businessCardEntity!!.name = tasks[i]!!.name
                businessCardEntity!!.companyname = tasks[i]!!.companyname
                businessCardEntity!!.mobileno = tasks[i]!!.mobileno
                businessCardEntity!!.address = tasks[i]!!.address
                businessCardEntity!!.emailid = tasks[i]!!.emailid
                businessCardEntity!!.website = tasks[i]!!.website
                businessCardEntity!!.jobtitle = tasks[i]!!.jobtitle
                businessCardEntity!!.cardType = tasks[i]!!.cardType
                businessCardEntity!!.image = tasks[i]!!.image
            } else if (tasks[i]!!.cardType.equals(
                            Constants.AADHARCARD,
                            ignoreCase = true
                    )
            ) {
                businessCardEntity!!.name = tasks[i]!!.name
                businessCardEntity!!.address = tasks[i]!!.address
                businessCardEntity!!.cardType = tasks[i]!!.cardType
                businessCardEntity!!.aadharid = tasks[i]!!.aadharid
                businessCardEntity!!.dob = tasks[i]!!.dob
                businessCardEntity!!.image = tasks[i]!!.image
            } else if (tasks[i]!!.cardType.equals(
                            Constants.PANCARD,
                            ignoreCase = true
                    )
            ) {
                businessCardEntity!!.name = tasks[i]!!.name
                businessCardEntity!!.address = tasks[i]!!.address
                businessCardEntity!!.cardType = tasks[i]!!.cardType
                businessCardEntity!!.pannumber = tasks[i]!!.pannumber
                businessCardEntity!!.image = tasks[i]!!.image
                businessCardEntity!!.dob = tasks[i]!!.dob
            } else {
                businessCardEntity!!.name = tasks[i]!!.name
                businessCardEntity!!.accountnumber = tasks[i]!!.accountnumber
                businessCardEntity!!.issuer = tasks[i]!!.issuer
                businessCardEntity!!.expirydate = tasks[i]!!.expirydate
                businessCardEntity!!.banktype = tasks[i]!!.banktype
                businessCardEntity!!.bankorganization = tasks[i]!!.bankorganization
                businessCardEntity!!.cardType = tasks[i]!!.cardType
                businessCardEntity!!.image = tasks[i]!!.image
            }
        }
        mActivityProfileDetailBinding!!.businessCardModel = businessCardEntity
        setDataToUI(businessCardEntity)
    }

    private fun setDataToUI(businessCardEntity: BusinessCardEntity?) {

        if (businessCardEntity?.cardType.equals(
                        Constants.BUSINESS_CARD,
                        ignoreCase = true
                )
        ) {
            setValueForBusinessCard(businessCardEntity)
        } else if (businessCardEntity?.cardType.equals(
                        Constants.AADHARCARD,
                        ignoreCase = true
                )
        ) {

            setValueForAadharCard(businessCardEntity)
        } else if (businessCardEntity?.cardType.equals(
                        Constants.PANCARD,
                        ignoreCase = true
                )
        ) {

            setValueForPanCard(businessCardEntity)
        } else {
            setValueForBankCard(businessCardEntity)
        }

    }

    private fun setValueForBankCard(businessCardEntity: BusinessCardEntity?) {
        mActivityProfileDetailBinding!!.liBankcardProfile.visibility = View.VISIBLE
        mActivityProfileDetailBinding!!.textViewBankcardname.text = businessCardEntity!!.name
        mActivityProfileDetailBinding!!.bankcardNumberTextView.text = deCodeString(
                businessCardEntity.accountnumber!!
        )

        mActivityProfileDetailBinding!!.areaBankcardTextView.setText(
                deCodeString(
                        businessCardEntity.expirydate!!
                )
        )
        mActivityProfileDetailBinding!!.bankcardImgCard.setImageBitmap(
                stringToBitMap(
                        businessCardEntity.image
                )
        )
        mActivityProfileDetailBinding!!.txtBankType.setText(
                deCodeString(
                        businessCardEntity.bankorganization!!
                )
        )
        mActivityProfileDetailBinding!!.card1.setBankCardName(businessCardEntity.name)
        mActivityProfileDetailBinding!!.card1.setBankCardExpiryDate(
                deCodeString(
                        businessCardEntity.expirydate!!
                )
        )
        mActivityProfileDetailBinding!!.card1.setBankCardNumber(
                deCodeString(
                        businessCardEntity.accountnumber!!
                )
        )
        mActivityProfileDetailBinding!!.card1.bankCardNumberFormat = BankCardNumberFormat.ALL_DIGITS_FORMAT
        mActivityProfileDetailBinding!!.card1.setBankCardType(BankCardType.AUTO)
        mActivityProfileDetailBinding!!.card1.setBankCardBackBackground(R.drawable.cardbackground_sky)
    }

    private fun setValueForPanCard(businessCardEntity: BusinessCardEntity?) {
        mActivityProfileDetailBinding!!.liPancardProfile.visibility = View.VISIBLE
        mActivityProfileDetailBinding!!.textViewPancardname.text = deCodeString(
                businessCardEntity?.name!!
        )
        mActivityProfileDetailBinding!!.pancardNumberTextView.text = deCodeString(
                businessCardEntity.pannumber!!
        )
        mActivityProfileDetailBinding!!.areaPancardTextView.text=businessCardEntity.address
        mActivityProfileDetailBinding!!.pancardImgCard.setImageBitmap(
                stringToBitMap(
                        businessCardEntity.image
                )
        )
        mActivityProfileDetailBinding!!.imgQrshare2.setImageBitmap(qrCodeImage)
        mActivityProfileDetailBinding!!.textViewPancarddob.text = deCodeString(
                businessCardEntity.dob!!
        )
    }

    private fun setValueForAadharCard(businessCardEntity: BusinessCardEntity?) {
        mActivityProfileDetailBinding!!.liAadharcardProfile.visibility = View.VISIBLE
        mActivityProfileDetailBinding!!.textViewAadharname.setText(
                deCodeString(
                        businessCardEntity?.name!!
                )
        )
        mActivityProfileDetailBinding!!.aadharNumberTextView.setText(
                deCodeString(
                        businessCardEntity.aadharid!!
                )
        )
        mActivityProfileDetailBinding!!.areaAadharTextView.setText(businessCardEntity.address)
        mActivityProfileDetailBinding!!.aadharImgCard.setImageBitmap(
                stringToBitMap(
                        businessCardEntity.image
                )
        )
        mActivityProfileDetailBinding!!.imgQrshare1.setImageBitmap(qrCodeImage)
        mActivityProfileDetailBinding!!.textViewAadhardob.setText(
                deCodeString(
                        businessCardEntity.dob!!
                )
        )
    }

    private fun setValueForBusinessCard(businessCardEntity: BusinessCardEntity?) {
        mActivityProfileDetailBinding!!.liBusinesscardProfile.visibility = View.VISIBLE
        if ((businessCardEntity!!.name != null
                        && !businessCardEntity.name!!.isEmpty()) &&
                (businessCardEntity.mobileno != null
                        && !businessCardEntity.mobileno!!.isEmpty()) &&
                (businessCardEntity.emailid != null
                        && !businessCardEntity.emailid!!.isEmpty())
        ) {
            mActivityProfileDetailBinding!!.btnUpdate.visibility = View.GONE
        } else {
            mActivityProfileDetailBinding!!.btnUpdate.visibility = View.VISIBLE
        }
        if (businessCardEntity.name != null && !businessCardEntity.name!!.isEmpty()) {
            mActivityProfileDetailBinding!!.nameTextView.setText(
                    deCodeString(businessCardEntity.name!!)
            )
        } else {
            mActivityProfileDetailBinding!!.nameTextView.visibility = View.GONE
            mActivityProfileDetailBinding!!.nameEdittext.visibility = View.VISIBLE
            businessCardEntity.name = mActivityProfileDetailBinding!!.nameEdittext.text.toString()

        }
        if (businessCardEntity.mobileno != null
                && !businessCardEntity.mobileno!!.isEmpty()
        ) {
            mActivityProfileDetailBinding!!.phnoTextView.setText(
                    deCodeString(
                            businessCardEntity.mobileno!!
                    )
            )
        } else {
            mActivityProfileDetailBinding!!.phnoTextView.visibility = View.GONE
            mActivityProfileDetailBinding!!.liPhoneBottom.visibility = View.GONE
            mActivityProfileDetailBinding!!.phnoEdittext.visibility = View.VISIBLE
            businessCardEntity.mobileno =
                    mActivityProfileDetailBinding!!.phnoEdittext.text.toString()
        }
        if (businessCardEntity.emailid != null
                && !businessCardEntity.emailid!!.isEmpty()
        ) {
            mActivityProfileDetailBinding!!.txtEmail1.setText(
                    deCodeString(
                            businessCardEntity.emailid!!
                    )
            )
        } else {
            mActivityProfileDetailBinding!!.txtEmail1.visibility = View.GONE
            mActivityProfileDetailBinding!!.txtLabelEmail.visibility = View.GONE
            mActivityProfileDetailBinding!!.emailEdittext.visibility = View.VISIBLE
            businessCardEntity.emailid =
                    mActivityProfileDetailBinding!!.emailEdittext.text.toString()
        }
        if (businessCardEntity.companyname != null
                && !businessCardEntity.companyname!!.isEmpty()
        ) {
            mActivityProfileDetailBinding!!.companyTextView.setText(
                    deCodeString(
                            businessCardEntity.companyname!!
                    )
            )
            mActivityProfileDetailBinding!!.txtCompany1.setText(
                    deCodeString(
                            businessCardEntity.companyname!!
                    )
            )
        } else {
            mActivityProfileDetailBinding!!.companyTextView.visibility = View.GONE
            mActivityProfileDetailBinding!!.liCompanyBottom.visibility = View.GONE
        }
        setExtraBusinessCardFieldValues(businessCardEntity)
    }

    private fun setExtraBusinessCardFieldValues(businessCardEntity: BusinessCardEntity) {
        mActivityProfileDetailBinding!!.areaTextView.setText(businessCardEntity.address)
        mActivityProfileDetailBinding!!.jobtitleTextView.setText(businessCardEntity.jobtitle)
        mActivityProfileDetailBinding!!.imgCard.setImageBitmap(
                stringToBitMap(
                        businessCardEntity.image
                )
        )
        mActivityProfileDetailBinding!!.imgQrshare.setImageBitmap(qrCodeImage)
        mActivityProfileDetailBinding!!.txtPhone1.setText(
                deCodeString(
                        businessCardEntity.mobileno!!
                )
        )
        mActivityProfileDetailBinding!!.txtWebsite1.setText(businessCardEntity.website)
    }

    /**
     * Send the SMS to the particula phonenumber which is from scanned business card
     *
     * @param phNo the type String
     */
    private fun smsSend(phNo: String?) {
        try {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.putExtra(ADDRESS, phNo)
            intent.putExtra(SMSBODY, getString(R.string.hi))
            intent.data = Uri.parse(getString(R.string.smsParse))
            startActivity(intent)
        } catch (anfe: ActivityNotFoundException) {
        }
    }

    /**
     * Send the mail to the particula mailID which is from scanned business card
     *
     * @param email the type String
     */
    private fun emailsend(email: String?) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.type = getString(R.string.intentEmailType)
        intent.putExtra(Intent.EXTRA_EMAIL, email)
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.subject))
        startActivity(Intent.createChooser(intent, getString(R.string.sendMailUsing)))
    }

    /**
     * Directly call the person through phone dialer
     *
     * @param phNo type String
     */
    private fun callPhone(phNo: String?) {
        val intent = Intent(Intent.ACTION_CALL)
        intent.data = Uri.parse(getString(R.string.tel) + phNo)
        if (ContextCompat.checkSelfPermission(applicationContext, permission.CALL_PHONE)
                == PackageManager.PERMISSION_GRANTED
        ) {
            startActivity(intent)
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(arrayOf(permission.CALL_PHONE), 1)
            }
        }
    }

    /**
     * This Method used to store scanned business card contactdetails in mobile contacts
     *
     * @param name  type String
     * @param phno  type String
     * @param email type String
     */
    private fun addtoContatcts(
            name: String?,
            phno: String?,
            email: String?
    ) {
        val readContactsPermission = getString(R.string.readContactPermission)
        val permissions =
                arrayOf(readContactsPermission)
        val intent = Intent(ContactsContract.Intents.Insert.ACTION)
        intent.type = ContactsContract.RawContacts.CONTENT_TYPE
        if (ContextCompat.checkSelfPermission(
                        this@ScannedCardDetailsActivity,
                        permission.READ_CONTACTS
                )
                != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                    this,
                    permissions,
                    CONTACTS_PERMISSION
            )
        } else {
            if (isContactExit(phno)) {
                Toast.makeText(applicationContext, R.string.numberexist, Toast.LENGTH_LONG)
                        .show()
            } else {
                if (intent.resolveActivity(this.packageManager) != null) {
                    intent.putExtra(ContactsContract.Intents.Insert.NAME, name)
                    intent.putExtra(ContactsContract.Intents.Insert.PHONE, phno)
                    intent.putExtra(ContactsContract.Intents.Insert.EMAIL, email)
                    this.startActivity(intent)
                }
            }
        }
    }

    /**
     * This method used to validate contact phonenumber is existed in mobile contatcs or not
     *
     * @param phno type String
     * @return boolean
     */
    private fun isContactExit(phno: String?): Boolean {
        if (phno != null) {
            val lookupUri = Uri.withAppendedPath(
                    ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                    Uri.encode(phno)
            )
            val mPhoneNumberProjection =
                    arrayOf(ContactsContract.PhoneLookup.NUMBER)
            this.contentResolver.query(
                    lookupUri, mPhoneNumberProjection, null, null,
                    null
            ).use { cur ->
                if (cur != null && cur.moveToFirst()) {
                    return true
                }
            }
        }
        return false
    }

    /**
     * Convert image to pdf.
     */
    fun convertImageToPDF() {
        val wm =
                getSystemService(Context.WINDOW_SERVICE) as WindowManager
        wm.defaultDisplay
        val displaymetrics = DisplayMetrics()
        this.windowManager.defaultDisplay.getMetrics(displaymetrics)
        val document = PdfDocument()
        val pageInfo =
                PageInfo.Builder(qrCodeImage!!.width, qrCodeImage!!.height, 1).create()
        val page = document.startPage(pageInfo)
        val canvas = page.canvas
        val paint = Paint()
        paint.color = Color.parseColor(getString(R.string.whiteColor))
        canvas.drawPaint(paint)
        qrCodeImage = Bitmap.createScaledBitmap(
                qrCodeImage!!,
                qrCodeImage!!.width,
                qrCodeImage!!.height,
                true
        )
        paint.color = Color.BLUE
        canvas.drawBitmap(qrCodeImage, 0f, 0f, null)
        document.finishPage(page)
        val targetPdf = getString(R.string.targetPdf)
        val filePath = File(targetPdf)
        try {
            document.writeTo(FileOutputStream(filePath))
        } catch (e: IOException) {
            Toast.makeText(
                    this,
                    R.string.something_wrong.toString() + e.toString(),
                    Toast.LENGTH_LONG
            ).show()
        }
        document.close()
        val newFile =
                File(getString(R.string.sdcardPath), getString(R.string.testPdf))
        val contentUri = FileProvider.getUriForFile(
                applicationContext,
                getString(R.string.fileProvider),
                newFile
        )
        val share = Intent()
        share.action = Intent.ACTION_SEND
        share.type = getString(R.string.applicationPdf)
        share.putExtra(Intent.EXTRA_STREAM, contentUri)
        startActivity(Intent.createChooser(share, getString(R.string.sharePdf)))
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_main, menu)
        val userItem = menu.findItem(R.id.action_user)
        if (qrCodeCardType == Constants.BUSINESS_CARD) {
            userItem.isVisible = true
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val menuId = item.itemId
        if (menuId == android.R.id.home) {
            onBackPressed()
            return true
        }
        if (menuId == R.id.action_user) {
            addPhoneContacts()
            return true
        }
        if (menuId == R.id.action_delete) {
            showDeleteDialogue()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun addPhoneContacts() {
        isUpdateBtnClick = false
        var name: String? = null
        var phno: String? = null
        var email: String? = null
        if (mActivityProfileDetailBinding!!.nameTextView.text.toString().isEmpty()) {
            if (mActivityProfileDetailBinding!!.nameEdittext.text.toString().isEmpty()) {
                Toast.makeText(this, R.string.contact_enter_name, Toast.LENGTH_LONG).show()
            } else {
                name = mActivityProfileDetailBinding!!.nameEdittext.text.toString()
            }
        } else {
            name = mActivityProfileDetailBinding!!.nameTextView.text.toString()
        }
        /**************************End of Name****************************************/
        if (mActivityProfileDetailBinding!!.phnoTextView.text.toString().isEmpty()) {
            if (mActivityProfileDetailBinding!!.phnoEdittext.text.toString().isEmpty()) {
                Toast.makeText(this, R.string.contact_enter_phno, Toast.LENGTH_LONG).show()
            } else {
                phno = mActivityProfileDetailBinding!!.phnoEdittext.text.toString()
            }
        } else {
            phno = mActivityProfileDetailBinding!!.phnoTextView.text.toString()
        }
        /**************************End of Phno****************************************/
        if (mActivityProfileDetailBinding!!.txtEmail1.text.toString().isEmpty()) {
            if (mActivityProfileDetailBinding!!.emailEdittext.text.toString().isEmpty()) {
                Toast.makeText(this, R.string.contact_enter_email, Toast.LENGTH_LONG).show()
            } else {
                email = mActivityProfileDetailBinding!!.emailEdittext.text.toString()
            }
        } else {
            email = mActivityProfileDetailBinding!!.txtEmail1.text.toString()
        }
        /**************************End of Email****************************************/
        if (name != null && !name.isEmpty() && phno != null && !phno.isEmpty() && email != null &&
            !email.isEmpty()) {
            updateContactInDB(enCodeString(name), enCodeString(phno),
                    enCodeString(email), id)
            addtoContatcts(name, phno, email)
        }
    }

    /**
     * Update.
     *
     * @param v the v
     */
    fun update(v: View?) {
        isUpdateBtnClick = false
        var name: String? = null
        var phno: String? = null
        var email: String? = null
        if (mActivityProfileDetailBinding!!.nameTextView.text.toString().isEmpty()) {
            if (mActivityProfileDetailBinding!!.nameEdittext.text.toString().isEmpty()) {
                Toast.makeText(this, R.string.contact_enter_name, Toast.LENGTH_LONG).show()
            } else {
                name = mActivityProfileDetailBinding!!.nameEdittext.text.toString()
            }
        } else {
            name = mActivityProfileDetailBinding!!.nameTextView.text.toString()
        }
        if (mActivityProfileDetailBinding!!.phnoTextView.text.toString().isEmpty()) {
            if (mActivityProfileDetailBinding!!.phnoEdittext.text.toString().isEmpty()) {
                Toast.makeText(this, R.string.contact_enter_phno, Toast.LENGTH_LONG).show()
            } else {
                phno = mActivityProfileDetailBinding!!.phnoEdittext.text.toString()
            }
        } else {
            phno = mActivityProfileDetailBinding!!.phnoTextView.text.toString()
        }
        if (mActivityProfileDetailBinding!!.txtEmail1.text.toString().isEmpty()) {
            if (mActivityProfileDetailBinding!!.emailEdittext.text.toString().isEmpty()) {
                Toast.makeText(this, R.string.contact_enter_email, Toast.LENGTH_LONG).show()
            } else {
                email = mActivityProfileDetailBinding!!.emailEdittext.text.toString()
            }
        } else {
            email = mActivityProfileDetailBinding!!.txtEmail1.text.toString()
        }
        if (name != null && !name.isEmpty() && phno != null && !phno.isEmpty() && email != null &&
            !email.isEmpty()) {
            updateContactInDB(enCodeString(name), enCodeString(phno),
                    enCodeString(email), id)
        }
    }

    private fun updateContactInDB(
            name: String?,
            contactNo: String?,
            email: String?,
            id: Int
    ) {
        @SuppressLint("StaticFieldLeak")
        class UpdateTask :
                AsyncTask<Void?, Void?, Void?>() {
            protected override fun doInBackground(vararg voids: Void?): Void? {
                getInstance(applicationContext)!!.appDatabase
                        ?.businessCardEntityDao()
                        ?.update(name, contactNo, email, id)
                return null
            }

            override fun onPostExecute(aVoid: Void?) {
                super.onPostExecute(aVoid)
                if (isUpdateBtnClick!!) {
                    Toast.makeText(
                            applicationContext, R.string.updatedRecord,
                            Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

        val st = UpdateTask()
        st.execute()
    }

    private fun showDeleteDialogue() {
        AlertDialog.Builder(this)
                .setMessage(R.string.delete_dialog)
                .setCancelable(false)
                .setPositiveButton(
                        R.string.yes
                ) { dialog: DialogInterface?, did: Int ->
                    IS_DELETED_FROM_PROFILEDETAILS = true
                    val intent = Intent(this, AuthenticationPage::class.java)
                    intent.putExtra(QRCODEIMAGE, qrCodeImage)
                    intent.putExtra(ID, id)
                    startActivity(intent)
                    finish()
                }
                .setNegativeButton(R.string.no, null)
                .show()
    }

    companion object {
        private const val CONTACTS_PERMISSION = 5
    }
}