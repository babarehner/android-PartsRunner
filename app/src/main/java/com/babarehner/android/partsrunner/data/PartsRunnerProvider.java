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
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.babarehner.android.partsrunner.data.PartsRunnerContract;
import com.babarehner.android.partsrunner.data.PartsRunnerContract.StuffEntry;

import static android.provider.BaseColumns._ID;
import static com.babarehner.android.partsrunner.data.PartsRunnerContract.PARTS_RUNNER_AUTHORITY;
import static com.babarehner.android.partsrunner.data.PartsRunnerContract.PATH_TABLE_NAME;
import static com.babarehner.android.partsrunner.data.PartsRunnerContract.StuffEntry.C_EQUIP_TYPE;
import static com.babarehner.android.partsrunner.data.PartsRunnerContract.StuffEntry.STUFF_ITEM_TYPE;
import static com.babarehner.android.partsrunner.data.PartsRunnerContract.StuffEntry.STUFF_LIST_TYPE;
import static com.babarehner.android.partsrunner.data.PartsRunnerContract.StuffEntry.TABLE_NAME;

 public class PartsRunnerProvider extends ContentProvider {

     public static final String LOG_TAG = PartsRunnerProvider.class.getSimpleName();

     private static final int STUFF = 100;
     private static final int STUFF_ID = 101;

     private PartsRunnerDBHelper mDBHelper;

     private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

     static {
         sUriMatcher.addURI(PARTS_RUNNER_AUTHORITY, PATH_TABLE_NAME, STUFF);
         sUriMatcher.addURI(PARTS_RUNNER_AUTHORITY, PATH_TABLE_NAME + "/#", STUFF_ID);
     }


     @Override
     public boolean onCreate() {
         mDBHelper = new PartsRunnerDBHelper(getContext());
         return true;
     }


     @Nullable
     @Override
     public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                         @Nullable String[] selectionArgs, @Nullable String sortOrder) {
         // Create or open a database to write to it
         SQLiteDatabase db = mDBHelper.getReadableDatabase();

         Cursor c;

         int match = sUriMatcher.match(uri);
         switch (match) {
             case STUFF:
                 c = db.query(TABLE_NAME, projection, selection, selectionArgs, null,
                         null, sortOrder);
                 break;
             case STUFF_ID:
                 selection = _ID + "=?";
                 selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                 c = db.query(TABLE_NAME, projection, selection, selectionArgs,
                         null, null, sortOrder);
                 break;
             default:
                 throw new IllegalArgumentException("Cannot query unknown URI: " + uri);
         }

         // notify if the data at this URI changes, Then we need to update the cursor listener
         // attached is automatically notified with uri
         c.setNotificationUri(getContext().getContentResolver(), uri);

         return c;
     }


     @Nullable
     @Override
     public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
         final int match = sUriMatcher.match((uri));
         switch (match) {
             case STUFF:
                 return insertRecord(uri, values);
             default:
                 throw new IllegalArgumentException("Insertion is not supported for: " + uri);
         }
     }


     // Insert a record into the records table with the given content values. Return the new content uri
     // for that specific row in the database
     public Uri insertRecord(Uri uri, ContentValues values) {
         // Check the the practice type is not null
         String prac_type = values.getAsString(C_EQUIP_TYPE);
         if (prac_type == null) {
             throw new IllegalArgumentException("Equipment Type required to insert new Record");
         }


         SQLiteDatabase db = mDBHelper.getWritableDatabase();
         long id = db.insert(TABLE_NAME, null, values);
         Log.v(LOG_TAG, "Record not entered");
         if (id == -1) {
             Log.e(LOG_TAG, "Failed to insert row for " + uri);
             return null;
         }

         // notify all listeners that the data has changed for the TSTUFF table
         getContext().getContentResolver().notifyChange(uri, null);
         // return the new Uri with the ID of the newly inserted row appended to the db
         return ContentUris.withAppendedId(uri, id);
     }


     @Override
     public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
         final int match = sUriMatcher.match(uri);
         switch (match) {
             case STUFF:
                 return updateRecords(uri, values, selection, selectionArgs);
             case STUFF_ID:
                 selection = _ID + "=?";
                 selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                 return updateRecords(uri, values, selection, selectionArgs);
             default:
                 throw new IllegalArgumentException("Update is not supported for: " + uri);
         }
     }


     private int updateRecords(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
         // if there are no values quit
         if (values.size() == 0) {
             return 0;
         }

         // check that the practice type is not empty
         if (values.containsKey(C_EQUIP_TYPE)) {
             String prac_name = values.getAsString(C_EQUIP_TYPE);
             // check again
             if (prac_name == null) {
                 throw new IllegalArgumentException("Exercise requires a practice type- in updateRecords");
             }
         }

         SQLiteDatabase db = mDBHelper.getWritableDatabase();
         int rows_updated = db.update(TABLE_NAME, values, selection, selectionArgs);
         if (rows_updated != 0) {
             getContext().getContentResolver().notifyChange(uri, null);
         }
         return rows_updated;
     }


     @Override
     public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
         int rowsDeleted;
         SQLiteDatabase db = mDBHelper.getWritableDatabase();
         // final int match = sUriMatcher.match(uri);
         selection = _ID + "=?";
         selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
         rowsDeleted = db.delete(TABLE_NAME, selection, selectionArgs);

         if (rowsDeleted != 0) {
             // Notify all listeners that the db has changed
             getContext().getContentResolver().notifyChange(uri, null);
         }

         return rowsDeleted;
     }


     @Nullable
     @Override
     public String getType(@NonNull Uri uri) {
         final int match = sUriMatcher.match(uri);
         switch (match) {
             case STUFF:
                 return STUFF_LIST_TYPE;
             case STUFF_ID:
                 return STUFF_ITEM_TYPE;
             default:
                 throw new IllegalStateException("Unknown Uri: " + uri + "with match: " + match);
         }
     }
 }


