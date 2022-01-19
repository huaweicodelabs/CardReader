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

package com.huawei.cardreader.java.scannedcardlist.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.huawei.cardreader.R;
import com.huawei.cardreader.java.bankcardscan.activity.BankCard;
import com.huawei.cardreader.databinding.ActivityQrCodeGeneratorMainBinding;
import com.huawei.cardreader.java.generalcardscan.activity.GeneralCardActivity;
import com.huawei.cardreader.java.scannedcardlist.adapter.BankCardListAdapter;
import com.huawei.cardreader.java.scannedcardlist.adapter.GeneralcardListAdapter;
import com.huawei.cardreader.java.scannedcardlist.model.QRcodmodel;
import com.huawei.cardreader.java.scannedcardlist.roomdb.BusinessCardEntity;
import com.huawei.cardreader.java.scannedcardlist.roomdb.DatabaseClient;
import com.huawei.cardreader.java.utils.Constants;
import com.huawei.cardreader.java.utils.DataConverter;
import com.huawei.hms.common.util.Logger;
import com.huawei.hms.hmsscankit.ScanUtil;
import com.huawei.hms.hmsscankit.WriterException;
import com.huawei.hms.ml.scan.HmsBuildBitmapOption;
import com.huawei.hms.ml.scan.HmsScan;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.huawei.cardreader.java.utils.Constants.CARD_TYPE_TO_SAVE;
import static com.huawei.cardreader.java.utils.Constants.IS_DELETED;

/**
 * The type Qr code generator main activity.
 */
