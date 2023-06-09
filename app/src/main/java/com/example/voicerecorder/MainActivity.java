
package com.example.voicerecorder; // Replace with your actual package name

// MainActivity.java
import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;


import android.widget.TextView;
import android.widget.Toast;
import java.io.File;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.util.Log;


import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 200;
    private boolean isRecording = false;
    private MediaRecorder mediaRecorder;
    private String fileName;

    private Button recordButton;
    private TextView statusText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Verify storage path
        String storagePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        Log.d("MainActivity", "Storage Path: " + storagePath);


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recordButton = findViewById(R.id.record_button);
        statusText = findViewById(R.id.status_text);

        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRecording) {
                    stopRecording();
                } else {
                    startRecording();
                }
            }
        });

        // Request audio recording permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    PERMISSION_REQUEST_CODE);
        }
    }

    private void startRecording() {
        isRecording = true;
        recordButton.setText("Stop");

        // Set the output file path
        String directory = getExternalFilesDir(null).getAbsolutePath() + "/VoiceRecorder";

        File directoryFile = new File(directory);
        if (!directoryFile.exists()) {
            boolean isDirectoryCreated = directoryFile.mkdirs();
            if (!isDirectoryCreated) {
                // Failed to create the directory
                Toast.makeText(this, "Failed to create directory", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        fileName = directory + "/recording.wav";
        String filePath = directoryFile.getAbsolutePath();
        //this will log the file path
        Log.d("File Path", filePath);


        // Create and configure the MediaRecorder
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setOutputFile(fileName);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
            statusText.setText("Recording...");
            Toast.makeText(this, "Recording started", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void stopRecording() {
        isRecording = false;
        recordButton.setText("Record");

        if (mediaRecorder != null) {
            try {
                mediaRecorder.stop();
                mediaRecorder.reset(); // Reset the MediaRecorder to the initial state
                mediaRecorder.release();
                mediaRecorder = null;
                statusText.setText("Recording stopped");
                Toast.makeText(this, "Recording stopped", Toast.LENGTH_SHORT).show();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                recordButton.setEnabled(true);
            } else {
                // Permission denied
                Toast.makeText(this, "Permission denied to record audio", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
