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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


// Helps run the local Parts Runner DB
public class PartsRunnerDBHelper extends SQLiteOpenHelper {

    // To allow for changes in DB versioning and keeping user data
    private static final int DB_VERSION = 1;

    static final String DB_NAME = "parts_runner.db";

    public PartsRunnerDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_STUFF_TABLE = "CREATE TABLE " +
                PartsRunnerContract.StuffEntry.TABLE_NAME + " (" +
                PartsRunnerContract.StuffEntry._ID + " INTEGER PRIMARY KEY, " +
                PartsRunnerContract.StuffEntry.C_EQUIP_TYPE + " TEXT NOT NULL, " +
                PartsRunnerContract.StuffEntry.C_MANUFACTURER + " TEXT , " +
                PartsRunnerContract.StuffEntry.C_MODEL_YEAR + " TEXT, " +
                PartsRunnerContract.StuffEntry.C_MODEL + " TEXT, " +
                PartsRunnerContract.StuffEntry.C_MODEL_NO + " TEXT, " +
                PartsRunnerContract.StuffEntry.C_SERIAL_NO + " TEXT, " +
                PartsRunnerContract.StuffEntry.C_NOTES + " TEXT );";

        sqLiteDatabase.execSQL(SQL_CREATE_STUFF_TABLE);
    }


    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // Note that this only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application.
        // Currently the next line wipes out all user data and starts with a fresh DB Table
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + PartsRunnerContract.StuffEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}

