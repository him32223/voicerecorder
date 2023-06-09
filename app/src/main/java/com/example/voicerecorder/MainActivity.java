package com.example.voicerecorder;

import android.Manifest;
import android.content.Intent;

import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements RecognitionListener {
    private static final int PERMISSION_REQUEST_CODE = 200;
    private boolean isRecording = false;
    private boolean isPlaying = false;
    private MediaRecorder mediaRecorder;
    private MediaPlayer mediaPlayer;
    private String fileName;
    private SpeechRecognizer speechRecognizer;
    private Intent speechRecognizerIntent;
    private Button recordButton;
    private Button playButton;
    private TextView statusText;
    private Chronometer timer;
    private TextView recognizedText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeSpeechRecognizer();

        recordButton = findViewById(R.id.record_button);
        playButton = findViewById(R.id.play_button);
        statusText = findViewById(R.id.status_text);
        timer = findViewById(R.id.timer);
        recognizedText = findViewById(R.id.recognized_text);

        recordButton.setOnClickListener(v -> {
            if (isRecording) {
                stopRecording();
            } else {
                startRecording();
            }
        });

        playButton.setOnClickListener(v -> {
            if (isPlaying) {
                stopPlaying();
            } else {
                playRecording();
            }
        });

        requestAudioRecordingPermission();
    }

    private void initializeSpeechRecognizer() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizer.setRecognitionListener(this);

        speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        if (speechRecognizerIntent.resolveActivity(getPackageManager()) == null) {
            Toast.makeText(this, "Speech recognition is not supported on this device", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void requestAudioRecordingPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    PERMISSION_REQUEST_CODE);
        } else {
            recordButton.setEnabled(true);
        }
    }

    private void startRecording() {
        isRecording = true;
        recordButton.setText("Stop");
        playButton.setEnabled(false);

        String directory = getExternalFilesDir(null).getAbsolutePath() + "/VoiceRecorder";

        timer.setBase(SystemClock.elapsedRealtime());
        timer.setVisibility(View.VISIBLE);
        timer.start();

        File directoryFile = new File(directory);
        if (!directoryFile.exists()) {
            boolean isDirectoryCreated = directoryFile.mkdirs();
            if (!isDirectoryCreated) {
                Toast.makeText(this, "Failed to create directory", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        fileName = directory + "/recording.wav";

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
        playButton.setEnabled(true);
        timer.stop();
        timer.setVisibility(View.GONE);

        if (mediaRecorder != null) {
            try {
                mediaRecorder.stop();
                mediaRecorder.reset();
                mediaRecorder.release();
                mediaRecorder = null;
                statusText.setText("Recording stopped");
                Toast.makeText(this, "Recording stopped", Toast.LENGTH_SHORT).show();

                convertSpeechToText(fileName);
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }
    }

    private void convertSpeechToText(String filePath) {
        speechRecognizer.startListening(speechRecognizerIntent);
    }

    private void playRecording() {
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.setDataSource(fileName);
                mediaPlayer.prepare();
                mediaPlayer.start();
                Toast.makeText(this, "Playing recording", Toast.LENGTH_SHORT).show();
                playButton.setText("Stop");

                timer.setBase(SystemClock.elapsedRealtime() - mediaPlayer.getCurrentPosition());
                timer.setVisibility(View.VISIBLE);
                timer.start();

                mediaPlayer.setOnCompletionListener(mp -> stopPlaying());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            stopPlaying();
        }
    }

    private void stopPlaying() {
        mediaPlayer.release();
        mediaPlayer = null;
        Toast.makeText(this, "Stopped playing", Toast.LENGTH_SHORT).show();
        playButton.setText("Play");
        timer.stop();
        timer.setVisibility(View.GONE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                recordButton.setEnabled(true);
            } else {
                Toast.makeText(this, "Permission denied to record audio", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // RecognitionListener methods

    @Override
    public void onReadyForSpeech(Bundle params) {
        // Called when the speech recognition service is ready to receive speech input.
    }

    @Override
    public void onBeginningOfSpeech() {
        // Called when the user starts speaking.
    }

    @Override
    public void onRmsChanged(float rmsdB) {
        // Called when the RMS (Root Mean Square) value of the recorded audio changes.
    }

    @Override
    public void onBufferReceived(byte[] buffer) {
        // Called when the audio recording buffer is received.
    }

    @Override
    public void onEndOfSpeech() {
        // Called when the user stops speaking.
    }

    @Override
    public void onError(int error) {
        // Called if there is an error during speech recognition.
    }

    @Override
    public void onResults(Bundle results) {
        // Called when speech recognition results are available.
    }

    @Override
    public void onPartialResults(Bundle partialResults) {
        // Called when partial speech recognition results are available.
    }

    @Override
    public void onEvent(int eventType, Bundle params) {
        // Called when an event related to speech recognition occurs.
    }
}
