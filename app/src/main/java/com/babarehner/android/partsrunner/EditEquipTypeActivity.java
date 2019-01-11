package com.babarehner.android.partsrunner;

import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.babarehner.android.partsrunner.data.PartsRunnerContract;

//TODO add onTouch Listener and wire up to messages
public class EditEquipTypeActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private final String LOG_TAG = EditEquipTypeActivity.class.getSimpleName();

    private Uri mCurrentEditEquipUri;
    static final int EXISTING_EDIT_EQUIP_LOADER = 10;

    private String mEditEquip;
    private boolean mEditEquipChanged;
    private EditText mEditEquipEditText;

    // Touch listener to check if changes made to a record
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event){
            mEditEquipChanged = true;
            v.performClick();
            return false;
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_equip_type);

        Intent intent = getIntent();
        mCurrentEditEquipUri = intent.getData();

        if (mCurrentEditEquipUri == null) {
            setTitle("Add an Equipment Type");
        } else {
            setTitle("Edit Equipment Type");
            getLoaderManager().initLoader(EXISTING_EDIT_EQUIP_LOADER, null, EditEquipTypeActivity.this);
        }

        mEditEquipEditText = findViewById(R.id.edit_equip_types);
        mEditEquipEditText.setOnTouchListener(mTouchListener);

    }


    @NonNull
    @Override
    public Loader onCreateLoader(int id, @Nullable Bundle args) {
        String[] projection = {PartsRunnerContract.EquipmentType._IDT,
                PartsRunnerContract.EquipmentType.C_EQUIPMENT_TYPE};

        return new CursorLoader(this, mCurrentEditEquipUri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor c) {

        // move to the only row in the cursor
        if (c.moveToFirst()){
            int equpmentTypeColIndex = c.getColumnIndex(PartsRunnerContract.EquipmentType.C_EQUIPMENT_TYPE);
            // use the index to pull the data out
            mEditEquip = c.getString(equpmentTypeColIndex);
            // update the text view
            mEditEquipEditText.setText(mEditEquip);
        }

    }

    @Override
    public void onLoaderReset(Loader loader) {
        // if invalid loader clear data from input field
        mEditEquipEditText.setText("");
    }

    // Options menu automatially called from onCreate I believe
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_equip_type_activity, menu);
        return true;
    }

    // Select from the options menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int menuItem = item.getItemId();
        switch (menuItem) {
            case R.id.action_save_edit_equip_type:
                saveRecord();
                finish();       // exit activity
                return true;
            case R.id.action_delete_edit_equip_type:
                // Alert Dialog for deleting one record
                showDeleteConfirmationDialog();
                return true;
            // this is the <- button on the toolbar
            case android.R.id.home:
                // record has not changed
                if (!mEditEquipChanged) {
                    NavUtils.navigateUpFromSameTask(EditEquipTypeActivity.this);
                    return true;
                }
                // set up dialog to warn user about unsaved changes
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {
                                //user click discard. Navigate up to parent activity
                                NavUtils.navigateUpFromSameTask(EditEquipTypeActivity.this);
                            }
                        };
                // show user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // hide delete menu item when adding a new record
    @Override
    public boolean onPrepareOptionsMenu(Menu m) {
        super.onPrepareOptionsMenu(m);
        // if this is add a record, hide "delete" menu item
        if (mCurrentEditEquipUri == null) {
            MenuItem deleteItem = m.findItem(R.id.action_delete_edit_equip_type);
            deleteItem.setVisible(false);
        }

        return true;
    }

    // Override the activity's normal back button. If record has changed create a
    // discard click listener that closed current activity.
    @Override
    public void onBackPressed() {
        if (!mEditEquipChanged) {
            super.onBackPressed();
            return;
        }
        //otherwise if there are unsaved changes setup a dialog to warn the  user
        //handles the user confirming that changes should be made
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        // user clicked "Discard" button, close the current activity
                        finish();
                    }
                };

        // show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }


    private void saveRecord() {

        String equipType =  mEditEquipEditText.getText().toString().trim();

        // if the date field is left blank do nothing
        if (mCurrentEditEquipUri == null & TextUtils.isEmpty(equipType)) {
            Toast.makeText(this, getString(R.string.missing_equip_type), Toast.LENGTH_LONG).show();
            return;
        }

        ContentValues values = new ContentValues();
        values.put(PartsRunnerContract.EquipmentType.C_EQUIPMENT_TYPE, equipType);

        if (mCurrentEditEquipUri == null) {
            // a new record
            Log.v(LOG_TAG, "in saveRecord " + PartsRunnerContract.EquipmentType.EQUIP_TYPE_URI.toString() + "\n" + values);
            Uri newUri = getContentResolver().insert(PartsRunnerContract.EquipmentType.EQUIP_TYPE_URI, values);

            if (newUri == null) {
                Toast.makeText(this, getString(R.string.parts_runner_provider_insert_record_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.parts_runner_provider_insert_record_succeeded),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            // existing record so update with content URI and pass in ContentValues
            int rowsAffected = getContentResolver().update(mCurrentEditEquipUri, values, null, null);
            if (rowsAffected == 0) {
                // TODO Check db- Text Not Null does not seem to be working or entering
                // "" does not mean NOT Null- there must be an error message closer to the db!!!
                Toast.makeText(this, getString(R.string.edit_update_record_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.edit_update_record_success),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }


    // delete record from db
    private void deleteRecord(){
        if (mCurrentEditEquipUri != null) {
            int rowsDeleted = getContentResolver().delete(mCurrentEditEquipUri, null, null);
            if (rowsDeleted == 0) {
                Toast.makeText(this, getString(R.string.delete_record_failure),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.delete_record_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }


    private void showDeleteConfirmationDialog() {
        // Create and AlertDialog.Builder, set message and click
        // listeners for positive and negative buttons
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User clicked delete so delete
                deleteRecord();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // user clicked cancel, dismiss dialog, continue editing
                if (dialog != null) {dialog.dismiss();}
            }
        });
        // Create and show dialog
        AlertDialog alertD = builder.create();
        alertD.show();
    }


    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener){
        // Create AlertDialogue.Builder amd set message and click listeners
        // for positive and negative buttons in dialogue.
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // user clicked the "keep eiditing" button. Dismiss dialog and keep editing
                if (dialog !=null) { dialog.dismiss();}
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }



}
