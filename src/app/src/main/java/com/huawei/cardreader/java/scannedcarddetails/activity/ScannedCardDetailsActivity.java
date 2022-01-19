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

package com.huawei.cardreader.java.scannedcarddetails.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;

import com.huawei.cardreader.R;
import com.huawei.cardreader.databinding.ActivityProfileDetailBinding;
import com.huawei.cardreader.java.scannedcardlist.roomdb.BusinessCardEntity;
import com.huawei.cardreader.java.scannedcardlist.roomdb.DatabaseClient;
import com.huawei.cardreader.java.userauthentication.activity.AuthenticationPage;
import com.huawei.cardreader.java.utils.Constants;
import com.huawei.cardreader.java.utils.DataConverter;
import com.huawei.cardreader.java.utils.bankcardviewutils.BankCardNumberFormat;
import com.huawei.cardreader.java.utils.bankcardviewutils.BankCardType;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static android.Manifest.permission.CALL_PHONE;
import static com.huawei.cardreader.java.utils.Constants.ADDRESS;
import static com.huawei.cardreader.java.utils.Constants.ID;
import static com.huawei.cardreader.java.utils.Constants.IS_DELETED_FROM_PROFILEDETAILS;
import static com.huawei.cardreader.java.utils.Constants.QRCODECARDTYPE;
import static com.huawei.cardreader.java.utils.Constants.QRCODEIMAGE;
import static com.huawei.cardreader.java.utils.Constants.SMSBODY;

/**
 * The type Profile detail activity.
 */
