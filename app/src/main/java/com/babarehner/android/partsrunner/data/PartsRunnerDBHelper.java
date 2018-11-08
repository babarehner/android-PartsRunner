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
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;


 // Helps run the local Parts Runner DB
public class PartsRunnerDBHelper extends SQLiteOpenHelper {

    // To allow for changes in DB versioning and keeping user data
    private static final int DB_VERSION = 1;

    static final String DB_NAME = "machines.db";

    public PartsRunnerDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_MACHINES_TABLE = "CREATE TABLE " +
                PartsRunnerContract.MachineEntry.MACHINE_TABLE_NAME + " (" +
                PartsRunnerContract.MachineEntry._IDM + " INTEGER PRIMARY KEY, " +
                PartsRunnerContract.MachineEntry.C_MACHINE_TYPE + " TEXT, " +
                PartsRunnerContract.MachineEntry.C_MANUFACTURER + " TEXT , " +
                PartsRunnerContract.MachineEntry.C_MODEL_YEAR + " INTEGER, " +
                PartsRunnerContract.MachineEntry.C_MODEL + " TEXT, " +
                PartsRunnerContract.MachineEntry.C_MODEL_NUM + " TEXT, " +
                PartsRunnerContract.MachineEntry.C_SERIAL_NUM + " TEXT, " +
                PartsRunnerContract.MachineEntry.C_MACHINE_NUM + " TEXT, " +
                PartsRunnerContract.MachineEntry.C_NOTES + " TEXT );";

        sqLiteDatabase.execSQL(SQL_CREATE_MACHINES_TABLE);

        final String SQL_CREATE_EQUIPMENT_TYPE_TABLE = "CREATE TABLE " +
                PartsRunnerContract.EquipmentType.EQUPMENT_TABLE_NAME + " (" +
                PartsRunnerContract.EquipmentType._IDT + " INTEGER PRIMARY KEY, " +
                PartsRunnerContract.EquipmentType.C_EQUIPMENT_TYPE + " TEXT );";

        sqLiteDatabase.execSQL(SQL_CREATE_EQUIPMENT_TYPE_TABLE);

        //  Load the Practice Aids Table with values the first time the db is created
        String[] equipmentTypes = { "Computers", "Refrigerators", "Vehicles" };

        for (String each : equipmentTypes){
            sqLiteDatabase.execSQL("INSERT INTO " + PartsRunnerContract.EquipmentType.EQUPMENT_TABLE_NAME
                    + " ( " + PartsRunnerContract.EquipmentType.C_EQUIPMENT_TYPE + " ) "
                    + " VALUES( "
                    + "'" + each + "'"  + ");");
        }
    }


    // TODO rawQuery not using asynctask or LOADER- should be updated later.
    public List<String> getEquipmentTypes(){
        List<String> equipmentTypes = new ArrayList<>();
        String fEquipmentTypeQuery = "SELECT * FROM " + PartsRunnerContract.EquipmentType.EQUPMENT_TABLE_NAME +
                " ORDER BY " + PartsRunnerContract.EquipmentType.C_EQUIPMENT_TYPE + ";" ;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(fEquipmentTypeQuery, null);
        if (c.moveToFirst()) {
            do {
                equipmentTypes.add(c.getString(1));
            } while (c.moveToNext());
        }
        // Log.v(LOG_TAG, "practice Aides " + practiceAides);
        c.close();
        db.close();
        return equipmentTypes;
    }


    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // Note that this only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application.
        // Currently the next line wipes out all user data and starts with a fresh DB Table
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + PartsRunnerContract.MachineEntry.MACHINE_TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + PartsRunnerContract.EquipmentType.EQUPMENT_TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

}

