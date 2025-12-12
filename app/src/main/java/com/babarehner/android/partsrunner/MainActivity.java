/*
 * Copyright (C) 2018,2025 Mike Rehner
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

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;

import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.babarehner.android.partsrunner.data.PartsRunnerContract;

import static com.babarehner.android.partsrunner.data.PartsRunnerContract.MachineEntry.PARTS_RUNNER_URI;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int MACHINE_LOADER = 0;
    MachineCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddEditItemActivity.class);
                startActivity(intent);
            }
        });

        ListView machinesListView = (ListView) findViewById(R.id.list_machines);
        View emptyView = findViewById(R.id.empty_view);
        machinesListView.setEmptyView(emptyView);

        mCursorAdapter = new MachineCursorAdapter(this, null);
        machinesListView.setAdapter(mCursorAdapter);
        machinesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int pos, long id) {
                Intent intent = new Intent(MainActivity.this, AddEditItemActivity.class);
                Uri currentMainUri = ContentUris.withAppendedId(
                        PARTS_RUNNER_URI, id);
                intent.setData(currentMainUri);
                startActivity(intent);
            }
        });

        getLoaderManager().initLoader(MACHINE_LOADER, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_edit_equip_types) {
            Intent intent = new Intent(MainActivity.this, EquipmentTypeActivity.class);
            startActivity(intent);
            return true;  // Keep from falling through to default
        } else if (id == R.id.action_backup_database){
            // for Android 8 and below, permissions granted at install time, request permission. Need +> sdk 23 to ask permission
            // Only ask for runtime permissions on Android 6.0 (Marshmallow- SDK 23) to above Android 9.0 (Pie- SDK 28).
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                // Check if permission is already granted
                if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    // Permission has not been granted, so request it.
                    // The result is handled in onRequestPermissionsResult().
                    requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                } else {
                    triggerBackup();
                }
            } else {
                // On versions below Marshmallow (API < 23), permissions are granted at install time.
                // We don't need to ask. Just proceed with the backup.
                triggerBackup();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Handle the result of the permission request (for Android 9 and below)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted, proceed with backup
                triggerBackup();
            } else {
                // Permission was denied
                Toast.makeText(this, "Storage permission is required to back up the database.", Toast.LENGTH_LONG).show();
            }
        }
    }


    //Creates an instance of the backup helper and executes the backup.
    private void triggerBackup() {
        DatabaseBackupHelper backupHelper = new DatabaseBackupHelper(this);
        backupHelper.executeBackup();
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu m) {
        super.onPrepareOptionsMenu(m);
        // hide menu items with menu item.setVisible(false)
        return true;
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args){
        String sortOrder = PartsRunnerContract.MachineEntry.C_MACHINE_TYPE + " ASC"
                + ", " + PartsRunnerContract.MachineEntry.C_MODEL_YEAR + " ASC"
                + ", " + PartsRunnerContract.MachineEntry.C_MANUFACTURER + " ASC";
        String [] projection = {PartsRunnerContract.MachineEntry._IDM,
                PartsRunnerContract.MachineEntry.C_MACHINE_TYPE,
                PartsRunnerContract.MachineEntry.C_MODEL_YEAR,
                PartsRunnerContract.MachineEntry.C_MANUFACTURER,
                PartsRunnerContract.MachineEntry.C_MODEL };

        return new CursorLoader(this,
                PARTS_RUNNER_URI,
                projection,
                null,
                null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }
}
