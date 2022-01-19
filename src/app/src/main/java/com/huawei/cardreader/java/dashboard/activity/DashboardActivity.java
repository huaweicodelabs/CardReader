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

package com.huawei.cardreader.java.dashboard.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.huawei.cardreader.R;
import com.huawei.cardreader.java.scannedcardlist.activity.ScannedCardListActivity;
import com.huawei.cardreader.java.settingspage.activity.SettingsActivity;
import com.huawei.cardreader.java.utils.Constants;
import com.huawei.hms.common.util.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * The type Dashboard activity.
 */
public class DashboardActivity extends AppCompatActivity implements
        ActivityCompat.OnRequestPermissionsResultCallback, View.OnClickListener {
    private static final int PERMISSION_REQUESTS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);
        try {
            if (!this.allPermissionsGranted()) {
                this.getRuntimePermissions();
            }
        } catch (PackageManager.NameNotFoundException e) {
            Logger.e("NameNotFoundException", e.toString());
        }
        LinearLayout indianIDLayout = findViewById(R.id.IDcardsView);
        LinearLayout bankCardLayout = findViewById(R.id.BankCardsView);
        LinearLayout settings = findViewById(R.id.Text);
        indianIDLayout.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), ScannedCardListActivity.class);
            intent.putExtra(Constants.CARD_TYPE_TO_SAVE, Constants.GENERALCARDS);
            intent.putExtra(Constants.SCREENTYPE, Constants.GENERALCARDS);
            intent.putExtra(Constants.SCANNEDCARDNUMBER, Constants.SAMPLNUMBER);
            startActivity(intent);
        });
        bankCardLayout.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), ScannedCardListActivity.class);
            intent.putExtra(Constants.CARD_TYPE_TO_SAVE, Constants.BANKCARD);
            intent.putExtra(Constants.SCREENTYPE, Constants.BANKCARD);
            intent.putExtra(Constants.SCANNEDCARDNUMBER, Constants.SAMPLNUMBER);
            startActivity(intent);
        });
        settings.setOnClickListener(view -> {
            Intent i = new Intent(DashboardActivity.this, SettingsActivity.class);
            startActivity(i);
        });
    }

    private String[] getRequiredPermissions() throws PackageManager.NameNotFoundException {
        try {
            PackageInfo info =
                    null;
            try {
                info = this.getPackageManager()
                        .getPackageInfo(this.getPackageName(), PackageManager.GET_PERMISSIONS);
            } catch (PackageManager.NameNotFoundException e) {
                throw e;
            }
            String[] ps = info.requestedPermissions;
            if (ps != null && ps.length > 0) {
                return ps;
            } else {
                return new String[0];
            }
        } catch (RuntimeException | PackageManager.NameNotFoundException e) {
            throw e;
        }
    }

    private boolean allPermissionsGranted() throws PackageManager.NameNotFoundException {
        for (String permission : this.getRequiredPermissions()) {
            if (!DashboardActivity.isPermissionGranted(this, permission)) {
                return false;
            }
        }
        return true;
    }

    private void getRuntimePermissions() throws PackageManager.NameNotFoundException {
        List<String> allNeededPermissions = new ArrayList<>();
        for (String permission : this.getRequiredPermissions()) {
            if (!DashboardActivity.isPermissionGranted(this, permission)) {
                allNeededPermissions.add(permission);
            }
        }

        if (!allNeededPermissions.isEmpty()) {
            ActivityCompat.requestPermissions(
                    this, allNeededPermissions.toArray(new String[0]),
                    DashboardActivity.PERMISSION_REQUESTS);
        }
    }

    private static boolean isPermissionGranted(Context context, String permission) {
        if (ContextCompat.checkSelfPermission(context, permission)
                == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull
            int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != DashboardActivity.PERMISSION_REQUESTS) {
            return;
        }
        boolean isNeedShowDiag = false;
        for (int i = 0; i < permissions.length; i++) {
            if ((permissions[i].equals(Manifest.permission.CAMERA) && grantResults[i]
                    != PackageManager.PERMISSION_GRANTED)
                    || (permissions[i].equals(Manifest.permission.READ_EXTERNAL_STORAGE)
                    && grantResults[i] != PackageManager.PERMISSION_GRANTED)
                    || (permissions[i].equals(Manifest.permission.RECORD_AUDIO) && grantResults[i]
                    != PackageManager.PERMISSION_GRANTED)) {
                isNeedShowDiag = true;
            }
        }
        if (isNeedShowDiag && !ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CALL_PHONE)) {
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setMessage(this.getString(R.string.camera_permission_rationale))
                    .setPositiveButton(this.getString(R.string.settings), (dialog12, which) -> {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.setData(Uri.parse(getString(R.string.intentPackage) +
                                DashboardActivity.this.getPackageName()));
                        DashboardActivity.this.startActivityForResult(intent, 200);
                        DashboardActivity.this.startActivity(intent);
                    })
                    .setNegativeButton(this.getString(R.string.cancel), (dialog1, which)
                            -> DashboardActivity.this.finish
                            ()).create();
            dialog.show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 200) {
            try {
                if (!this.allPermissionsGranted()) {
                    this.getRuntimePermissions();
                }
            } catch (PackageManager.NameNotFoundException e) {
                Logger.e("NameNotFoundException", e.toString());
            }
        }
    }

    @Override
    public void onClick(View view) {
    }
}