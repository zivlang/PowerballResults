package com.example.powerballresults;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

class GetJson {

    private String url;
    String jsonString;

    GetJson(String url) throws ExecutionException, InterruptedException {
        this.url = url;
        new StreamJson().execute().get();
    }

    public class StreamJson extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {

            try {
                jsonString = getJsonString(url);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }

        private String getJsonString(String url) throws IOException {

            HttpURLConnection urlConnection = (HttpURLConnection) new URL(url).openConnection();
            //an object that reads from the internet
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            StringBuilder fullJSON = new StringBuilder(); // a string that will hold the JSON
            String line; // will hold a certain line from the JSON
            while ((line = bufferedReader.readLine()) != null) { //unless the read line is null,
                // it's being saved in line
                fullJSON.append(line); // adding the read line to the already saved string
            }
            //Close our InputStream and Buffered reader
            bufferedReader.close();

            return fullJSON.toString();
        }
    }
}