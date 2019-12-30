package com.example.powerballresults;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.powerballresults.model.Result;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class Fragment1 extends Fragment {

    private AsyncTask<String, String, String> getJson;

    private Context context;

    private ArrayList<Result> resultsList;
    private ResultsAdapter resultsAdapter;

    private int listCount = 1000;
    private static final int PERMISSION_REQUEST_CODE = 200;

    private boolean loadOriginal = true;
    private String typedDate, modifiedUrl;
    private String originalUrl = "https://data.ny.gov/resource/d6yy-54nr.json";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        context = getActivity();

        try {
            new GetJson().execute().get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        View view = inflater.inflate(R.layout.layout_fragment1, container, false);

        final EditText dateView = view.findViewById(R.id.dateId);
        final EditText resultsNumberView = view.findViewById(R.id.resultsNumberId);
        Button dateBtn = view.findViewById(R.id.dateBtnId);
        Button resultsNumberBtn = view.findViewById(R.id.resultsNumberBtnId);

        resultsAdapter = new ResultsAdapter(context);
        RecyclerView rv = view.findViewById(R.id.rvId);

        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        rv.setLayoutManager(layoutManager);
        rv.setHasFixedSize(true);

        DividerItemDecoration dividerItemDecoration =
                new DividerItemDecoration(context, layoutManager.getOrientation());

        rv.addItemDecoration(dividerItemDecoration);
        rv.setAdapter(resultsAdapter);

//        new GetJson().execute();

        resultsAdapter.attachResultsList(resultsList);

        dateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                typedDate = dateView.getText().toString();

                if (typedDate.isEmpty()) {
                    loadOriginal = true;
                    try {
                        new GetJson().execute().get();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    resultsAdapter.attachResultsList(resultsList);
//                    resultsAdapter.notifyDataSetChanged();
                } else {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    try {
                        dateFormat.parse(typedDate);
                        modifiedUrl = originalUrl + "?draw_date=" + typedDate;
                        loadOriginal = false;
                        new GetJson().execute().get();

                        resultsAdapter.attachResultsList(resultsList);
                        if(resultsList.size() == 0){
                            Toast.makeText(context, "No record was found for this date", Toast.LENGTH_SHORT).show();
                        }
//                        resultsAdapter.notifyDataSetChanged();
                    } catch (ParseException e) {
                        Toast.makeText(context, "Type numbers in the following pattern: yyyy-MM-dd", Toast.LENGTH_LONG).show();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        resultsNumberBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String typedNumber = resultsNumberView.getText().toString();

                if (typedNumber.isEmpty()) {
                    loadOriginal = true;
                    try {
                        new GetJson().execute().get();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    resultsAdapter.attachResultsList(resultsList);
                } else {
                    try {
                        int toInt = Integer.parseInt(typedNumber);
                        if (toInt <= listCount && toInt >= 0) {
                            modifiedUrl = originalUrl + "?&$limit=" + typedNumber;
                            new GetJson().execute().get();
                            resultsAdapter.attachResultsList(resultsList);
                        } else {
                            throw new NumberFormatException(null);
                        }
                    } catch (NumberFormatException e) {
                        Toast.makeText(context, "Type an integer between 0 and 1000", Toast.LENGTH_SHORT).show();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    return view;
    }

    public class GetJson extends AsyncTask<String,String,String> {

        @Override
        protected String doInBackground(String... strings) {

            if(loadOriginal) {
                try {
                    resultsList = getResultsArray(originalUrl);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                loadOriginal = false;
            }

            else{
                try {
                    resultsList = getResultsArray(modifiedUrl);
                } catch (IOException e) {
                    Toast.makeText(context, "No such date is found in the data", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            resultsAdapter.notifyDataSetChanged();
        }

        private ArrayList<Result> getResultsArray(String url) throws IOException, JSONException {

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
            String jsonString = fullJSON.toString();

            ArrayList<Result> resultsArrayList = new ArrayList<>();

            JSONArray resultsArray = new JSONArray(jsonString);

            for (int i = 0; i < resultsArray.length(); i++) {

                Result result = new Result();

                JSONObject resultObject = resultsArray.getJSONObject(i);
                result.setDate(resultObject.getString("draw_date").substring(0, 10));
                result.setFirstNumbers(resultObject.getString("winning_numbers").substring(0,15));
                result.setRedNumber(resultObject.getString("winning_numbers").substring(15,17));

                resultsArrayList.add(result);
            }

            return resultsArrayList;
        }
    }
}