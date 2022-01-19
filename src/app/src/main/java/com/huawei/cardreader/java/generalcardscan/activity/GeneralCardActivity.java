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

package com.huawei.cardreader.java.generalcardscan.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.huawei.cardreader.R;
import com.huawei.cardreader.java.generalcardscan.processor.gcr.aadharcard.AadhaarProcessing;
import com.huawei.cardreader.java.generalcardscan.processor.gcr.businesscard.GenericProcessing;
import com.huawei.cardreader.java.generalcardscan.processor.gcr.pancard.PanProcessing;
import com.huawei.cardreader.java.scannedcardlist.activity.ScannedCardListActivity;
import com.huawei.cardreader.java.scannedcardlist.roomdb.BusinessCardEntity;
import com.huawei.cardreader.java.scannedcardlist.roomdb.DatabaseClient;
import com.huawei.cardreader.java.utils.Constants;
import com.huawei.cardreader.java.utils.DataConverter;
import com.huawei.hms.common.util.Logger;
import com.huawei.hms.mlplugin.card.gcr.MLGcrCapture;
import com.huawei.hms.mlplugin.card.gcr.MLGcrCaptureConfig;
import com.huawei.hms.mlplugin.card.gcr.MLGcrCaptureFactory;
import com.huawei.hms.mlplugin.card.gcr.MLGcrCaptureResult;
import com.huawei.hms.mlplugin.card.gcr.MLGcrCaptureUIConfig;

import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

/**
 * The type General card activity.
 */
public class GeneralCardActivity extends AppCompatActivity implements View.OnClickListener {
    private Uri mImageUri;
    private static final int REQUEST_IMAGE_SELECT_FROM_ALBUM = 1000;
    private static final int REQUEST_IMAGE_CAPTURE = 1001;
    private Object object = false;
    private ImageView frontImg;
    private ImageView frontSimpleImg;
    private ImageView frontDeleteImg;
    private LinearLayout frontAddView;
    private LinearLayout aadharListResults;
    private LinearLayout businessCardList;
    private LinearLayout panCard;
    private RelativeLayout emptyImg;
    private ProgressDialog progressDialog;
    private Bitmap bitmap;
    private Bitmap mTryImageBitmap = null;
    private CardType cardTypeEnum = CardType.BusinessCard;
    private Bitmap imageBitmap;
    private String cardTypeToSave;
    private HashMap<String, String> dataMap = null;
    private EditText mContactName;
    private TextView mContactNumber;
    private TextView mContactEmail;
    private TextView mContactOrganization;
    private CardView cardview;
    private TextView name;
    private TextView aadharName;
    private TextView fatherName;
    private TextView panNumber;
    private TextView dateOfBirth;
    private TextView aadhaarNumber;
    private TextView gender;
    private TextView yearOfBirth;
    private TextView contactResults;
    /**
     * The Radio listener.
     */
    RadioGroup.OnCheckedChangeListener radioListener = (group, checkedId) -> {
        switch (checkedId) {
            case R.id.businesscard:
                updateCardType(CardType.BusinessCard);
                cardTypeToSave = Constants.BUSINESS_CARD;
                emptyImg.setVisibility(View.VISIBLE);
                break;
            case R.id.panard:
                updateCardType(CardType.PanCard);
                cardTypeToSave = Constants.PANCARD;
                emptyImg.setVisibility(View.VISIBLE);
                break;
            case R.id.aadharcard:
                updateCardType(CardType.AadharCard);
                cardTypeToSave = Constants.AADHARCARD;
                emptyImg.setVisibility(View.VISIBLE);
                break;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generalcard);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        initComponent();
    }

    /**
     * updating card type.
     *
     * @param type of CardType
     */
    private void updateCardType(CardType type) {
        if (cardTypeEnum != type) {
            cardview.setVisibility(View.GONE);
            showFrontDeleteImage();
        }
        cardTypeEnum = type;
    }

