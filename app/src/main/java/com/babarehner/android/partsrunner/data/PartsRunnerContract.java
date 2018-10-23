 /*
  * Copyright (C) 2018 Mike Rehner
  *
  * Licensed under the Apache License, Version 2.0 (the "License");
  * you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at
  *
  *      http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  */

package com.babarehner.android.partsrunner.data;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;



public class PartsRunnerContract {

    // To prevent someone from accidentally instantiating thecontract class
    private PartsRunnerContract() { }

    // use package name for convenience for the Content Authority
    public static final String PARTS_RUNNER_AUTHORITY = "com.babarehner.android.partsrunner";

    // Use PARTS_RUNNER_AUTHORITY to create the base of all Uri's which apps use
    // to contact the content provider
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" +
            PARTS_RUNNER_AUTHORITY);
    public static final String PATH_TABLE_NAME = "TMachines";



    // Inner class that defines parts runner table and columns
    public static final class MachineEntry implements BaseColumns {

        // MIME type of the (@link #CONTENT_URI for a stuff database table
        public static final String MACHINE_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
                + "/" + PARTS_RUNNER_AUTHORITY + "/" + PATH_TABLE_NAME;
        // MIME type of the (@link #CONTENT_URI for a single record
        public static final String MACHINE_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
                + "/" + PARTS_RUNNER_AUTHORITY + "/" + PATH_TABLE_NAME;
        // Content URI to access the table data in the provider
        public static final Uri PARTS_RUNNER_URI = Uri.withAppendedPath(BASE_CONTENT_URI,
                PATH_TABLE_NAME);

        public static final String TABLE_NAME = "TMachines";

        // the globals and the columns
        public static final String C_MACHINE_TYPE = "CMachineType";
        public static final String C_MODEL_YEAR = "CModelYear";
        public static final String C_MANUFACTURER = "CManufacturer";
        public static final String C_MODEL = "CModel";
        public static final String C_MODEL_NO = "CModelNo";
        public static final String C_SERIAL_NO = "CSerialNo";
        public static final String C_MACHINE_NUM = "CMachineNum";   // if more than one same machine
        public static final String C_NOTES = "CNotes";

    }

}


