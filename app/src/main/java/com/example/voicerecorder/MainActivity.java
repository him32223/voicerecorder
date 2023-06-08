package com.example.voicerecorder;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import java.io.File;
import android.os.Environment;
// Import the AudioRecorder class
import com.example.voicerecorder.AudioRecorder;
public class MainActivity extends AppCompatActivity {
    private AudioRecorder audioRecorder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create the directory if it doesn't exist
        File directory = new File(getFilesDir(), "my_directory");
        if (!directory.exists()) {
            if (!directory.mkdirs()) {
                // Failed to create the directory
                return;
            }
        }





        audioRecorder = new AudioRecorder("/path/to/output/file.3gp");

        Button startButton = findViewById(R.id.startButton);
        Button stopButton = findViewById(R.id.stopButton);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                audioRecorder.startRecording();
                // Update UI or perform any other necessary actions
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                audioRecorder.stopRecording();
                // Update UI or perform any other necessary actions
            }
        });
    }

    // Other methods and lifecycle callbacks
}




