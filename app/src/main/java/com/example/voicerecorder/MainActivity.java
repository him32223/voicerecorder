package com.example.voicerecorder;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private AudioRecorder audioRecorder;
    private MediaPlayer mediaPlayer;
    private TextView timerTextView;
    private Handler handler;
    private Runnable timerRunnable;
    private long startTime;
    private boolean isPlaying;
    private String outputFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        outputFile = getCacheDir().getAbsolutePath() + "/recording.3gp";

        audioRecorder = new AudioRecorder(outputFile);
        mediaPlayer = new MediaPlayer();
        timerTextView = findViewById(R.id.timerTextView);
        handler = new Handler();
        isPlaying = false;

        File file = new File(audioRecorder.getOutputFile());
        if (file.exists()) {
            try {
                mediaPlayer.setDataSource(file.getPath());
                mediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Button startButton = findViewById(R.id.startButton);
        Button stopButton = findViewById(R.id.stopButton);
        Button replayButton = findViewById(R.id.Replay);
        Button stopPlaybackButton = findViewById(R.id.StopPlaying);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRecording();
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopRecording();
            }
        });

        replayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replayRecording();
            }
        });

        stopPlaybackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopPlayback();
            }
        });
    }

    private void startRecording() {
        if (audioRecorder != null) {
            audioRecorder.startRecording();
            startTime = System.currentTimeMillis();
            startTimer();
        }
    }

    private void updateTimerTextView(long timeInMillis) {
        int seconds = (int) (timeInMillis / 1000);
        int minutes = seconds / 60;
        seconds %= 60;
        String time = String.format("%02d:%02d", minutes, seconds);
        timerTextView.setText(time);
    }

    private void stopRecording() {
        if (audioRecorder != null) {
            audioRecorder.stopRecording();
            stopTimer();
        }
    }

    private void replayRecording() {
        if (!isPlaying) {
            try {
                mediaPlayer.reset();
                mediaPlayer.setDataSource(outputFile);
                if (mediaPlayer.getDuration() > 0) {
                    // MediaPlayer is already prepared, no need to call prepare again
                    startPlayback();
                } else {
                    mediaPlayer.prepareAsync();
                    mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            startPlayback();
                        }
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void startPlayback() {
        mediaPlayer.start();
        isPlaying = true;

        // Calculate the remaining time and update the timerTextView
        long totalTime = mediaPlayer.getDuration();
        long elapsedTime = System.currentTimeMillis() - startTime;
        long remainingTime = totalTime - elapsedTime;
        updateTimerTextView(remainingTime);
    }


    private void stopPlayback() {
        if (isPlaying) {
            mediaPlayer.stop();
            mediaPlayer.reset();
            isPlaying = false;
        }
    }

    private void startTimer() {
        timerRunnable = new Runnable() {
            @Override
            public void run() {
                long milliseconds = System.currentTimeMillis() - startTime;
                int seconds = (int) (milliseconds / 1000);
                int minutes = seconds / 60;
                seconds %= 60;
                String time = String.format("%02d:%02d", minutes, seconds);
                timerTextView.setText(time);
                handler.postDelayed(this, 1000);
            }
        };
        handler.postDelayed(timerRunnable, 0);
    }

    private void stopTimer() {
        handler.removeCallbacks(timerRunnable);
        timerTextView.setText("00:00");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }
}
