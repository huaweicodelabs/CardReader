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

package com.huawei.cardreader.java.bankcardscan.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.huawei.cardreader.R;
import com.huawei.cardreader.java.scannedcardlist.activity.ScannedCardListActivity;
import com.huawei.cardreader.java.scannedcardlist.roomdb.BusinessCardEntity;
import com.huawei.cardreader.java.scannedcardlist.roomdb.DatabaseClient;
import com.huawei.cardreader.java.utils.Constants;
import com.huawei.cardreader.java.utils.DataConverter;
import com.huawei.hms.mlplugin.card.bcr.MLBcrCapture;
import com.huawei.hms.mlplugin.card.bcr.MLBcrCaptureConfig;
import com.huawei.hms.mlplugin.card.bcr.MLBcrCaptureFactory;
import com.huawei.hms.mlplugin.card.bcr.MLBcrCaptureResult;

import java.util.Objects;

/**
 * The type Bank card.
 */
public class BankCard extends AppCompatActivity implements View.OnClickListener {
    private ImageView bankCardFrontImg;
    private ImageView bankCardFrontSimpleImg;
    private ImageView bankCardFrontDeleteImg;
    private ImageView numberImageView;
    private LinearLayout bankCardFrontAddView;
    private LinearLayout results;
    private TextView showResult;
    private String lastFrontResult = "";
    private Bitmap currentImage;
    private MLBcrCaptureResult scannedResult;
    private Bitmap bitmap;
    private Button save;
    private RelativeLayout emptyImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_card);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Window window = getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);
        initComponent();
    }

    /**
     * Init component.
     */
    private void initComponent() {
        bankCardFrontImg = findViewById(R.id.avatar_img);
        bankCardFrontSimpleImg = findViewById(R.id.avatar_sample_img);
        bankCardFrontDeleteImg = findViewById(R.id.avatar_delete);
        bankCardFrontAddView = findViewById(R.id.avatar_add);
        showResult = findViewById(R.id.show_result);
        save = findViewById(R.id.btn_save);
        results = findViewById(R.id.results);
        emptyImg = findViewById(R.id.emptyimg);
        bankCardFrontAddView.setOnClickListener(this);
        bankCardFrontDeleteImg.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.avatar_add:
                startCaptureActivity();
                break;
            case R.id.avatar_delete:
                showFrontDeleteImage();
                lastFrontResult = "";
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (currentImage != null && !currentImage.isRecycled()) {
            currentImage.recycle();
            currentImage = null;
        }
    }

    /**
     * Start Capture Activity.
     */
    private void startCaptureActivity() {
        MLBcrCaptureConfig config = new MLBcrCaptureConfig.Factory()
                .setOrientation(MLBcrCaptureConfig.ORIENTATION_AUTO)
                .setResultType(MLBcrCaptureConfig.RESULT_ALL)
                .create();
        MLBcrCapture bcrCapture = MLBcrCaptureFactory.getInstance().getBcrCapture(config);
        bcrCapture.captureFrame(this, this.callback);
    }

    /**
     * Format Card Results.
     *
     * @param result of type MLBcrCaptureResult
     * @return String of type bankcard
     */
    private String formatCardResult(MLBcrCaptureResult result) {
        scannedResult = result;
        String resultBuilder = Constants.BANKCARDNUMBER +
                result.getNumber() +
                System.lineSeparator() +
                Constants.ISSUER +
                result.getIssuer() +
                System.lineSeparator() +
                Constants.EXPIRE +
                result.getExpire() +
                System.lineSeparator() +
                Constants.CARDTYPE +
                result.getType() +
                System.lineSeparator() +
                Constants.ORGANIZATION +
                result.getOrganization() +
                System.lineSeparator();
        return resultBuilder;
    }

    private MLBcrCapture.Callback callback = new MLBcrCapture.Callback() {
        @Override
        public void onSuccess(MLBcrCaptureResult result) {
            if (result == null) {
                return;
            }
            bitmap = result.getOriginalBitmap();
            showSuccessResult(bitmap, result);
        }

        @Override
        public void onCanceled() {
        }

        @Override
        public void onFailure(int retCode, Bitmap bitmap) {
            showResult.setText(getString(R.string.REC_FAILED));
        }

        @Override
        public void onDenied() {
        }
    };

    /**
     * Show Success Result.
     *
     * @param bitmap       of type Bitmap
     * @param idCardResult MLBcrCaptureResult
     */
    private void showSuccessResult(Bitmap bitmap, MLBcrCaptureResult idCardResult) {
        showFrontImage(bitmap);
        lastFrontResult = formatCardResult(idCardResult);
        showResult.setText(lastFrontResult);
        String lastBackResult = "";
        showResult.append(lastBackResult);
        numberImageView = findViewById(R.id.number);
        numberImageView.setImageBitmap(idCardResult.getNumberBitmap());
    }

    private void showFrontImage(Bitmap bitmap) {
        bankCardFrontImg.setVisibility(View.VISIBLE);
        bankCardFrontImg.setImageBitmap(bitmap);
        bankCardFrontSimpleImg.setVisibility(View.GONE);
        bankCardFrontAddView.setVisibility(View.GONE);
        bankCardFrontDeleteImg.setVisibility(View.VISIBLE);
        save.setVisibility(View.VISIBLE);
        results.setVisibility(View.VISIBLE);
        emptyImg.setVisibility(View.GONE);
    }

    private void showFrontDeleteImage() {
        bankCardFrontImg.setVisibility(View.GONE);
        bankCardFrontSimpleImg.setVisibility(View.VISIBLE);
        bankCardFrontAddView.setVisibility(View.VISIBLE);
        bankCardFrontDeleteImg.setVisibility(View.GONE);
        save.setVisibility(View.GONE);
        results.setVisibility(View.GONE);
    }

    /**
     * Save card.
     *
     * @param v the v
     */
    public void saveCard(View v) {
        if (lastFrontResult != null && !lastFrontResult.isEmpty()) {
            save();
        } else {
            Toast.makeText(getApplicationContext(), R.string.pleasescancard, Toast.LENGTH_LONG).show();
        }
    }

    private void save() {
        @SuppressLint("StaticFieldLeak")
        class SaveTask extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... voids) {
                BusinessCardEntity userDetails = new BusinessCardEntity();
                userDetails.setName(getString(R.string.xxx));
                userDetails.setAccountnumber(DataConverter.enCodeString(scannedResult.getNumber()));
                userDetails.setIssuer(scannedResult.getIssuer());
                userDetails.setExpirydate(DataConverter.enCodeString(scannedResult.getExpire()));
                userDetails.setBanktype(scannedResult.getType());
                userDetails.setBankorganization(DataConverter.enCodeString
                        (scannedResult.getOrganization()));
                userDetails.setCardType(getString(R.string.cardType_bankCard));
                userDetails.setImage(DataConverter.bitMapToString(bitmap));
                DatabaseClient.getInstance(getApplicationContext()).getAppDatabase()
                        .businessCardEntityDao()
                        .insert(userDetails);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                bankCardFrontImg.setVisibility(View.GONE);
                bankCardFrontSimpleImg.setVisibility(View.VISIBLE);
                bankCardFrontAddView.setVisibility(View.VISIBLE);
                bankCardFrontDeleteImg.setVisibility(View.GONE);
                showResult.setVisibility(View.GONE);
                numberImageView.setVisibility(View.GONE);
                Intent intent = new Intent(getApplicationContext(), ScannedCardListActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra(Constants.CARD_TYPE_TO_SAVE, getString(R.string.cardType_bankCard));
                startActivity(intent);
            }
        }
        SaveTask st = new SaveTask();
        st.execute();
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
