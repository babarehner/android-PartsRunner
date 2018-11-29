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

package com.babarehner.android.partsrunner;

 import android.app.LoaderManager;
 import android.content.ContentValues;
 import android.content.CursorLoader;
 import android.content.DialogInterface;
 import android.content.Intent;
 import android.content.Loader;
 import android.database.Cursor;
 import android.database.CursorWrapper;
 import android.net.Uri;
 import android.os.Bundle;
 import android.support.v4.app.DialogFragment;
 import android.support.v4.app.NavUtils;
 import android.support.v4.view.MenuItemCompat;
 import android.support.v7.app.AlertDialog;
 import android.support.v7.app.AppCompatActivity;
 import android.support.v7.widget.ShareActionProvider;
 import android.text.TextUtils;
 import android.util.Log;
 import android.view.Menu;
 import android.view.MenuItem;
 import android.view.MotionEvent;
 import android.view.View;
 import android.widget.AdapterView;
 import android.widget.Button;
 import android.widget.EditText;
 import android.widget.SimpleCursorAdapter;
 import android.widget.Spinner;
 import android.widget.TextView;
 import android.widget.Toast;

 import com.babarehner.android.partsrunner.data.PartsRunnerContract;
 import com.babarehner.android.partsrunner.data.PartsRunnerDBHelper;


 public class AddEditItemActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

     public final String LOG_TAG = AddEditItemActivity.class.getSimpleName();

     public static final int EXISTING_ADD_EDIT_MACHINE_LOADER = 0;
     public static final int LOADER_EQUIP_TYPE = 1;

     private Uri mCurrentMachineUri;
     private Uri mCurrentEquipTypeUri;

     private Spinner mSpinEquipType;
     SimpleCursorAdapter mSpinAdapter;

     private EditText mEditTextYear;
     private EditText mEditTextManufacturer;
     private EditText mEditTextModel;
     private EditText mEditTextModelNum;
     private EditText mEditTextSerialNum;
     private EditText mEditTextItemNum;
     private EditText mEditTextNotes;
     private Button mButtonModelYear;

     private String mSpinVal = "";

     private boolean mMachineChanged = false;   // When edit change made to an exercise row

     private ShareActionProvider mShareActionProvider;

     // Touch Listener to check if changes made to a book
     private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
         @Override
         public boolean onTouch(View v, MotionEvent event) {
             mMachineChanged = true;
             // following line was added to suppress warning for not programming for disabled
             // v.performClick();
             // above line caused date picker to hang up- probably because a click needed to be handled
             // in an onTouch event for date picker.
             return false;
         }
     };


     protected void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
         setContentView(R.layout.activity_add_edit_item);

         //Get intent and get data from intent
         Intent intent = getIntent();
         mCurrentMachineUri = intent.getData();

         // intitialize this loader first to load spinner
         getLoaderManager().initLoader(LOADER_EQUIP_TYPE, null,
                 AddEditItemActivity.this);

         // If the intent does not contain a single item-  Uri FAB clicked
         if (mCurrentMachineUri == null) {
             // set page header to add exercise
             setTitle(getString(R.string.activity_add_edit_item_title_add_machine));
             // take out the delete menu
             invalidateOptionsMenu();
         } else {
             // set page header to edit exercise
             setTitle(getString(R.string.activity_add_edit_item_title_edit_machine));
             getLoaderManager().initLoader(EXISTING_ADD_EDIT_MACHINE_LOADER, null,
                     AddEditItemActivity.this);
         }

         mSpinEquipType = findViewById(R.id.sp_machine_type);
         mEditTextManufacturer = findViewById(R.id.et_manufacturer);
         mEditTextYear =  findViewById(R.id.et_model_year);
         mButtonModelYear = (Button) findViewById(R.id.pick_year);
         mEditTextModel = findViewById(R.id.et_model);
         mEditTextModelNum = findViewById(R.id.et_model_num);
         mEditTextSerialNum = findViewById(R.id.et_serial_num);
         mEditTextItemNum = findViewById(R.id.et_item_num);
         mEditTextNotes = findViewById(R.id.et_notes);

         mSpinAdapter = new SimpleCursorAdapter(getApplicationContext(), android.R.layout.simple_spinner_item,
                 null, new String[]{PartsRunnerContract.EquipmentType.C_EQUIPMENT_TYPE},
                 new int[] {android.R.id.text1}, 0);
         mSpinAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
         mSpinEquipType.setAdapter(mSpinAdapter);

         mSpinEquipType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
             @Override
             public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                 CursorWrapper cw;  //wtf do I have to use a CursorWrapper when mucking with a db
                 cw = (CursorWrapper) parent.getItemAtPosition(position);
                 mSpinVal = String.valueOf(cw.getString(1)); //1 is position of value
             }
             @Override
             public void onNothingSelected(AdapterView<?> parent) {
                 mSpinVal = "";
             }
         });

         // Set up Touch Listener on all input fields to see if a field has been modified
         mSpinEquipType.setOnTouchListener(mTouchListener);
         mEditTextManufacturer.setOnTouchListener(mTouchListener);
         mButtonModelYear.setOnTouchListener(mTouchListener);
         mEditTextModel.setOnTouchListener(mTouchListener);
         mEditTextModelNum.setOnTouchListener(mTouchListener);
         mEditTextSerialNum.setOnTouchListener(mTouchListener);
         mEditTextItemNum.setOnTouchListener(mTouchListener);
         mEditTextNotes.setOnTouchListener(mTouchListener);
     }


     @Override
     public Loader<Cursor> onCreateLoader(int loaderId, Bundle bundle) {
         Loader<Cursor> loader = null; // returns null if not either case
         switch (loaderId){
             case EXISTING_ADD_EDIT_MACHINE_LOADER:
                 String[] projectionMachines = {PartsRunnerContract.MachineEntry._IDM,
                         PartsRunnerContract.MachineEntry.C_MACHINE_TYPE,
                         PartsRunnerContract.MachineEntry.C_MODEL_YEAR,
                         PartsRunnerContract.MachineEntry.C_MANUFACTURER,
                         PartsRunnerContract.MachineEntry.C_MODEL,
                         PartsRunnerContract.MachineEntry.C_MODEL_NUM,
                         PartsRunnerContract.MachineEntry.C_SERIAL_NUM,
                         PartsRunnerContract.MachineEntry.C_MACHINE_NUM,
                         PartsRunnerContract.MachineEntry.C_NOTES};
                 // new loader for new thread
                 loader = new CursorLoader(this, mCurrentMachineUri, projectionMachines, null,
                     null, null);
                 break;
             case LOADER_EQUIP_TYPE:
                 // get the URI for equipment types
                 //String queryUri = PartsRunnerContract.EquipmentType.EQUIP_TYPE_URI.toString();
                 //mCurrentEquipTypeUri = Uri.parse(queryUri);
                 mCurrentEquipTypeUri = PartsRunnerContract.EquipmentType.EQUIP_TYPE_URI;
                 String[] projectionEquipType = {PartsRunnerContract.EquipmentType._IDT,
                         PartsRunnerContract.EquipmentType.C_EQUIPMENT_TYPE};
                 loader = new CursorLoader(this, mCurrentEquipTypeUri, projectionEquipType, null,
                         null, null);
                 break;
         }

         return loader;
     }

     @Override
     public void onLoadFinished(Loader<Cursor> loader, Cursor c) {
         switch (loader.getId()){
             case EXISTING_ADD_EDIT_MACHINE_LOADER:
                 // move to the only row in the cursor

                 if (c.moveToFirst()) {
                     int machineTypeColIndex = c.getColumnIndex(PartsRunnerContract.MachineEntry.C_MACHINE_TYPE);
                     int modelYearColIndex = c.getColumnIndex(PartsRunnerContract.MachineEntry.C_MODEL_YEAR);
                     int manufacturerColIndex = c.getColumnIndex(PartsRunnerContract.MachineEntry.C_MANUFACTURER);
                     int modelColIndex = c.getColumnIndex(PartsRunnerContract.MachineEntry.C_MODEL);
                     int modelNumColIndex = c.getColumnIndex(PartsRunnerContract.MachineEntry.C_MODEL_NUM);
                     int serialNumColIndex = c.getColumnIndex(PartsRunnerContract.MachineEntry.C_SERIAL_NUM);
                     int machineNumColIndex = c.getColumnIndex(PartsRunnerContract.MachineEntry.C_MACHINE_NUM);
                     int notesColIndex = c.getColumnIndex(PartsRunnerContract.MachineEntry.C_NOTES);

                     // use index to pull data out of the cursor
                     String machineType = c.getString(machineTypeColIndex);
                     String modelYear = c.getString(modelYearColIndex);
                     String manufacturer = c.getString(manufacturerColIndex);
                     String model = c.getString(modelColIndex);
                     String modelNum = c.getString(modelNumColIndex);
                     String serialNum = c.getString(serialNumColIndex);
                     String machineNum = c.getString(machineNumColIndex);
                     String notes = c.getString(notesColIndex);

                      // of all the ways I tried mSpinQuipType (not adapter) & CursorWrapper
                     // were the only way I could get the string from the spinner
                     CursorWrapper  cursorWrapper;
                     int pos = 0;
                     // Go through the spinner list and find a match with item in db
                     for (int i = 0; i < mSpinEquipType.getCount(); i++){
                         cursorWrapper = (CursorWrapper) mSpinEquipType.getItemAtPosition(i);
                         if (String.valueOf(cursorWrapper.getString(1)).equals
                                 (machineType)){
                             pos = i;
                             break;
                         }
                     }

                     mSpinEquipType.setSelection(pos);

                     // update the rest of the views
                     mEditTextYear.setText(modelYear);
                     mEditTextManufacturer.setText(manufacturer);
                     mEditTextModel.setText(model);
                     mEditTextModelNum.setText(modelNum);
                     mEditTextSerialNum.setText(serialNum);
                     mEditTextItemNum.setText(machineNum);
                     mEditTextNotes.setText(notes);

                 }

                 break;
             case LOADER_EQUIP_TYPE:
                 mSpinAdapter.swapCursor(c);
                 break;
         }
     }



     @Override
     public void onLoaderReset(Loader<Cursor> loader) {
         // If invalid Loader clear data from input field
         switch (loader.getId()){
             case EXISTING_ADD_EDIT_MACHINE_LOADER:
                 //mSpinEquipType.setSelection(0);
                 mEditTextYear.setText("");
                 mEditTextManufacturer.setText("");
                 mEditTextModel.setText("");
                 mEditTextModelNum.setText("");
                 mEditTextSerialNum.setText("");
                 mEditTextItemNum.setText("");
                 mEditTextNotes.setText("");
                 break;
             case LOADER_EQUIP_TYPE:
                 // if invalid loader clear data from field
                 mSpinAdapter.swapCursor(null);
                 break;
         }

     }

     @Override   // set up the menu the first time
     public boolean onCreateOptionsMenu(Menu m) {
         getMenuInflater().inflate(R.menu.menu_add_edit_item_activity, m);

         // relate mShareActionProvider to share e-mail menu item
         mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider
                 (m.findItem(R.id.action_share_email));
         return true;
     }


     @Override   // hide delete menu item when adding a new exercise
     public boolean onPrepareOptionsMenu(Menu m) {
         super.onPrepareOptionsMenu(m);
         // if this is add an exercise, hide "delete" menu item

         if (mCurrentMachineUri == null) {
             MenuItem deleteItem = m.findItem(R.id.action_delete);
             deleteItem.setVisible(false);
             MenuItem shareEMailItem = m.findItem(R.id.action_share_email);
             shareEMailItem.setVisible(false);
         }
         return true;
     }



     @Override        // Select from the options menu
     public boolean onOptionsItemSelected(MenuItem item) {
         switch (item.getItemId()) {
             case R.id.action_save:
                 saveMachine();
                 finish();       // exit activity
                 return true;
             case R.id.action_share_email:
                 if (mShareActionProvider != null) {
                     mShareActionProvider.setShareIntent(shareEMail());
                 }
                 // Intent.createChooser(i, " Create Chooser");
                 Log.v(LOG_TAG, "in action share EMail after String Builder");
                 return true;
             //case R.id.action_share_text:
                 //return true;
             case R.id.action_delete:
                 // Alert Dialog for deleting one book
                 showDeleteConfirmationDialog();
                 return true;
             // this is the <- button on the header
             case android.R.id.home:
                 // book has not changed
                 if (!mMachineChanged) {
                     NavUtils.navigateUpFromSameTask(AddEditItemActivity.this);
                     return true;
                 }
                 // set up dialog to warn user about unsaved changes
                 DialogInterface.OnClickListener discardButtonClickListener =
                         new DialogInterface.OnClickListener() {
                             @Override
                             public void onClick(DialogInterface dialog, int i) {
                                 //user click discard. Navigate up to parent activity
                                 NavUtils.navigateUpFromSameTask(AddEditItemActivity.this);
                             }
                         };
                 // show user they have unsaved changes
                 showUnsavedChangesDialog(discardButtonClickListener);
                 return true;
         }
         return super.onOptionsItemSelected(item);
     }



     private void saveMachine() {

         // read from EditText input fields
         // TODO get data from Spinner
         String equipmentType = mSpinVal;
         String manufacturerString = mEditTextManufacturer.getText().toString().trim();
         String modelYearString = mEditTextYear.getText().toString().trim();
         String modelString = mEditTextModel.getText().toString().trim();
         String modelNumString = mEditTextModelNum.getText().toString().trim();
         String serialNumString = mEditTextSerialNum.getText().toString().trim();
         String machineNumString = mEditTextItemNum.getText().toString().trim();
         String notesString = mEditTextNotes.getText().toString().trim();


         ContentValues values = new ContentValues();
         values.put(PartsRunnerContract.MachineEntry.C_MACHINE_TYPE, equipmentType);
         values.put(PartsRunnerContract.MachineEntry.C_MANUFACTURER, manufacturerString);
         values.put(PartsRunnerContract.MachineEntry.C_MODEL_YEAR, modelYearString);
         values.put(PartsRunnerContract.MachineEntry.C_MODEL, modelString);
         values.put(PartsRunnerContract.MachineEntry.C_MODEL_NUM, modelNumString);
         values.put(PartsRunnerContract.MachineEntry.C_SERIAL_NUM, serialNumString);
         values.put(PartsRunnerContract.MachineEntry.C_MACHINE_NUM, machineNumString);
         values.put(PartsRunnerContract.MachineEntry.C_NOTES, notesString);

         if (mCurrentMachineUri == null) {
             // a new machine
             // ***********
             Log.v(LOG_TAG, "in saveRecord " + PartsRunnerContract.MachineEntry.PARTS_RUNNER_URI.toString() + "\n" + values);

             Uri newUri = getContentResolver().insert(PartsRunnerContract.MachineEntry.PARTS_RUNNER_URI, values);
             // ************

             if (newUri == null) {
                 Toast.makeText(this, getString(R.string.insert_machine_failed),
                         Toast.LENGTH_SHORT).show();
             } else {
                 Toast.makeText(this, getString(R.string.insert_machine_good),
                         Toast.LENGTH_SHORT).show();
             }
         } else {
             // existing book so update with content URI and pass in ContentValues
             int rowsAffected = getContentResolver().update(mCurrentMachineUri, values, null, null);
             if (rowsAffected == 0) {
                 // TODO Check db- Text Not Null does not seem to be working or entering
                 // "" does not mean NOT Null- there must be an error message closer to the db!!!
                 Toast.makeText(this, getString(R.string.edit_update_machine_failed),
                         Toast.LENGTH_SHORT).show();
             } else {
                 Toast.makeText(this, getString(R.string.edit_update_machine_success),
                         Toast.LENGTH_SHORT).show();
             }

         }
     }


     // delete exercise from DB
     private void deleteExercise(){
         if (mCurrentMachineUri != null) {
             int rowsDeleted = getContentResolver().delete(mCurrentMachineUri, null, null);
             if (rowsDeleted == 0) {
                 Toast.makeText(this, getString(R.string.delete_machine_failure),
                         Toast.LENGTH_SHORT).show();
             } else {
                 Toast.makeText(this, getString(R.string.delete_machine_successful),
                         Toast.LENGTH_SHORT).show();
             }
         }
         finish();
     }


     private void showDeleteConfirmationDialog() {
         // Create an AlertDialog.Builder, set message and click
         // listeners for positive and negative buttons
         AlertDialog.Builder builder = new AlertDialog.Builder(this);
         builder.setMessage(R.string.delete_dialog_msg);
         builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
             @Override
             public void onClick(DialogInterface dialog, int which) {
                 // User clicked delete so delete
                 deleteExercise();
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
                 // user clicked the "keep editing" button. Dismiss dialog and keep editing
                 if (dialog !=null) { dialog.dismiss();}
             }
         });

         AlertDialog alertDialog = builder.create();
         alertDialog.show();
     }


     // Override the activity's normal back button. If book has changed create a
     // discard click listener that closed current activity.
     @Override
     public void onBackPressed() {
         if (!mMachineChanged) {
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

     public void showDatePickerDialog(View v){
         DialogFragment yearFragment = new DatePickerFragment();
         yearFragment.show(getSupportFragmentManager(), "datePicker");
     }

     public Intent shareEMail(){
         Intent intent = new Intent(Intent.ACTION_SEND);

         StringBuilder sb = new StringBuilder(buildShareString());
         String s = sb.toString();
         Log.v(LOG_TAG, "String from String Builder " + s);
         if (mShareActionProvider != null){
             // spent all day looking for why I couldn't implement ACTION/SEND
             // misspelled "text/plain as textPlain
             intent.setType("text/plain");
             intent.putExtra(Intent.EXTRA_TEXT, s);
         } else {
             Log.v(LOG_TAG, "ShareActionProvider is null");
         }

         return intent;
     }

     private void shareText() {

         StringBuilder share = buildShareString();
         ShareDataFragment shareFragment = new ShareDataFragment();
     }

     public StringBuilder buildShareString(){

         StringBuilder sb = new StringBuilder();

         EditText mEditTextManufacturer = findViewById(R.id.et_manufacturer);
         EditText mEditTextYear =  findViewById(R.id.et_model_year);
         EditText mEditTextModel = findViewById(R.id.et_model);
         EditText mEditTextModelNum = findViewById(R.id.et_model_num);
         EditText mEditTextSerialNum = findViewById(R.id.et_serial_num);

         sb.append(mEditTextYear.getText().toString()).append(" ")
                 .append(mEditTextManufacturer.getText().toString()).append(" ")
                 .append(mEditTextModel.getText().toString()).append("\n")
                 .append("Model Number: ").append(mEditTextModelNum.getText().toString()).append("\n")
                 .append("Serial Number: ").append(mEditTextSerialNum.getText().toString());
         Log.v(LOG_TAG, "String Builder " + sb);

         return sb;

     }

 }
