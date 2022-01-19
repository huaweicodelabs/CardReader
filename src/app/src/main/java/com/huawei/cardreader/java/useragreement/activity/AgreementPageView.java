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

package com.huawei.cardreader.java.useragreement.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.huawei.cardreader.R;
import com.huawei.cardreader.java.dashboard.activity.DashboardActivity;
import com.huawei.cardreader.java.utils.BioAuthUtils;

/**
 * The type Agreement page view.
 */
public class AgreementPageView extends AppCompatActivity {
    /**
     * The Is agree checked.
     */
    boolean isAgreeChecked = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agreement_page_view);
        this.setTitle(getString(R.string.Agree_Page_Title));
        init();
    }

    private void init() {
        BioAuthUtils.checkAuthenticationType(this);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            if (isAgreeChecked && BioAuthUtils.isDeviceAuthenticated()) {
                Intent i = new Intent(AgreementPageView.this, DashboardActivity.class
                );
                startActivity(i);
                finish();
            } else if (!isAgreeChecked) {
                BioAuthUtils.showGuide(view, R.string.AGREE_TO_PROCEED);
            } else {
                BioAuthUtils.showGuide(view, R.string.AUTH_SUGGESTION_MSG
                );
            }
        });
    }

    /**
     * On agree check click.
     *
     * @param view the view
     */
    public void onAgreeCheckClick(View view) {
        boolean checked = ((CheckBox) view).isChecked();
        if (view.getId() == R.id.AgreeCheckbox) {
            if (checked) {
                isAgreeChecked = true;
            } else {
                isAgreeChecked = false;
                BioAuthUtils.setIsKeyguardSecure(false);
                BioAuthUtils.setIsFingerPrintSecure(false);
                BioAuthUtils.setIsFaceRecognitionSecure(false);
            }
        }
    }
}