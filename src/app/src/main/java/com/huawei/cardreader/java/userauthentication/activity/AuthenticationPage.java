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

package com.huawei.cardreader.java.userauthentication.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.provider.Settings;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.huawei.cardreader.R;
import com.huawei.cardreader.java.scannedcarddetails.activity.ScannedCardDetailsActivity;
import com.huawei.cardreader.java.scannedcardlist.roomdb.DatabaseClient;
import com.huawei.cardreader.java.utils.BioAuthUtils;
import com.huawei.cardreader.java.utils.Constants;
import com.huawei.hms.support.api.fido.bioauthn.BioAuthnCallback;
import com.huawei.hms.support.api.fido.bioauthn.BioAuthnManager;
import com.huawei.hms.support.api.fido.bioauthn.BioAuthnPrompt;
import com.huawei.hms.support.api.fido.bioauthn.BioAuthnResult;
import com.huawei.hms.support.api.fido.bioauthn.FaceManager;

import java.util.Objects;

/**
 * The type Authentication page.
 */
public class AuthenticationPage extends AppCompatActivity implements View.OnClickListener {
    private static final int PIN_LOCK_REQ_CODE = 11;
    private static final int SECURITY_SETTING_REQUEST_CODE = 111;
    private boolean isAuthenticationClicked = false;
    private BioAuthnPrompt bioAuthnPrompt;
    private Bitmap qrCodeImage;
    private int id;
    private String qrCodeCardType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication_page);
        this.setTitle(getString(R.string.AUTHENTICATION_PAGE_TITLE));
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        initialize();
    }

    private void initialize() {
        ImageView lockImage = findViewById(R.id.LockImage);
        lockImage.setOnClickListener(this);

        ImageView fingerPrintImage = findViewById(R.id.FingerprintImage);
        fingerPrintImage.setOnClickListener(this);

        ImageView faceRecognizeImage = findViewById(R.id.FacerecignitionImage);
        faceRecognizeImage.setOnClickListener(this);

        bioAuthnPrompt = createBioAuthnPrompt();

        qrCodeImage = getIntent().getParcelableExtra(Constants.QRCODEIMAGE);
        id = getIntent().getIntExtra(Constants.ID, 0);
        qrCodeCardType = getIntent().getStringExtra(Constants.QRCODECARDTYPE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!isAuthenticationClicked) {
            Constants.IS_DELETED_FROM_PROFILEDETAILS = false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        isAuthenticationClicked = false;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.LockImage:
                checkAuthWithDeviceCredentials();
                isAuthenticationClicked = true;
                break;
            case R.id.FingerprintImage:
                checkAuthwithFingerPrint();
                isAuthenticationClicked = true;
                break;
            case R.id.FacerecignitionImage:
                checkAuthWithFaceRecognition();
                isAuthenticationClicked = true;
                break;

            default:
                isAuthenticationClicked = false;
                break;
        }
    }

    /* Checks for faceRecognition */
    private void checkAuthWithFaceRecognition() {
        // check camera permission
        int permissionCheck = ContextCompat.checkSelfPermission
                (this, Manifest.permission.CAMERA);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            BioAuthUtils.showToast(AuthenticationPage.this, getString
                    (R.string.FACE_AUTH_CAMERA_PERMISSION_NOT_ENABLED));
            ActivityCompat.requestPermissions(AuthenticationPage.this, new String[]
                    {Manifest.permission.CAMERA}, 1);
            return;
        }
        // call back
        BioAuthnCallback callback = new BioAuthnCallback() {
            @Override
            public void onAuthError(int errMsgId, @NonNull CharSequence errString) {
                BioAuthUtils.showToast(AuthenticationPage.this,
                        getString(R.string.FACE_AUTH_ERROR) + errMsgId + getString(R.string.errorMessage)
                                + errString
                                + (errMsgId == 1012 ? getString(R.string.FACE_CAMERA_MAY_NOT_BE) :
                                ""));
            }

            @Override
            public void onAuthHelp(int helpMsgId, @NonNull CharSequence helpString) {
                BioAuthUtils.showToast(AuthenticationPage.this, getString
                        (R.string.FACE_AUTH_HELP) + helpMsgId + getString(R.string.helpString) + helpString);
            }

            @Override
            public void onAuthSucceeded(@NonNull BioAuthnResult result) {
                BioAuthUtils.showToast(AuthenticationPage.this,
                        getString(R.string.FACE_AUTH_SUCCESS));
                if (Constants.IS_DELETED_FROM_PROFILEDETAILS) {
                    deleteCard(id);
                } else {
                    Intent intent = new Intent(getApplicationContext(),
                            ScannedCardDetailsActivity.class);
                    intent.putExtra(Constants.ID, id);
                    intent.putExtra(Constants.QRCODEIMAGE, qrCodeImage);
                    intent.putExtra(Constants.QRCODECARDTYPE, qrCodeCardType);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onAuthFailed() {
                BioAuthUtils.showToast(AuthenticationPage.this, getString(R.string.FACE_AUTH_FAIL));
            }
        };
        BioAuthUtils.checkAuthenticationType(this);
        if (BioAuthUtils.isFaceRecognitionSecure()) {
            // Cancellation Signal
            CancellationSignal cancellationSignal = new CancellationSignal();
            FaceManager faceManager = new FaceManager(this);
            // Checks whether 3D facial authentication can be used.
            int errorCode = faceManager.canAuth();
            if (errorCode != 0) {
                BioAuthUtils.showToast(this, getString(R.string.FACE_CANNOT_AUTH)
                        + errorCode);
                return;
            }
            int flags = 0;
            faceManager.auth(null, cancellationSignal, flags, callback, null);
        } else {
            Toast.makeText(this, R.string.FACEREC_SUPPORT_ERROR,
                    Toast.LENGTH_LONG)
                    .show();
        }
    }

    /* Creating Fingerprint result call back */
    private BioAuthnPrompt createBioAuthnPrompt() {
        BioAuthnCallback fpResultCallback = new BioAuthnCallback() {
            @Override
            public void onAuthError(int errMsgId, @NonNull CharSequence errString) {
                BioAuthUtils.showToast(AuthenticationPage.this,
                        getString(R.string.FP_AUTH_ERROR) + errMsgId +
                                getString(R.string.errorMessage) + errString);
            }

            @Override
            public void onAuthSucceeded(@NonNull BioAuthnResult result) {
                if (Constants.IS_DELETED_FROM_PROFILEDETAILS) {
                    deleteCard(id);
                } else {
                    Intent intent = new Intent(getApplicationContext(),
                            ScannedCardDetailsActivity.class);
                    intent.putExtra(Constants.ID, id);
                    intent.putExtra(Constants.QRCODEIMAGE, qrCodeImage);
                    intent.putExtra(Constants.QRCODECARDTYPE, qrCodeCardType);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onAuthFailed() {
                BioAuthUtils.showToast(AuthenticationPage.this, getString(R.string.FP_AUTH_FAILED));
            }
        };
        return new BioAuthnPrompt(this, ContextCompat.getMainExecutor(this),
                fpResultCallback);
    }

    /* checks for Fingerprint Authentications */
    private void checkAuthwithFingerPrint() {
        BioAuthUtils.checkAuthenticationType(this);
        if (BioAuthUtils.isFingerPrintSecure()) {
            BioAuthnManager bioAuthnManager = new BioAuthnManager(this);
            int errorCode = bioAuthnManager.canAuth();
            if (errorCode != 0) {
                Toast.makeText(this, R.string.FP_AUTH_CHECK + errorCode,
                        Toast.LENGTH_LONG).show();
                return;
            }
            BioAuthnPrompt.PromptInfo.Builder builder =
                    new BioAuthnPrompt.PromptInfo.Builder().setTitle(getString(R.string.FP_PROMPT_HEAD))
                            .setSubtitle(getString(R.string.FP_PROMPT_TITLE))
                            .setDescription(getString(R.string.FP_PROMPT_DESC))
                            .setDeviceCredentialAllowed(true);
            BioAuthnPrompt.PromptInfo info = builder.build();
            bioAuthnPrompt.auth(info);
        } else {
            Toast.makeText(this, R.string.FP_DEVICE_SUPPORT_ERROR,
                    Toast.LENGTH_LONG)
                    .show();
        }
    }

    /* check for PIN Lock Authentications */
    private void checkAuthWithDeviceCredentials() {
        BioAuthUtils.checkAuthenticationType(this);
        if (BioAuthUtils.isKeyguardSecure()) {
            KeyguardManager mKeyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
            Intent i = mKeyguardManager.createConfirmDeviceCredentialIntent
                    (getString(R.string.PIN_LOCK_TITLE), getString(R.string.PIN_LOCK_DESC));
            try {
                startActivityForResult(i, PIN_LOCK_REQ_CODE);
            } catch (NullPointerException e) {
                Intent intent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
                startActivityForResult(intent, SECURITY_SETTING_REQUEST_CODE);
            }
        } else {
            Toast.makeText(this, R.string.AUTH_SUGGESTION_MSG,
                    Toast.LENGTH_LONG)
                    .show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PIN_LOCK_REQ_CODE) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, R.string.PIN_SUCCESS, Toast.LENGTH_LONG).show();
                if (Constants.IS_DELETED_FROM_PROFILEDETAILS) {
                    deleteCard(id);
                } else {
                    Intent intent = new Intent(getApplicationContext(),
                            ScannedCardDetailsActivity.class);
                    intent.putExtra(Constants.ID, id);
                    intent.putExtra(Constants.QRCODEIMAGE, qrCodeImage);
                    intent.putExtra(Constants.QRCODECARDTYPE, qrCodeCardType);
                    startActivity(intent);
                    finish();
                }
            } else {
                Toast.makeText(this, R.string.PIN_FAILURE,
                        Toast.LENGTH_LONG)
                        .show();
            }
        }
    }

    private void deleteCard(int id) {
        @SuppressLint("StaticFieldLeak")
        class DeleteTask extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... voids) {
                DatabaseClient.getInstance(getApplicationContext()).getAppDatabase()
                        .businessCardEntityDao()
                        .deleteById(id);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Toast.makeText(getApplicationContext(), R.string.successFullyDeleted, Toast.LENGTH_LONG)
                        .show();
                Constants.IS_DELETED_FROM_PROFILEDETAILS = false;
                finish();
            }
        }
        DeleteTask st = new DeleteTask();
        st.execute();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}