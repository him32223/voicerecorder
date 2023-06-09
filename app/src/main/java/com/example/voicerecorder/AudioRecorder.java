package com.example.voicerecorder;
import android.media.MediaRecorder;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.IOException;

public class AudioRecorder {
    private MediaRecorder mediaRecorder;
    private String outputFile;


        private FileOutputStream fileOutputStream;


        public AudioRecorder(String outputFile) {
            this.outputFile = outputFile;
        }
    public String getOutputFile() {
        return outputFile;
    }
        public void startRecording() {
            if (fileOutputStream == null) {
                try {
                    fileOutputStream = new FileOutputStream(new File(outputFile));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public void stopRecording() {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                fileOutputStream = null;
            }
        }
    }

