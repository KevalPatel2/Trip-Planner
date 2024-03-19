package com.example.tripplanner;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.ComponentActivity;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class SuccessActivity extends ComponentActivity {

    private final ActivityResultLauncher<String[]> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), permissions -> {
                if (permissions.containsKey(Manifest.permission.WRITE_EXTERNAL_STORAGE) &&
                        Boolean.TRUE.equals(permissions.get(Manifest.permission.WRITE_EXTERNAL_STORAGE))) {
                    new DownloadFileTask().execute();
                } else {
                    Toast.makeText(this, "File Downloaded Successfully", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success);

        Button downloadReceiptButton = findViewById(R.id.downloadButton);
        downloadReceiptButton.setOnClickListener(v -> checkStoragePermission());

        // Add a new button to get Rickrolled
        Button rickrollButton = findViewById(R.id.rickrollButton);
        rickrollButton.setOnClickListener(v -> {
            String rickrollUrl = "https://www.lonelyplanet.com/"; // Rick Astley - Never Gonna Give You Up
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(rickrollUrl));
            startActivity(intent);
        });
    }

    private void checkStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            new DownloadFileTask().execute();
        } else {
            requestPermissionLauncher.launch(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE});
        }
    }

    private class DownloadFileTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            // Download file operation
            // Replace "fileUrl" and "fileName" with your actual file URL and name
            String fileUrl = "https://example.com/yourfile.pdf";
            String fileName = "yourfile.pdf";

            try {
                URL url = new URL(fileUrl);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    File directory = new File(Environment.getExternalStorageDirectory(), "/TripPlanner");
                    if (!directory.exists() && !directory.mkdirs()) {
                        return false;
                    }

                    File file = new File(directory, fileName);

                    InputStream inputStream = urlConnection.getInputStream();
                    FileOutputStream fos = new FileOutputStream(file);

                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = inputStream.read(buffer)) != -1) {
                        fos.write(buffer, 0, len);
                    }

                    fos.close();
                    inputStream.close();

                    return true;
                } else {
                    return false;
                }

            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                Toast.makeText(SuccessActivity.this, "File downloaded successfully", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(SuccessActivity.this, "Failed to download file", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
