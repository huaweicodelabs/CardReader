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

package com.huawei.cardreader.java.settingspage.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.huawei.cardreader.java.BaseActivity;
import com.huawei.cardreader.R;
import com.huawei.cardreader.java.utils.Constants;
import com.huawei.hms.common.util.Logger;

import java.util.Objects;

/**
 * The type Settings activity.
 */
public class SettingsActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.settings_activity);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        TextView mVersion = this.findViewById(R.id.version);
        try {
            mVersion.setText(this.getVersionName());
        } catch (PackageManager.NameNotFoundException e) {
            Logger.e("NameNotFoundException", e.toString());
        }

        TextView changePwd = this.findViewById(R.id.ChangePWD);
        changePwd.setOnClickListener(view -> startActivityForResult(new Intent
                (Settings.ACTION_SECURITY_SETTINGS), 0));
    }

    /**
     * get App versionName
     *
     * @return version name
     */
    public String getVersionName() throws PackageManager.NameNotFoundException {
        PackageManager packageManager = this.getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(this.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return Constants.DEFAULT_VERSION;
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
