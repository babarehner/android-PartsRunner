/*
 * Copyright (C) 2018 Mike Rehner
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.babarehner.android.partsrunner;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;


import android.widget.TextView;

public class ShareDataFragment extends Fragment {

    public final String LOG_TAG = ShareDataFragment.class.getSimpleName();

    public Intent shareEMail(ShareActionProvider sap, StringBuilder sb){
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        if (sap != null){
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_SUBJECT, "Parts Info");
            intent.putExtra(Intent.EXTRA_TEXT, sb.toString());
            intent.setData(Uri.parse("mailto: "));
        } else {
            Log.v(LOG_TAG, "Share Action Provider is most likely null");
        }
        return intent;
    }

    public Intent shareText(ShareActionProvider sap, StringBuilder sb){
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        String s = sb.toString();
        if (sap != null){
            intent.putExtra("sms_body", s);
            intent.setData(Uri.parse("smsto:"));
        } else {
            Log.v(LOG_TAG, "Share Action Provider is most likely null");
        }
        return intent;
    }

}