public class ScannedCardDetailsActivity extends AppCompatActivity {
    private static final int CONTACTS_PERMISSION = 5;
    private Bitmap qrCodeImage;
    private BusinessCardEntity businessCardEntity = null;
    private ActivityProfileDetailBinding mActivityProfileDetailBinding;
    private int id;
    private Boolean isActionUser = false;
    private Boolean isUpdateBtnClick;
    private String qrCodeCardType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityProfileDetailBinding = DataBindingUtil.setContentView
                (this, R.layout.activity_profile_detail);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        initComponent();
    }

    /* Initialize component and fetch datas from local room db */
    private void initComponent() {

        mActivityProfileDetailBinding.setActivity1(ScannedCardDetailsActivity.this);
        if (getIntent().getParcelableExtra(QRCODEIMAGE) != null && getIntent().getIntExtra
                (ID, 0) != 0 && getIntent().getStringExtra(QRCODECARDTYPE)!=null) {
            qrCodeImage = getIntent().getParcelableExtra(QRCODEIMAGE);
            id = getIntent().getIntExtra(ID, 0);
            qrCodeCardType = getIntent().getStringExtra(QRCODECARDTYPE);
        }
        dataFromRoomDB(id);

        mActivityProfileDetailBinding.relPhone.setOnClickListener(view -> {
            if (businessCardEntity.getMobileno() != null
                    && !businessCardEntity.getMobileno().isEmpty()) {
                callPhone(businessCardEntity.getMobileno());
            } else {
                callPhone(mActivityProfileDetailBinding.phnoEdittext.getText().toString());
            }
        });
        mActivityProfileDetailBinding.relEmail.setOnClickListener(view -> {
            if (businessCardEntity.getEmailid() != null
                    && !businessCardEntity.getEmailid().isEmpty()) {
                emailsend(businessCardEntity.getEmailid());
            } else {
                emailsend(mActivityProfileDetailBinding.emailEdittext.getText().toString());
            }
        });
        mActivityProfileDetailBinding.relSms.setOnClickListener(view -> {
            if (businessCardEntity.getMobileno() != null
                    && !businessCardEntity.getMobileno().isEmpty()) {
                smsSend(businessCardEntity.getMobileno());
            } else {
                smsSend(mActivityProfileDetailBinding.phnoEdittext.getText().toString());
            }
        });
        mActivityProfileDetailBinding.relQrshare.setOnClickListener(view -> convertImageToPDF());
        mActivityProfileDetailBinding.imgQrshare1.setOnClickListener(view -> convertImageToPDF());
        mActivityProfileDetailBinding.imgQrshare2.setOnClickListener(view -> convertImageToPDF());
    }

    /**
     * Fetching the scanned card details  from local room db
     *
     * @param id the type int
     */
    private void dataFromRoomDB(int id) {
        @SuppressLint("StaticFieldLeak")
        class GetTasks extends AsyncTask<Void, Void, List<BusinessCardEntity>> {
            @Override
            protected List<BusinessCardEntity> doInBackground(Void... voids) {
                return DatabaseClient
                        .getInstance(getApplicationContext())
                        .getAppDatabase()
                        .businessCardEntityDao()
                        .get(id);
            }

            @Override
            protected void onPostExecute(List<BusinessCardEntity> tasks) {
                super.onPostExecute(tasks);
                setBusinessCardDetails(tasks);
            }
        }

        GetTasks gt = new GetTasks();
        gt.execute();
    }

    private void setBusinessCardDetails(List<BusinessCardEntity> tasks) {
        for (int i = 0; i < tasks.size(); i++) {
            businessCardEntity = new BusinessCardEntity();
            if (tasks.get(i).getCardType().equalsIgnoreCase(Constants.BUSINESS_CARD)) {
                businessCardEntity.setName(tasks.get(i).getName());
                businessCardEntity.setCompanyname(tasks.get(i).getCompanyname());
                businessCardEntity.setMobileno(tasks.get(i).getMobileno());
                businessCardEntity.setAddress(tasks.get(i).getAddress());
                businessCardEntity.setEmailid(tasks.get(i).getEmailid());
                businessCardEntity.setWebsite(tasks.get(i).getWebsite());
                businessCardEntity.setJobtitle(tasks.get(i).getJobtitle());
                businessCardEntity.setCardType(tasks.get(i).getCardType());
                businessCardEntity.setImage(tasks.get(i).getImage());
            } else if (tasks.get(i).getCardType().equalsIgnoreCase(Constants.AADHARCARD)) {
                businessCardEntity.setName(tasks.get(i).getName());
                businessCardEntity.setAddress(tasks.get(i).getAddress());
                businessCardEntity.setCardType(tasks.get(i).getCardType());
                businessCardEntity.setAadharid(tasks.get(i).getAadharid());
                businessCardEntity.setDob(tasks.get(i).getDob());
                businessCardEntity.setImage(tasks.get(i).getImage());
            } else if (tasks.get(i).getCardType().equalsIgnoreCase(Constants.PANCARD)) {
                businessCardEntity.setName(tasks.get(i).getName());
                businessCardEntity.setAddress(tasks.get(i).getAddress());
                businessCardEntity.setCardType(tasks.get(i).getCardType());
                businessCardEntity.setPannumber(tasks.get(i).getPannumber());
                businessCardEntity.setImage(tasks.get(i).getImage());
                businessCardEntity.setDob(tasks.get(i).getDob());
            } else {
                businessCardEntity.setName(tasks.get(i).getName());
                businessCardEntity.setAccountnumber(tasks.get(i).getAccountnumber());
                businessCardEntity.setIssuer(tasks.get(i).getIssuer());
                businessCardEntity.setExpirydate(tasks.get(i).getExpirydate());
                businessCardEntity.setBanktype(tasks.get(i).getBanktype());
                businessCardEntity.setBankorganization(tasks.get(i).getBankorganization());
                businessCardEntity.setCardType(tasks.get(i).getCardType());
                businessCardEntity.setImage(tasks.get(i).getImage());
            }
        }
        mActivityProfileDetailBinding.setBusinessCardModel1(businessCardEntity);
        setDataToUI(businessCardEntity);
    }

    private void setDataToUI(BusinessCardEntity businessCardEntity) {
        if (businessCardEntity.getCardType().equalsIgnoreCase(Constants.BUSINESS_CARD)) {
            setValueForBusinessCard(businessCardEntity);
        } else if (businessCardEntity.getCardType().equalsIgnoreCase(Constants.AADHARCARD)) {
            setValueForAadharCard(businessCardEntity);
        } else if (businessCardEntity.getCardType().equalsIgnoreCase(Constants.PANCARD)) {
            setValueForPanCard(businessCardEntity);
        } else {
            setValueForBankCard(businessCardEntity);
        }
    }

    private void setValueForBankCard(BusinessCardEntity businessCardEntity) {
        mActivityProfileDetailBinding.liBankcardProfile.setVisibility(View.VISIBLE);
        mActivityProfileDetailBinding.textViewBankcardname.setText
                (businessCardEntity.getName());
        mActivityProfileDetailBinding.bankcardNumberTextView.setText
                (DataConverter.deCodeString(businessCardEntity.getAccountnumber()));
        mActivityProfileDetailBinding.areaBankcardTextView.setText
                (DataConverter.deCodeString(businessCardEntity.getExpirydate()));
        mActivityProfileDetailBinding.bankcardImgCard.setImageBitmap
                (DataConverter.stringToBitMap(businessCardEntity.getImage()));
        mActivityProfileDetailBinding.txtBankType.setText
                (DataConverter.deCodeString(businessCardEntity.getBankorganization()));
        mActivityProfileDetailBinding.card1.setBankCardName(businessCardEntity.getName());
        mActivityProfileDetailBinding.card1.setBankCardExpiryDate(DataConverter.deCodeString
                (businessCardEntity.getExpirydate()));
        mActivityProfileDetailBinding.card1.setBankCardNumber
                (DataConverter.deCodeString(businessCardEntity.getAccountnumber()));
        mActivityProfileDetailBinding.card1.setBankCardNumberFormat(BankCardNumberFormat.ALL_DIGITS_FORMAT);
        mActivityProfileDetailBinding.card1.setBankCardType(BankCardType.AUTO);
        mActivityProfileDetailBinding.card1.setBankCardBackBackground
                (R.drawable.cardbackground_sky);
    }

    private void setValueForPanCard(BusinessCardEntity businessCardEntity) {
        mActivityProfileDetailBinding.liPancardProfile.setVisibility(View.VISIBLE);
        mActivityProfileDetailBinding.textViewPancardname.setText(DataConverter.deCodeString
                (businessCardEntity.getName()));
        mActivityProfileDetailBinding.pancardNumberTextView.setText
                (DataConverter.deCodeString(businessCardEntity.getPannumber()));
        mActivityProfileDetailBinding.areaPancardTextView.setText
                (businessCardEntity.getAddress());
        mActivityProfileDetailBinding.pancardImgCard.setImageBitmap
                (DataConverter.stringToBitMap(businessCardEntity.getImage()));
        mActivityProfileDetailBinding.imgQrshare2.setImageBitmap(qrCodeImage);
        mActivityProfileDetailBinding.textViewPancarddob.setText(DataConverter.deCodeString
                (businessCardEntity.getDob()));
    }

    private void setValueForAadharCard(BusinessCardEntity businessCardEntity) {
        mActivityProfileDetailBinding.liAadharcardProfile.setVisibility(View.VISIBLE);
        mActivityProfileDetailBinding.textViewAadharname.setText(DataConverter.deCodeString
                (businessCardEntity.getName()));
        mActivityProfileDetailBinding.aadharNumberTextView.setText
                (DataConverter.deCodeString(businessCardEntity.getAadharid()));
        mActivityProfileDetailBinding.areaAadharTextView.setText
                (businessCardEntity.getAddress());
        mActivityProfileDetailBinding.aadharImgCard.setImageBitmap
                (DataConverter.stringToBitMap(businessCardEntity.getImage()));
        mActivityProfileDetailBinding.imgQrshare1.setImageBitmap(qrCodeImage);
        mActivityProfileDetailBinding.textViewAadhardob.setText(DataConverter.deCodeString
                (businessCardEntity.getDob()));
    }

    private void setValueForBusinessCard(BusinessCardEntity businessCardEntity) {
        mActivityProfileDetailBinding.liBusinesscardProfile.setVisibility(View.VISIBLE);
        if ((businessCardEntity.getName() != null
                && !businessCardEntity.getName().isEmpty()) &&
                (businessCardEntity.getMobileno() != null
                        && !businessCardEntity.getMobileno().isEmpty()) &&
                (businessCardEntity.getEmailid() != null
                        && !businessCardEntity.getEmailid().isEmpty())) {
            mActivityProfileDetailBinding.btnUpdate.setVisibility(View.GONE);
        } else {
            mActivityProfileDetailBinding.btnUpdate.setVisibility(View.VISIBLE);
        }
        if (businessCardEntity.getName() != null && !businessCardEntity.getName().isEmpty()) {
            mActivityProfileDetailBinding.nameTextView.setText(DataConverter.deCodeString
                    (businessCardEntity.getName()));

        } else {
            mActivityProfileDetailBinding.nameTextView.setVisibility(View.GONE);
            mActivityProfileDetailBinding.nameEdittext.setVisibility(View.VISIBLE);
            businessCardEntity.setName
                    (mActivityProfileDetailBinding.nameEdittext.getText().toString());
        }
        if (businessCardEntity.getMobileno() != null
                && !businessCardEntity.getMobileno().isEmpty()) {
            mActivityProfileDetailBinding.phnoTextView.setText
                    (DataConverter.deCodeString(businessCardEntity.getMobileno()));
        } else {
            mActivityProfileDetailBinding.phnoTextView.setVisibility(View.GONE);
            mActivityProfileDetailBinding.liPhoneBottom.setVisibility(View.GONE);
            mActivityProfileDetailBinding.phnoEdittext.setVisibility(View.VISIBLE);
            businessCardEntity.setMobileno
                    (mActivityProfileDetailBinding.phnoEdittext.getText().toString());
        }
        if (businessCardEntity.getEmailid() != null
                && !businessCardEntity.getEmailid().isEmpty()) {
            mActivityProfileDetailBinding.txtEmail1.setText(DataConverter.deCodeString
                    (businessCardEntity.getEmailid()));
        } else {
            mActivityProfileDetailBinding.txtEmail1.setVisibility(View.GONE);
            mActivityProfileDetailBinding.txtLabelEmail.setVisibility(View.GONE);
            mActivityProfileDetailBinding.emailEdittext.setVisibility(View.VISIBLE);
            businessCardEntity.setEmailid
                    (mActivityProfileDetailBinding.emailEdittext.getText().toString());
        }
        if (businessCardEntity.getCompanyname() != null
                && !businessCardEntity.getCompanyname().isEmpty()) {
            mActivityProfileDetailBinding.companyTextView.setText(DataConverter.deCodeString
                    (businessCardEntity.getCompanyname
                    ()));
            mActivityProfileDetailBinding.txtCompany1.setText(DataConverter.deCodeString
                    (businessCardEntity.getCompanyname()));
        } else {
            mActivityProfileDetailBinding.companyTextView.setVisibility(View.GONE);
            mActivityProfileDetailBinding.liCompanyBottom.setVisibility(View.GONE);
        }
        setExtraBusinessCardFieldValues(businessCardEntity);
    }

    private void setExtraBusinessCardFieldValues(BusinessCardEntity businessCardEntity) {
        mActivityProfileDetailBinding.areaTextView.setText(businessCardEntity.getAddress());
        mActivityProfileDetailBinding.jobtitleTextView.setText(businessCardEntity.getJobtitle());
        mActivityProfileDetailBinding.imgCard.setImageBitmap
                (DataConverter.stringToBitMap(businessCardEntity.getImage()));
        mActivityProfileDetailBinding.imgQrshare.setImageBitmap(qrCodeImage);
        mActivityProfileDetailBinding.txtPhone1.setText(DataConverter.deCodeString
                (businessCardEntity.getMobileno()));
        mActivityProfileDetailBinding.txtWebsite1.setText(businessCardEntity.getWebsite());
    }

    /**
     * Send the SMS to the particula phonenumber which is from scanned business card
     *
     * @param phNo the type String
     */
    private void smsSend(String phNo) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.putExtra(ADDRESS, phNo);
            intent.putExtra(SMSBODY, getString(R.string.hi));
            intent.setData(Uri.parse(getString(R.string.smsParse)));
            startActivity(intent);
        } catch (android.content.ActivityNotFoundException anfe) {
            throw anfe;
        }
    }

    /**
     * Send the mail to the particula mailID which is from scanned business card
     *
     * @param email the type String
     */
    private void emailsend(String email) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setType(getString(R.string.intentEmailType));
        intent.putExtra(Intent.EXTRA_EMAIL, email);
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.subject));
        startActivity(Intent.createChooser(intent, getString(R.string.sendMailUsing)));
    }

    /**
     * Directly call the person through phone dialer
     *
     * @param phNo type String
     */
    private void callPhone(String phNo) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse(getString(R.string.tel) + phNo));
        if (ContextCompat.checkSelfPermission(getApplicationContext(), CALL_PHONE)
                == PackageManager.PERMISSION_GRANTED) {
            startActivity(intent);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{CALL_PHONE}, 1);
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
    private void addtoContatcts(String name, String phno, String email) {

        String readContactsPermission = getString(R.string.readContactPermission);
        String[] permissions = {readContactsPermission};
        Intent intent = new Intent(ContactsContract.Intents.Insert.ACTION);
        intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);
        if (ContextCompat.checkSelfPermission
                (ScannedCardDetailsActivity.this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, permissions, CONTACTS_PERMISSION);

        } else {
            if (isContactExit(phno)) {
                Toast.makeText(getApplicationContext
                        (), R.string.numberexist, Toast.LENGTH_LONG).show();
            } else {
                if (intent.resolveActivity(this.getPackageManager()) != null) {
                    intent.putExtra(ContactsContract.Intents.Insert.NAME, name);
                    intent.putExtra(ContactsContract.Intents.Insert.PHONE, phno);
                    intent.putExtra(ContactsContract.Intents.Insert.EMAIL, email);
                    this.startActivity(intent);
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
    private boolean isContactExit(String phno) {
        if (phno != null) {
            Uri lookupUri = Uri.withAppendedPath
                    (ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phno));
            String[] mPhoneNumberProjection = {ContactsContract.PhoneLookup.NUMBER};
            try (Cursor cur = this.getContentResolver().query
                    (lookupUri, mPhoneNumberProjection, null, null,
                            null)) {
                if (cur != null && cur.moveToFirst()) {
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * Convert image to pdf.
     */
    public void convertImageToPDF() {
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay();
        DisplayMetrics displaymetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);

        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder
                (qrCodeImage.getWidth(), qrCodeImage.getHeight(), 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        Paint paint = new Paint();
        paint.setColor(Color.parseColor(getString(R.string.whiteColor)));
        canvas.drawPaint(paint);
        qrCodeImage = Bitmap.createScaledBitmap
                (qrCodeImage, qrCodeImage.getWidth(), qrCodeImage.getHeight(), true);

        paint.setColor(Color.BLUE);
        canvas.drawBitmap(qrCodeImage, 0, 0, null);
        document.finishPage(page);
        String targetPdf = getString(R.string.targetPdf);
        File filePath = new File(targetPdf);
        try {
            document.writeTo(new FileOutputStream(filePath));
        } catch (IOException e) {
            Toast.makeText(this, R.string.something_wrong + e.toString
                    (), Toast.LENGTH_LONG).show();
        }
        document.close();
        File newFile = new File(getString(R.string.sdcardPath), getString(R.string.testPdf));
        Uri contentUri = FileProvider.getUriForFile
                (getApplicationContext(), getString(R.string.fileProvider), newFile);
        Intent share = new Intent();
        share.setAction(Intent.ACTION_SEND);
        share.setType(getString(R.string.applicationPdf));
        share.putExtra(Intent.EXTRA_STREAM, contentUri);
        startActivity(Intent.createChooser(share, getString(R.string.sharePdf)));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        MenuItem userItem = menu.findItem(R.id.action_user);
        if (qrCodeCardType.equals(Constants.BUSINESS_CARD)) {
            userItem.setVisible(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int menuId = item.getItemId();
        if (menuId == android.R.id.home) {
            onBackPressed();
            return true;
        }
        if (menuId == R.id.action_user) {
            addPhoneContacts();
            return true;
        }
        if (menuId == R.id.action_delete) {
            showDeleteDialogue();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addPhoneContacts() {
        isUpdateBtnClick = false;
        String name = null;
        String phno = null;
        String email = null;
        if (mActivityProfileDetailBinding.nameTextView.getText().toString().isEmpty()) {
            if (mActivityProfileDetailBinding.nameEdittext.getText().toString().isEmpty()) {
                Toast.makeText(this, R.string.contact_enter_name, Toast.LENGTH_LONG).show();
            } else {
                name = mActivityProfileDetailBinding.nameEdittext.getText().toString();
            }
        } else {
            name = mActivityProfileDetailBinding.nameTextView.getText().toString();
        }
        if (mActivityProfileDetailBinding.phnoTextView.getText().toString().isEmpty()) {
            if (mActivityProfileDetailBinding.phnoEdittext.getText().toString().isEmpty()) {
                Toast.makeText(this, R.string.contact_enter_phno, Toast.LENGTH_LONG).show();
            } else {
                phno = mActivityProfileDetailBinding.phnoEdittext.getText().toString();
            }
        } else {
            phno = mActivityProfileDetailBinding.phnoTextView.getText().toString();
        }
        if (mActivityProfileDetailBinding.txtEmail1.getText().toString().isEmpty()) {
            if (mActivityProfileDetailBinding.emailEdittext.getText().toString().isEmpty()) {
                Toast.makeText(this, R.string.contact_enter_email, Toast.LENGTH_LONG).show();
            } else {
                email = mActivityProfileDetailBinding.emailEdittext.getText().toString();
            }
        } else {
            email = mActivityProfileDetailBinding.txtEmail1.getText().toString();
        }
        if (name != null && !name.isEmpty() && phno != null && !phno.isEmpty() && email != null
                && !email.isEmpty()) {
            updateContactInDB(DataConverter.enCodeString(name), DataConverter.enCodeString(phno),
                    DataConverter.enCodeString(email), id);
            addtoContatcts(name, phno, email);
        }

    }

    /**
     * Update.
     *
     * @param v the v
     */
    public void update(View v) {
        isUpdateBtnClick = true;
        String name = null;
        String phno = null;
        String email = null;
        if (mActivityProfileDetailBinding.nameTextView.getText().toString().isEmpty()) {
            if (mActivityProfileDetailBinding.nameEdittext.getText().toString().isEmpty()) {
                Toast.makeText(this, R.string.contact_enter_name, Toast.LENGTH_LONG).show();
            } else {
                name = mActivityProfileDetailBinding.nameEdittext.getText().toString();
            }
        } else {
            name = mActivityProfileDetailBinding.nameTextView.getText().toString();
        }
        if (mActivityProfileDetailBinding.phnoTextView.getText().toString().isEmpty()) {
            if (mActivityProfileDetailBinding.phnoEdittext.getText().toString().isEmpty()) {
                Toast.makeText(this, R.string.contact_enter_phno, Toast.LENGTH_LONG).show();
            } else {
                phno = mActivityProfileDetailBinding.phnoEdittext.getText().toString();
            }
        } else {
            phno = mActivityProfileDetailBinding.phnoTextView.getText().toString();
        }
        if (mActivityProfileDetailBinding.txtEmail1.getText().toString().isEmpty()) {
            if (mActivityProfileDetailBinding.emailEdittext.getText().toString().isEmpty()) {
                Toast.makeText(this, R.string.contact_enter_email, Toast.LENGTH_LONG).show();
            } else {
                email = mActivityProfileDetailBinding.emailEdittext.getText().toString();
            }
        } else {
            email = mActivityProfileDetailBinding.txtEmail1.getText().toString();
        }
        if (name != null && !name.isEmpty() && phno != null && !phno.isEmpty() && email != null
                && !email.isEmpty()) {
            updateContactInDB(DataConverter.enCodeString(name), DataConverter.enCodeString
                    (phno), DataConverter.enCodeString(email), id);
        }
    }


    private void updateContactInDB(String name, String contactNo, String email, int id) {
        @SuppressLint("StaticFieldLeak")
        class UpdateTask extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... voids) {
                DatabaseClient.getInstance(getApplicationContext()).getAppDatabase()
                        .businessCardEntityDao()
                        .update(name, contactNo, email, id);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                if (isUpdateBtnClick) {
                    Toast.makeText(getApplicationContext(), R.string.updatedRecord,
                            Toast.LENGTH_LONG).show();
                }
            }
        }

        UpdateTask st = new UpdateTask();
        st.execute();
    }

    private void showDeleteDialogue() {
        new AlertDialog.Builder(this)
                .setMessage(R.string.delete_dialog)
                .setCancelable(false)
                .setPositiveButton(R.string.yes, (dialog, did) -> {
                    IS_DELETED_FROM_PROFILEDETAILS = true;
                    Intent intent = new Intent(this, AuthenticationPage.class);
                    intent.putExtra(QRCODEIMAGE, qrCodeImage);
                    intent.putExtra(ID, id);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton(R.string.no, null)
                .show();
    }
}