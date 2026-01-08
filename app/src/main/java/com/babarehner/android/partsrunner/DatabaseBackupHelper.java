package com.babarehner.android.partsrunner;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;

import com.babarehner.android.partsrunner.data.PartsRunnerDBHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Handles the logic for backing up the application's SQLite database.
 */
public class DatabaseBackupHelper {

    private Context mContext;

    public DatabaseBackupHelper(Context context) {
        mContext = context;
    }

    /**
     * Triggers the database backup process.
     */
    public void executeBackup() {
        File dbFile = mContext.getDatabasePath(PartsRunnerDBHelper.DB_NAME);

        // Create a timestamp for a unique file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String fileName = "parts_runner_backup_" + timeStamp + ".db";

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Modern method for Android 10 (API 29) and above
                backupWithMediaStore(dbFile, fileName);
            } else {
                // Legacy method for Android 9 (API 28) and below
                backupWithFileChannel(dbFile, fileName);
            }
            // Use mContext to show the Toast
            Toast.makeText(mContext, "File 'parts_runner_backup_' saved to Download folder.", Toast.LENGTH_LONG).show();

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(mContext, "Backup failed!", Toast.LENGTH_LONG).show();
        }
    }

    private void backupWithMediaStore(File sourceFile, String fileName) throws IOException {
        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
        values.put(MediaStore.MediaColumns.MIME_TYPE, "application/x-sqlite3");
        values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

        Uri uri = mContext.getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);
        if (uri != null) {
            try (OutputStream outputStream = mContext.getContentResolver().openOutputStream(uri);
                 FileInputStream inputStream = new FileInputStream(sourceFile)) {

                if (outputStream == null) {
                    throw new IOException("Failed to open output stream for " + uri);
                }

                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, length);
                }
            }
        } else {
            throw new IOException("Failed to create new MediaStore entry.");
        }
    }

    private void backupWithFileChannel(File sourceFile, String fileName) throws IOException {
        File backupDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        if (!backupDir.exists()) {
            backupDir.mkdirs();
        }
        File backupFile = new File(backupDir, fileName);

        try (FileChannel source = new FileInputStream(sourceFile).getChannel();
             FileChannel destination = new FileOutputStream(backupFile).getChannel()) {
            destination.transferFrom(source, 0, source.size());
        }
    }
}