    /**
     * Init component.
     */
    void initComponent() {
        frontImg = findViewById(R.id.avatar_img);
        frontSimpleImg = findViewById(R.id.avatar_sample_img);
        frontDeleteImg = findViewById(R.id.avatar_delete);
        frontAddView = findViewById(R.id.avatar_add);
        frontAddView.setOnClickListener(this);
        frontDeleteImg.setOnClickListener(this);
        RadioGroup cardType = findViewById(R.id.card_type);
        cardType.setOnCheckedChangeListener(radioListener);
        mContactName = findViewById(R.id.contactname);
        mContactNumber = findViewById(R.id.contactnumber);
        mContactEmail = findViewById(R.id.contactemail);
        mContactOrganization = findViewById(R.id.contactogranization);
        cardTypeToSave = Constants.BUSINESS_CARD;
        aadharListResults = findViewById(R.id.aadharlistresults);
        businessCardList = findViewById(R.id.businesscardlist);
        panCard = findViewById(R.id.pancard);
        name = findViewById(R.id.name);
        aadharName = findViewById(R.id.Aadharname);
        fatherName = findViewById(R.id.fname);
        panNumber = findViewById(R.id.pan);
        dateOfBirth = findViewById(R.id.dob);
        yearOfBirth = findViewById(R.id.yob);
        aadhaarNumber = findViewById(R.id.aadharnum);
        gender = findViewById(R.id.gender);
        contactResults = findViewById(R.id.contactresults);
        cardview = findViewById(R.id.allresultslist);
        emptyImg = findViewById(R.id.emptyimg);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.avatar_add:
                detectPhoto(object, callback);
                break;
            case R.id.avatar_delete:
                showFrontDeleteImage();
                break;
            case R.id.back:
                finish();
                break;
            default:
                break;
        }
    }

    /**
     * Save card.
     *
     * @param v the v
     */
    public void saveCard(View v) {
        if ((dataMap != null && !dataMap.isEmpty() && dataMap.size() > 1) ||
                (contactResults != null && contactResults.getText() != null &&
                        !contactResults.getText().toString().isEmpty())) {
            save();
        } else {
            Toast.makeText(getApplicationContext(), R.string.pleasescancard, Toast.LENGTH_LONG).show();
        }
    }

    private void save() {
        class SaveTask extends AsyncTask<Void, Void, Void> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = new ProgressDialog(GeneralCardActivity.this);
                progressDialog.setMessage(getString(R.string.saving));
                progressDialog.setIndeterminate(false);
                progressDialog.setCancelable(false);
                progressDialog.show();
            }

            @Override
            protected Void doInBackground(Void... voids) {
                BusinessCardEntity userDetails = new BusinessCardEntity();
                if (cardTypeToSave.equalsIgnoreCase(Constants.BUSINESS_CARD)) {
                    userDetails.setName(DataConverter.enCodeString
                            (mContactName.getText().toString()));
                    userDetails.setMobileno(DataConverter.enCodeString
                            (mContactNumber.getText().toString()));
                    userDetails.setEmailid(DataConverter.enCodeString
                            (mContactEmail.getText().toString()));
                    userDetails.setCompanyname(DataConverter.enCodeString
                            (mContactOrganization.getText().toString()));
                    userDetails.setAddress("");
                    userDetails.setJobtitle("");
                    userDetails.setWebsite("");
                    userDetails.setCardType(cardTypeToSave);
                    userDetails.setImage(DataConverter.bitMapToString(bitmap));
                } else if (cardTypeToSave.equalsIgnoreCase(Constants.AADHARCARD)) {
                    userDetails.setName(DataConverter.enCodeString
                            (name.getText().toString()));
                    userDetails.setAadharid(DataConverter.enCodeString
                            (aadhaarNumber.getText().toString().trim()));
                    userDetails.setGender(DataConverter.enCodeString
                            (gender.getText().toString()));
                    userDetails.setFathername(DataConverter.enCodeString
                            (fatherName.getText().toString()));
                    userDetails.setDob(DataConverter.enCodeString
                            (yearOfBirth.getText().toString().trim().replace
                            (":", "")));
                    userDetails.setAddress("");
                    userDetails.setCardType(cardTypeToSave);
                    userDetails.setImage(DataConverter.bitMapToString(bitmap));
                } else {
                    userDetails.setName(DataConverter.enCodeString
                            (name.getText().toString()));
                    userDetails.setPannumber(DataConverter.enCodeString
                            (panNumber.getText().toString()));
                    userDetails.setFathername(DataConverter.enCodeString
                            (fatherName.getText().toString()));
                    userDetails.setDob(DataConverter.enCodeString
                            (dateOfBirth.getText().toString()));
                    userDetails.setAddress("");
                    userDetails.setCardType(cardTypeToSave);
                    userDetails.setImage(DataConverter.bitMapToString(bitmap));
                }
                DatabaseClient.getInstance(getApplicationContext()).getAppDatabase()
                        .businessCardEntityDao()
                        .insert(userDetails);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                progressDialog.dismiss();
                dataMap.clear();
                contactResults.setText("");
                frontImg.setVisibility(View.GONE);
                frontSimpleImg.setVisibility(View.VISIBLE);
                frontAddView.setVisibility(View.VISIBLE);
                frontDeleteImg.setVisibility(View.GONE);
                cardview.setVisibility(View.GONE);
                Intent intent = new Intent(getApplicationContext(), ScannedCardListActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra(Constants.CARD_TYPE_TO_SAVE, cardTypeToSave);
                startActivity(intent);
            }
        }
        SaveTask st = new SaveTask();
        st.execute();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == REQUEST_IMAGE_SELECT_FROM_ALBUM && resultCode == RESULT_OK) {
            if (intent != null) {
                mImageUri = intent.getData();
            }
            tryReloadAndDetectInImage();
        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            tryReloadAndDetectInImage();
        }
    }

    // take a picture
    private void detectPhoto(Object object, MLGcrCapture.Callback callback) {
        MLGcrCaptureConfig cardConfig = new MLGcrCaptureConfig.Factory().setLanguage(getString(R.string.en)).create();
        MLGcrCaptureUIConfig uiConfig = new MLGcrCaptureUIConfig.Factory()
                .setTipText(getResources().getString(R.string.capture_tip))
                .setOrientation(MLGcrCaptureUIConfig.ORIENTATION_AUTO).create();
        MLGcrCapture ocrManager = MLGcrCaptureFactory.getInstance().getGcrCapture(cardConfig,
                uiConfig);

        ocrManager.capturePhoto(this, object, callback);
    }

    // local image
    private void detectLocalImage(Bitmap bitmap, MLGcrCapture.Callback callback) {
        MLGcrCaptureConfig config = new MLGcrCaptureConfig.Factory().create();
        MLGcrCapture ocrManager = MLGcrCaptureFactory.getInstance().getGcrCapture(config);
        ocrManager.captureImage(bitmap, null, callback);
    }

    /* Callback Function */
    private MLGcrCapture.Callback callback = new MLGcrCapture.Callback() {
        @Override
        public int onResult(MLGcrCaptureResult result, Object o) {
            if (result == null) {
                return MLGcrCaptureResult.CAPTURE_CONTINUE;
            }
            if (cardTypeEnum == CardType.BusinessCard) {
                dataMap = new GenericProcessing().processText(result.text, getApplicationContext());
                businessOutput(dataMap);
                businessCardList.setVisibility(View.VISIBLE);
                aadharListResults.setVisibility(View.GONE);
                panCard.setVisibility(View.GONE);
                contactResults.setVisibility(View.GONE);
                if (businessCardList.getVisibility() == View.VISIBLE) {
                    emptyImg.setVisibility(View.GONE);
                }
            } else if (cardTypeEnum == CardType.PanCard) {
                dataMap = new PanProcessing().processText(result.text, getApplicationContext());
                panCard.setVisibility(View.VISIBLE);
                businessCardList.setVisibility(View.GONE);
                aadharListResults.setVisibility(View.GONE);
                if (businessCardList.getVisibility() == View.GONE) {
                    emptyImg.setVisibility(View.GONE);
                }
            } else if (cardTypeEnum == CardType.AadharCard) {
                dataMap = new AadhaarProcessing().processExtractedTextForFrontPic(result.text,
                        getApplicationContext());
                presentFrontOutput(dataMap);
                panCard.setVisibility(View.GONE);
                businessCardList.setVisibility(View.GONE);
                aadharListResults.setVisibility(View.VISIBLE);
                if (businessCardList.getVisibility() == View.GONE) {
                    emptyImg.setVisibility(View.GONE);
                }
            }
            String a = DataConverter.bitMapToString(result.cardBitmap);
            bitmap = DataConverter.stringToBitMap(a);
            showFrontImage(result.cardBitmap);
            presentOutput(dataMap);
            // If the results don't match
            return MLGcrCaptureResult.CAPTURE_STOP;
        }

        @Override
        public void onCanceled() {
        }

        @Override
        public void onFailure(int retCode, Bitmap bitmap) {
        }

        @Override
        public void onDenied() {
        }
    };

    /**
     * The enum Card type.
     */
    public enum CardType {
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

    /* Pan Card Output */
    private void presentOutput(HashMap<String, String> dataMap) {
        if (dataMap != null) {
            name.setText(dataMap.get(getString(R.string.panOutName)), TextView.BufferType.EDITABLE);
            fatherName.setText(dataMap.get(getString(R.string.panOutFatherName)), TextView.BufferType.EDITABLE);
            panNumber.setText(dataMap.get(getString(R.string.panOutNumber)), TextView.BufferType.EDITABLE);
            dateOfBirth.setText(dataMap.get(getString(R.string.panOutDob)), TextView.BufferType.EDITABLE);
        }
    }

    /* Aadhar Card Output */
    private void presentFrontOutput(HashMap<String, String> dataMap) {
        if (dataMap != null) {
            aadharName.setText(dataMap.get(getString(R.string.aadharOutName)), TextView.BufferType.EDITABLE);
            aadhaarNumber.setText(dataMap.get(getString(R.string.aadharOutNumber)), TextView.BufferType.EDITABLE);
            gender.setText(dataMap.get(getString(R.string.aadharOutGender)), TextView.BufferType.EDITABLE);
            fatherName.setText(dataMap.get(getString(R.string.aadharOutFatherName)), TextView.BufferType.EDITABLE);
            dateOfBirth.setText(dataMap.get(getString(R.string.aadharOutDob)), TextView.BufferType.EDITABLE);
            if (dataMap.get(getString(R.string.aadharOutYob)) != null) {
                yearOfBirth.setText(Objects.requireNonNull(dataMap.get(getString(R.string.aadharOutYob))).replace
                        (getString(R.string.aadharOutBirth), ""), TextView.BufferType.EDITABLE);
            }
        }
    }

    /* Business Card Output */
    private void businessOutput(HashMap<String, String> dataMap) {
        if (dataMap != null) {
            contactResults.setText(dataMap.get(getString(R.string.businessOutText)));
            mContactName.setText(dataMap.get(getString(R.string.businessOutName)));
            mContactNumber.setText(dataMap.get(getString(R.string.businessOutPhoneNumber)));
            mContactEmail.setText(dataMap.get(getString(R.string.businessOutEmail)));
            mContactOrganization.setText(dataMap.get(getString(R.string.businessOutOrganization)));
        }
    }

    private void showFrontImage(Bitmap bitmap) {
        frontImg.setVisibility(View.VISIBLE);
        frontImg.setImageBitmap(bitmap);
        frontSimpleImg.setVisibility(View.GONE);
        frontAddView.setVisibility(View.GONE);
        frontDeleteImg.setVisibility(View.VISIBLE);
        cardview.setVisibility(View.VISIBLE);
    }

    private void showFrontDeleteImage() {
        frontImg.setVisibility(View.GONE);
        frontSimpleImg.setVisibility(View.VISIBLE);
        frontAddView.setVisibility(View.VISIBLE);
        frontDeleteImg.setVisibility(View.GONE);
        cardview.setVisibility(View.GONE);
    }

    /* Detect an Image */
    private void tryReloadAndDetectInImage() {
        if (mImageUri == null) {
            return;
        }
        try {
            mTryImageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mImageUri);
        } catch (IOException error) {
            Logger.e("IOException", error);
        }
        detectLocalImage(mTryImageBitmap, callback);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (imageBitmap != null && !imageBitmap.isRecycled()) {
            imageBitmap.recycle();
            imageBitmap = null;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}