package com.example.voicerecorder;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.File;
import java.io.IOException;

public class ApiManager {
    private OkHttpClient client;

    public ApiManager() {
        client = new OkHttpClient();
    }

    public void sendAudio(String apiUrl, String audioFilePath) {
        File audioFile = new File(audioFilePath);

        RequestBody requestBody = RequestBody.create(
                MediaType.parse("audio/wav"), // Replace "audio/wav" with the appropriate MIME type for WAV files
                audioFile
        );

        Request request = new Request.Builder()
                .url(apiUrl)
                .post(requestBody)
                .build();

        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                // Handle the API response here
                String responseBody = response.body().string();
                // Process the response as needed
            } else {
                // Handle the case when the API request is not successful
            }
        } catch (IOException e) {
            e.printStackTrace();
            // Handle any exception that occurs during the API request
        }


    }
}

