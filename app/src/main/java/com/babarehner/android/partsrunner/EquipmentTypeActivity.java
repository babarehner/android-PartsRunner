package com.babarehner.android.partsrunner;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.babarehner.android.partsrunner.data.PartsRunnerContract;

public class EquipmentTypeActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EQUIP_TYPE_LOADER = 0;
    EquipTypeCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equipment_type);

        setTitle("Practice Aids");

        // Create a floating action button Need to add
        // compile 'com.android.support:design:26.1.0' to build gradle module
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.pa_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EquipmentTypeActivity.this, EditEquipTypeActivity.class);
                startActivity(intent);
            }
        });


        ListView equipTypeListView = (ListView) findViewById(R.id.practice_aid_list);
        // display the empty view
        View emptyView = findViewById(R.id.equipment_type_empty_view);
        equipTypeListView.setEmptyView(emptyView);

        mCursorAdapter = new EquipTypeCursorAdapter(this, null);
        equipTypeListView.setAdapter(mCursorAdapter);
        equipTypeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int pos, long id) {
                Intent intent = new Intent(EquipmentTypeActivity.this, EditEquipTypeActivity.class);
                Uri currentPracticeAidUri = ContentUris.withAppendedId(
                        PartsRunnerContract.EquipmentType.EQUIP_TYPE_URI, id);
                intent.setData(currentPracticeAidUri);
                startActivity(intent);

            }
        });


        getLoaderManager().initLoader(EQUIP_TYPE_LOADER, null, this);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        // Define a projections with the columns we are interested in
        // CursorAdapter requires column title of "_Id"
        String[] projection = {PartsRunnerContract.EquipmentType._IDT,
                PartsRunnerContract.EquipmentType.C_EQUIPMENT_TYPE};

        // This loader will execute the ContentProvider's query  method on a background thread
        return new CursorLoader(this,                           // parent activity context
                PartsRunnerContract.EquipmentType.EQUIP_TYPE_URI,       // ProviderContent URI
                projection,
                null,
                null,
                PartsRunnerContract.EquipmentType.C_EQUIPMENT_TYPE + " ASC");
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // update(@link EquipTypeCursorAdapter) with this new cursor containing update Equpment Type data
        mCursorAdapter.swapCursor(data);
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Callback called when the data needs to be deleted- use null
        mCursorAdapter.swapCursor(null);
    }
}
