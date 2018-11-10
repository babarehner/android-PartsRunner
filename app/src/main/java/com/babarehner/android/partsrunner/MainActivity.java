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
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu m) {
        super.onPrepareOptionsMenu(m);
        // hide menu items with menu item.setVisible(false)
        return true;
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args){
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
                PartsRunnerContract.MachineEntry.C_MACHINE_TYPE + " ASC");
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