public class ScannedCardListActivity extends AppCompatActivity implements
        GeneralcardListAdapter.AdapterCallback, BankCardListAdapter.BankAdapterCallback {
    private int type = HmsScan.QRCODE_SCAN_TYPE;
    private String cardType;
    private ActivityQrCodeGeneratorMainBinding mActivityQrCodeGeneratorMainBinding;
    private boolean shouldExecuteOnResume;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityQrCodeGeneratorMainBinding = DataBindingUtil.setContentView
                (this, R.layout.activity_qr_code_generator_main);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        shouldExecuteOnResume = false;
        initComponent();
    }

    /* Initialize component and fetch datas from local room db */
    private void initComponent() {
        if (getIntent().getStringExtra(CARD_TYPE_TO_SAVE) != null) {
            cardType = getIntent().getStringExtra(CARD_TYPE_TO_SAVE);
        }
        mActivityQrCodeGeneratorMainBinding.relScan.setOnClickListener
                (view -> openScannerActivity());
        mActivityQrCodeGeneratorMainBinding.relBottomScan.setOnClickListener
                (view -> openScannerActivity());
        getDatafromRoomdb();
    }

    /**
     * Navigate to respective activity from list of cards screen
     */
    private void openScannerActivity() {
        if (cardType.equalsIgnoreCase(Constants.BANKCARD)) {
            Intent intent = new Intent(ScannedCardListActivity.this, BankCard.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(ScannedCardListActivity.this,
                    GeneralCardActivity.class);
            startActivity(intent);
        }
    }

    /**
     * Retrive card details  data from room db
     */
    private void getDatafromRoomdb() {
        @SuppressLint("StaticFieldLeak")
        class GetTasks extends AsyncTask<Void, Void, List<BusinessCardEntity>> {
            @Override
            protected List<BusinessCardEntity> doInBackground(Void... voids) {
                return getAllCards();
            }

            @Override
            protected void onPostExecute(List<BusinessCardEntity> tasks) {
                super.onPostExecute(tasks);
                List<BusinessCardEntity> businessCardEntityList = new ArrayList<>();
                List<BusinessCardEntity> bankCardEntityList = new ArrayList<>();
                HmsBuildBitmapOption options = new HmsBuildBitmapOption.Creator()
                        .setBitmapMargin(0).create();
                try {
                    setCardDetails(tasks, businessCardEntityList, bankCardEntityList,
                            options);
                } catch (WriterException e) {
                    Logger.e("IOException", e);
                }
            }
        }

        GetTasks gt = new GetTasks();
        gt.execute();
    }

    private void setCardDetails(List<BusinessCardEntity> tasks,
                                List<BusinessCardEntity> businessCardEntityList,
                                List<BusinessCardEntity> bankCardEntityList,
                                HmsBuildBitmapOption options) throws WriterException {
        StringBuilder stringBuilder;
        ArrayList<QRcodmodel> dataset = new ArrayList<>();
        ArrayList<QRcodmodel> dataset1 = new ArrayList<>();
        Bitmap qrBitmap;
        int width = 200;
        int height = 200;
        try {
            for (int i = 0; i < tasks.size(); i++) {
                QRcodmodel qRcodmodel = new QRcodmodel();
                BusinessCardEntity userDetails = new BusinessCardEntity();

                if (tasks.get(i).getCardType().equalsIgnoreCase(Constants.BUSINESS_CARD)) {
                    userDetails.setId(tasks.get(i).getId());
                    userDetails.setName(tasks.get(i).getName());
                    userDetails.setMobileno(tasks.get(i).getMobileno());
                    userDetails.setCompanyname(tasks.get(i).getCompanyname());
                    userDetails.setEmailid(tasks.get(i).getEmailid());
                    userDetails.setAddress(tasks.get(i).getAddress());
                    userDetails.setWebsite(tasks.get(i).getWebsite());
                    userDetails.setJobtitle(tasks.get(i).getJobtitle());
                    userDetails.setImage(tasks.get(i).getImage());
                    userDetails.setCardType(tasks.get(i).getCardType());
                    businessCardEntityList.add(userDetails);
                    stringBuilder = new StringBuilder();
                    stringBuilder.append(getResources().getString(R.string.qrName)).append
                            (DataConverter.deCodeString(userDetails.getName())).append
                            (System.lineSeparator());
                    stringBuilder.append(getResources().getString(R.string.qrMobileno)).append
                            (DataConverter.deCodeString(userDetails.getMobileno())).append
                            (System.lineSeparator());
                    stringBuilder.append(getResources().getString(R.string.qrCompanyName)).append
                            (DataConverter.deCodeString(userDetails.getCompanyname())).append
                            (System.lineSeparator());
                    stringBuilder.append(getResources().getString(R.string.qrEmailId)).append
                            (DataConverter.deCodeString(userDetails.getEmailid())).append
                            (System.lineSeparator());
                    stringBuilder.append(getResources().getString(R.string.qrCardType)).append
                            (userDetails.getCardType()).append(System.lineSeparator());
                    qrBitmap = ScanUtil.buildBitmap
                            (stringBuilder.toString(), type, width, height, options);
                    qRcodmodel.setBusinesscardUserDetailsList(businessCardEntityList);
                    qRcodmodel.setBitmap(qrBitmap);
                    dataset.add(qRcodmodel);
                } else if (tasks.get(i).getCardType().equalsIgnoreCase
                        (Constants.AADHARCARD)) {
                    userDetails.setId(tasks.get(i).getId());
                    userDetails.setName(tasks.get(i).getName());
                    userDetails.setAadharid(tasks.get(i).getAadharid());
                    userDetails.setGender(tasks.get(i).getGender());
                    userDetails.setFathername(tasks.get(i).getFathername());
                    userDetails.setDob(tasks.get(i).getDob());
                    userDetails.setAddress(tasks.get(i).getAddress());
                    userDetails.setCardType(tasks.get(i).getCardType());
                    userDetails.setImage(tasks.get(i).getImage());
                    businessCardEntityList.add(userDetails);
                    stringBuilder = new StringBuilder();
                    stringBuilder.append(getResources().getString(R.string.qrName)).append
                            (DataConverter.deCodeString(userDetails.getName())).append
                            (System.lineSeparator());
                    stringBuilder.append(getResources().getString(R.string.qrCardType)).append
                            (userDetails.getCardType()).append(System.lineSeparator());
                    stringBuilder.append(getResources().getString(R.string.qrAadhaarId)).append
                            (DataConverter.deCodeString(userDetails.getAadharid())).append
                            (System.lineSeparator());
                    stringBuilder.append(getResources().getString(R.string.qrDOB)).append
                            (DataConverter.deCodeString(userDetails.getDob())).append
                            (System.lineSeparator());
                    qrBitmap = ScanUtil.buildBitmap
                            (stringBuilder.toString(), type, width, height, options);
                    qRcodmodel.setBusinesscardUserDetailsList(businessCardEntityList);
                    qRcodmodel.setBitmap(qrBitmap);
                    dataset.add(qRcodmodel);
                } else if (tasks.get(i).getCardType().equalsIgnoreCase(Constants.PANCARD)) {
                    userDetails.setId(tasks.get(i).getId());
                    userDetails.setName(tasks.get(i).getName());
                    userDetails.setPannumber(tasks.get(i).getPannumber());
                    userDetails.setFathername(tasks.get(i).getFathername());
                    userDetails.setDob(tasks.get(i).getDob());
                    userDetails.setCardType(tasks.get(i).getCardType());
                    userDetails.setImage(tasks.get(i).getImage());
                    businessCardEntityList.add(userDetails);
                    stringBuilder = new StringBuilder();
                    stringBuilder.append(getResources().getString(R.string.qrName)).append
                            (DataConverter.deCodeString(userDetails.getName())).append
                            (System.lineSeparator());
                    stringBuilder.append(getResources().getString(R.string.qrCardType)).append
                            (userDetails.getCardType()).append(System.lineSeparator());
                    stringBuilder.append(getResources().getString(R.string.qrPanNumber)).append
                            (DataConverter.deCodeString(userDetails.getPannumber())).append
                            (System.lineSeparator());
                    stringBuilder.append(getResources().getString(R.string.qrDOB)).append
                            (DataConverter.deCodeString(userDetails.getDob())).append
                            (System.lineSeparator());
                    qrBitmap = ScanUtil.buildBitmap
                            (stringBuilder.toString(), type, width, height, options);
                    qRcodmodel.setBusinesscardUserDetailsList(businessCardEntityList);
                    qRcodmodel.setBitmap(qrBitmap);
                    dataset.add(qRcodmodel);
                } else {
                    userDetails.setId(tasks.get(i).getId());
                    userDetails.setName(tasks.get(i).getName());
                    userDetails.setAccountnumber(tasks.get(i).getAccountnumber());
                    userDetails.setCardType(tasks.get(i).getCardType());
                    userDetails.setIssuer(tasks.get(i).getIssuer());
                    userDetails.setExpirydate(tasks.get(i).getExpirydate());
                    userDetails.setBanktype(tasks.get(i).getBanktype());
                    userDetails.setBankorganization(tasks.get(i).getBankorganization());
                    userDetails.setImage(tasks.get(i).getImage());
                    bankCardEntityList.add(userDetails);
                    stringBuilder = new StringBuilder();
                    stringBuilder.append(getResources().getString(R.string.qrName)).append
                            (DataConverter.deCodeString(userDetails.getName())).append
                            (System.lineSeparator());
                    stringBuilder.append(getResources().getString(R.string.qrAcNumber)).append
                            (DataConverter.deCodeString(userDetails.getAccountnumber())).append
                            (System.lineSeparator());
                    stringBuilder.append(getResources().getString(R.string.qrCardType)).append
                            (userDetails.getCardType()).append(System.lineSeparator());
                    stringBuilder.append(getResources().getString(R.string.qrIssuer)).append
                            (userDetails.getIssuer()).append(System.lineSeparator());
                    stringBuilder.append(getResources().getString(R.string.qrBankType)).append
                            (userDetails.getBanktype()).append(System.lineSeparator());
                    stringBuilder.append(getResources().getString(R.string.qrExpiryDate)).append
                            (DataConverter.deCodeString(userDetails.getExpirydate())).append
                            (System.lineSeparator());
                    stringBuilder.append(getResources().getString(R.string.qrBankOrganization)).append(
                            DataConverter.deCodeString(userDetails.getBankorganization())).append
                            (System.lineSeparator());
                    qrBitmap = ScanUtil.buildBitmap
                            (stringBuilder.toString(), type, width, height, options);
                    qRcodmodel.setBusinesscardUserDetailsList(bankCardEntityList);
                    qRcodmodel.setBitmap(qrBitmap);
                    dataset1.add(qRcodmodel);
                }
            }
        } catch (WriterException e) {
            throw e;
        }
        initRecyclerview(dataset, dataset1, cardType);
    }

    private List<BusinessCardEntity> getAllCards() {
        return DatabaseClient
                .getInstance(getApplicationContext())
                .getAppDatabase()
                .businessCardEntityDao()
                .getAll();
    }

    /* Initialize Generlcards and Bankcard recyclerview */
    private void initRecyclerview(ArrayList<QRcodmodel> dataset, ArrayList<QRcodmodel>
            dataset1, String cardType) {
        if (cardType.equalsIgnoreCase(Constants.BANKCARD)) {
            if (dataset1 != null && dataset1.size() > 0) {
                BankCardListAdapter mBankCardListAdapter = new BankCardListAdapter
                        (this, dataset1);
                mBankCardListAdapter.setmBankAdapterCallback(this);
                mActivityQrCodeGeneratorMainBinding.bankcardRecyclerview.setVisibility
                        (View.VISIBLE);
                mActivityQrCodeGeneratorMainBinding.bankcardRecyclerview.setHasFixedSize(true);
                mActivityQrCodeGeneratorMainBinding.bankcardRecyclerview.setLayoutManager
                        (new LinearLayoutManager(this));
                mActivityQrCodeGeneratorMainBinding.bankcardRecyclerview.setAdapter
                        (mBankCardListAdapter);
            } else {
                mActivityQrCodeGeneratorMainBinding.liListempty.setVisibility(View.VISIBLE);
                mActivityQrCodeGeneratorMainBinding.relBottomScan.setVisibility(View.GONE);
            }
        } else {
            if (dataset != null && dataset.size() > 0) {
                GeneralcardListAdapter mAdapter = new GeneralcardListAdapter
                        (this, dataset);
                mAdapter.setmAdapterCallback(this);
                mActivityQrCodeGeneratorMainBinding.recyclerView.setVisibility(View.VISIBLE);
                mActivityQrCodeGeneratorMainBinding.recyclerView.setHasFixedSize(true);
                mActivityQrCodeGeneratorMainBinding.recyclerView.setLayoutManager
                        (new LinearLayoutManager(this));
                mActivityQrCodeGeneratorMainBinding.recyclerView.setAdapter(mAdapter);
            } else {
                mActivityQrCodeGeneratorMainBinding.liListempty.setVisibility(View.VISIBLE);
                mActivityQrCodeGeneratorMainBinding.relBottomScan.setVisibility(View.GONE);
            }
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

    @Override
    protected void onResume() {
        super.onResume();
        if (shouldExecuteOnResume) {
            getDatafromRoomdb();
        } else {
            shouldExecuteOnResume = true;
        }
    }

    @Override
    public void onMethodCallback(int id) {
        @SuppressLint("StaticFieldLeak")
        class DeleteTask extends AsyncTask<Void, Void, Integer> {
            @Override
            protected Integer doInBackground(Void... voids) {
                DatabaseClient.getInstance(getApplicationContext()).getAppDatabase()
                        .businessCardEntityDao()
                        .deleteById(id);
                return 0;
            }

            @Override
            protected void onPostExecute(Integer aVoid) {
                super.onPostExecute(aVoid);
                if (IS_DELETED) {
                    mActivityQrCodeGeneratorMainBinding.liListempty.setVisibility(View.VISIBLE);
                    mActivityQrCodeGeneratorMainBinding.relBottomScan.setVisibility(View.GONE);
                }
            }
        }

        DeleteTask st = new DeleteTask();
        st.execute();
    }
}