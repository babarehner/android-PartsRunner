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

 import android.app.Activity;
 import android.app.DatePickerDialog;
 import android.app.Dialog;
 import android.app.LoaderManager;
 import android.content.ContentValues;
 import android.content.CursorLoader;
 import android.content.DialogInterface;
 import android.content.Intent;
 import android.content.Loader;
 import android.database.Cursor;
 import android.net.Uri;
 import android.os.Bundle;
 import android.support.v4.app.NavUtils;
 import android.support.v7.app.AlertDialog;
 import android.support.v7.app.AppCompatActivity;
 import android.text.TextUtils;
 import android.util.Log;
 import android.view.Menu;
 import android.view.MenuItem;
 import android.view.MotionEvent;
 import android.view.View;
 import android.widget.ArrayAdapter;
 import android.widget.Button;
 import android.widget.DatePicker;
 import android.widget.EditText;
 import android.widget.Spinner;
 import android.widget.TextView;
 import android.widget.Toast;

 import com.babarehner.android.partsrunner.data.PartsRunnerContract;

 import java.util.Calendar;

 public class AddEditItemActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

     public static final int EXISTING_ADD_EDIT_ITEM_LOADER = 0;

     private final String LOG_TAG = AddEditItemActivity.class.getSimpleName();

     private Uri mCurrentItemUri;

     private Spinner mSpinnerMachineType;
     private EditText mEditTextYear;
     private EditText mEditTextManufacturer;
     private EditText mEditTextModel;
     private EditText mEditTextModelNum;
     private EditText mEditTextSerialNum;
     private EditText mEditTextItemNum;
     private EditText mEditTextNotes;


     private TextView mTextViewYear;
     private Button mButtonPickYear;
     private int mYear;

     static final int DATE_DIALOG_ID = 99;

     private boolean mItemChanged = false;   // When edit change made to an exercise row

     // Touch Listener to check if changes made to a book
     private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
         @Override
         public boolean onTouch(View v, MotionEvent event) {
             mItemChanged = true;
             return false;
         }
     };


     protected void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
         setContentView(R.layout.activity_add_edit_item);

         //Get intent and get data from intent
         Intent intent = getIntent();
         mCurrentItemUri = intent.getData();
         // String str = mCurrentItemUri.toString();

         // If the intent does not contain a single item-  Uri FAB clicked
         if (mCurrentItemUri == null) {
             // set page header to add exercise
             setTitle(getString(R.string.activity_add_edit_item_title_add_machine));
             // take out the delete menu
             invalidateOptionsMenu();
         } else {
             // set page header to edit exercise
             setTitle(getString(R.string.activity_add_edit_item_title_edit_machine));
             getLoaderManager().initLoader(EXISTING_ADD_EDIT_ITEM_LOADER, null, AddEditItemActivity.this);
         }

         // initialization required or it crashes
         mTextViewYear = (TextView) findViewById(R.id.et_year);
         // Find all input views to read from
         mSpinnerMachineType = findViewById(R.id.sp_machine_type);
         mEditTextManufacturer = (EditText) findViewById(R.id.et_manufacturer);
         mEditTextModel = (EditText) findViewById(R.id.et_model);
         mEditTextModelNum = (EditText) findViewById(R.id.et_model_num);
         mEditTextSerialNum = (EditText) findViewById(R.id.et_serial_num);
         mEditTextItemNum = (EditText) findViewById(R.id.et_item_num);
         mEditTextNotes = findViewById(R.id.et_notes);

         // Set up Touch Listener on all input fields to see if a field has been modified
         mSpinnerMachineType.setOnTouchListener(mTouchListener);
         mEditTextManufacturer.setOnTouchListener(mTouchListener);
         mEditTextModel.setOnTouchListener(mTouchListener);
         mEditTextModelNum.setOnTouchListener(mTouchListener);
         mEditTextSerialNum.setOnTouchListener(mTouchListener);
         mEditTextNotes.setOnTouchListener(mTouchListener);

         // getYear();
     }


     @Override
     public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
         String[] projection = {PartsRunnerContract.MachineEntry._IDM,
                 PartsRunnerContract.MachineEntry.C_MACHINE_TYPE,
                 PartsRunnerContract.MachineEntry.C_MODEL_YEAR,
                 PartsRunnerContract.MachineEntry.C_MANUFACTURER,
                 PartsRunnerContract.MachineEntry.C_MODEL,
                 PartsRunnerContract.MachineEntry.C_MODEL_NUM,
                 PartsRunnerContract.MachineEntry.C_SERIAL_NUM,
                 PartsRunnerContract.MachineEntry.C_MACHINE_NUM,
                 PartsRunnerContract.MachineEntry.C_NOTES };
         // start a new thread
         return new CursorLoader(this, mCurrentItemUri, projection, null, null, null);
     }

     @Override
     public void onLoadFinished(Loader<Cursor> loader, Cursor c) {
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
             int modelYear = c.getInt(modelYearColIndex);
             String manufacturer = c.getString(manufacturerColIndex);
             String model = c.getString(modelColIndex);
             String modelNum = c.getString(modelNumColIndex);
             String serialNum = c.getString(serialNumColIndex);
             int machineNum = c.getInt(machineNumColIndex);
             String notes = c.getString(notesColIndex);

             // deal with the spinner view
             ArrayAdapter arrayMachineType = (ArrayAdapter) mSpinnerMachineType.getAdapter();
             int pos = arrayMachineType.getPosition(machineType);
             mSpinnerMachineType.setSelection(pos);mEditTextSerialNum.setText(serialNum);
             // update the rest of the views
             mEditTextYear.setText(modelYear);
             mEditTextManufacturer.setText(manufacturer);
             mEditTextModel.setText(model);
             mEditTextModelNum.setText(modelNum);
             mEditTextSerialNum.setText(serialNum);
             mEditTextItemNum.setText(machineNum);
             mEditTextNotes.setText(notes);

         }
     }


     @Override
     public void onLoaderReset(Loader<Cursor> loader) {
         // If invalid Loader clear data from input field
         mSpinnerMachineType.setSelection(0);
         mEditTextYear.setText("");
         mEditTextManufacturer.setText("");
         mEditTextModel.setText("");
         mEditTextModelNum.setText("");
         mEditTextSerialNum.setText("");
         mEditTextItemNum.setText("");
         mEditTextNotes.setText("");
     }

     // set up date picker
     public void getDate() {

         tvDate = (TextView) findViewById(R.id.tvDate);
         pickDate = (Button) findViewById(R.id.pick_date);

         // add a click listener to the button
         pickDate.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 // if on edit view and date button is clicked changed boolean in touch listener
                 if (mCurrentItemUri != null) {
                     mItemChanged = true;
                 }
                 showDialog(DATE_DIALOG_ID);
             }
         });

         //get the current date
         final Calendar c = Calendar.getInstance();
         mYear = c.get(Calendar.YEAR);
         mMonth = c.get(Calendar.MONTH);
         mDay = c.get(Calendar.DAY_OF_MONTH);
     }


     //updates the date displayed in TextView
     private void updateDate() {
         tvDate.setText(
                 new StringBuilder()
                         .append((mMonth + 1)).append("/")
                         .append(mDay).append("/")
                         .append(mYear).append(" "));
     }

     // the callback received when the user sets the date in the dialog
     private DatePickerDialog.OnDateSetListener DateSetListener =
             new DatePickerDialog.OnDateSetListener() {
                 @Override
                 public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                     mYear = year;
                     mMonth = month;
                     mDay = dayOfMonth;
                     updateDate();
                 }
             };

     // Dialog needed to launch date picker called by showDialog()
     @Override
     protected Dialog onCreateDialog(int id){
         if (id == DATE_DIALOG_ID) {
             return new DatePickerDialog(this, DateSetListener, mYear, mMonth, mDay);
         }
         return null;
     }

     //TODO change save checkmart to white color
     @Override   // show the options menu
     public boolean onCreateOptionsMenu(Menu m) {
         getMenuInflater().inflate(R.menu.menu_edit, m);
         return true;
     }

     @Override   // hide delete menu item when adding a new exercise
     public boolean onPrepareOptionsMenu(Menu m) {
         super.onPrepareOptionsMenu(m);
         // if this is add an exercise, hide "delete" menu item
         if (mCurrentItemUri == null) {
             MenuItem menuItem = m.findItem(R.id.action_delete);
             menuItem.setVisible(false);
         }
         return true;
     }



     @Override        // Select from the options menu
     public boolean onOptionsItemSelected(MenuItem item) {
         switch (item.getItemId()) {
             case R.id.action_save:
                 saveExercise();
                 finish();       // exit activity
                 return true;
             case R.id.action_delete:
                 // Alert Dialog for deleting one book
                 showDeleteConfirmationDialog();
                 return true;
             // this is the <- button on the header
             case android.R.id.home:
                 // book has not changed
                 if (!mItemChanged) {
                     NavUtils.navigateUpFromSameTask(StrengthExActivity.this);
                     return true;
                 }
                 // set up dialog to warn user about unsaved changes
                 DialogInterface.OnClickListener discardButtonClickListener =
                         new DialogInterface.OnClickListener() {
                             @Override
                             public void onClick(DialogInterface dialog, int i) {
                                 //user click discard. Navigate up to parent activity
                                 NavUtils.navigateUpFromSameTask(StrengthExActivity.this);
                             }
                         };
                 // show user they have unsaved changes
                 showUnsavedChangesDialog(discardButtonClickListener);
                 return true;
         }
         return super.onOptionsItemSelected(item);
     }


     private void saveExercise() {

         // read from EditText input fields
         String exNameString = mExNameEditText.getText().toString().trim();
         String weightString = mWeightEditText.getText().toString().trim();
         String repsString = mRepsEditText.getText().toString().trim();
         String setsString = mSetsEditText.getText().toString().trim();
         String notesString = mNotesEditText.getText().toString().trim();
         String dateString = tvDate.getText().toString().trim();


         // if adding exercise and the name field is left blank do nothing
         if (mCurrentItemUri == null && TextUtils.isEmpty(exNameString)) {
             Toast.makeText(this, getString(R.string.missing_exercise_name),
                     Toast.LENGTH_SHORT).show();
             return;
         }

         ContentValues values = new ContentValues();
         values.put(ExerciseContract.ExerciseEntry.C_EX_NAME, exNameString);
         values.put(ExerciseContract.ExerciseEntry.C_WEIGHT, weightString);
         values.put(ExerciseContract.ExerciseEntry.C_REPS, repsString);
         values.put(ExerciseContract.ExerciseEntry.C_SETS, setsString);
         values.put(ExerciseContract.ExerciseEntry.C_NOTES, notesString);
         values.put(ExerciseContract.ExerciseEntry.C_DATE, dateString);

         if (mCurrentItemUri == null) {
             // a new exercise
             // ***********
             Log.v(LOG_TAG, "in saveRecord " + ExerciseContract.ExerciseEntry.STRENGTH_URI.toString() + "\n" + values);

             Uri newUri = getContentResolver().insert(ExerciseContract.ExerciseEntry.STRENGTH_URI, values);
             // ************

             if (newUri == null) {
                 Toast.makeText(this, getString(R.string.strengthex_insert_exercise_failed),
                         Toast.LENGTH_SHORT).show();
             } else {
                 Toast.makeText(this, getString(R.string.strengthex_insert_exercise_good),
                         Toast.LENGTH_SHORT).show();
             }
         } else {
             // existing book so update with content URI and pass in ContentValues
             int rowsAffected = getContentResolver().update(mCurrentItemUri, values, null, null);
             if (rowsAffected == 0) {
                 // TODO Check db- Text Not Null does not seem to be working or entering
                 // "" does not mean NOT Null- there must be an error message closer to the db!!!
                 Toast.makeText(this, getString(R.string.edit_update_strengthex_failed),
                         Toast.LENGTH_SHORT).show();
             } else {
                 Toast.makeText(this, getString(R.string.edit_update_strengthex_success),
                         Toast.LENGTH_SHORT).show();
             }

         }
     }


     // delete exercise from DB
     private void deleteExercise(){
         if (mCurrentItemUri != null) {
             int rowsDeleted = getContentResolver().delete(mCurrentItemUri, null, null);
             if (rowsDeleted == 0) {
                 Toast.makeText(this, getString(R.string.delete_exercise_failure),
                         Toast.LENGTH_SHORT).show();
             } else {
                 Toast.makeText(this, getString(R.string.delete_exercise_successful),
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
                 // User clicked delet so delete
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
                 // user clicked the "keep eiditing" button. Dismiss dialog and keep editing
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
         if (!mItemChanged) {
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

 }
