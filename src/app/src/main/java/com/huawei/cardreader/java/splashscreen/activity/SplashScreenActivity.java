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

package com.huawei.cardreader.java.splashscreen.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import com.huawei.cardreader.java.BaseActivity;
import com.huawei.cardreader.R;
import com.huawei.cardreader.java.dashboard.activity.DashboardActivity;
import com.huawei.cardreader.java.useragreement.activity.AgreementPageView;

/**
 * The type Splash screen activity.
 */
public class SplashScreenActivity extends BaseActivity
        implements ActivityCompat.OnRequestPermissionsResultCallback, View.OnClickListener {
    private SharedPreferences sharedPreferences;
    private Boolean firstTime;

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        sharedPreferences = SplashScreenActivity.this.getSharedPreferences(getString(R.string.splash), MODE_PRIVATE);
        firstTime = sharedPreferences.getBoolean(getString(R.string.firstTime), true);
        if (firstTime) {
            new Handler().postDelayed(() -> {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                firstTime = false;
                editor.putBoolean(getString(R.string.firstTime), firstTime);
                editor.apply();

                Intent i = new Intent(SplashScreenActivity.this,
                        AgreementPageView.class);
                startActivity(i);
                finish();
            }, 2000);
        } else {
            new Handler().postDelayed(() -> {
                // This method will be executed once the timer is over
                Intent i = new Intent(SplashScreenActivity.this,
                        DashboardActivity.class);
                startActivity(i);
                finish();
            }, 2000);
        }
    }


    @Override
    public void onClick(View view) {
    }
}