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



    // Inner class that defines parts runner table and columns
    public static final class StuffEntry implements BaseColumns {

        public static final String TABLE_NAME = "TStuff";

        // the globals and the columns
        public static final String C_MANUFACTURER = "CManufacturer";
        public static final String C_MODEL_YEAR = "CModelYear";
        public static final String C_MODEL = "CModel";
        public static final String C_MODEL_NO = "CModelNo";
        public static final String C_SERIAL_NO = "CSerialNo";
        public static final String C_NOTES = "CNotes";
    }

}


