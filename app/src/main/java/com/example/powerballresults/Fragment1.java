package com.example.powerballresults;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.powerballresults.model.Result;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

public class Fragment1 extends Fragment {

    private Context context;

    private GetJson getJson;

    private AlertDialog filterDialog;
    private EditText resultsNumberView;
    private ArrayList<Result> resultsList;
    private ResultsAdapter resultsAdapter;

    private int listCount = 1000;

    private boolean dateSelected;
    private String modifiedUrl;
    private String jsonString;
    private String originalUrl = "https://data.ny.gov/resource/d6yy-54nr.json";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        context = getActivity();

        try {
            getJson = new GetJson(originalUrl);
            jsonString = getJson.jsonString;
            resultsList = getResultsArray();
        } catch (ExecutionException | JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        View view = inflater.inflate(R.layout.layout_fragment1, container, false);

        Button filterLimitBtn = view.findViewById(R.id.filterLimitBtnId);
        Button resetBtn = view.findViewById(R.id.resetBtnId);

        resultsAdapter = new ResultsAdapter(context);
        RecyclerView rv = view.findViewById(R.id.rvId);

        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        rv.setLayoutManager(layoutManager);
        rv.setHasFixedSize(true);

        DividerItemDecoration dividerItemDecoration =
                new DividerItemDecoration(context, layoutManager.getOrientation());

        rv.addItemDecoration(dividerItemDecoration);
        rv.setAdapter(resultsAdapter);

        resultsAdapter.attachResultsList(resultsList);

        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    getJson = new GetJson(originalUrl);
                    jsonString = getJson.jsonString;
                    resultsList = getResultsArray();
                    resultsAdapter.attachResultsList(resultsList);
                    resultsAdapter.notifyDataSetChanged();
                } catch (ExecutionException | JSONException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        filterLimitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(resultsList.size() < listCount) {
                    try {
                        new GetJson(originalUrl);
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                View inflatedView = LayoutInflater.from(context)
                        .inflate(R.layout.dialog_filter,null,false);

                final Spinner spinner = inflatedView.findViewById(R.id.spinnerId);
                resultsNumberView = inflatedView.findViewById(R.id.resultsNumberId);
                final Button alertBtn = inflatedView.findViewById(R.id.alertBtnId);

                final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
                dialogBuilder.setView(inflatedView);

                filterDialog = dialogBuilder.show();

                try {
                    getSpinner(spinner);
                } catch (JSONException | ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }

                resultsNumberView.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if(!resultsNumberView.getText().toString().isEmpty()) {
                            spinner.setSelection(0);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                });
                alertBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(dateSelected) {
                            processDate(spinner);
                        }
                        else {
                            processNumber(resultsNumberView);
                        }
                    }
                });
            }
        });
        return view;
    }

    private void processDate(Spinner spinner) {

        String selectedDate = spinner.getSelectedItem().toString();
        try {
            modifiedUrl = originalUrl + "?draw_date=" + selectedDate;
            getJson = new GetJson(modifiedUrl);
            jsonString = getJson.jsonString;
            resultsList = getResultsArray();
            resultsAdapter.attachResultsList(resultsList);
            resultsAdapter.notifyDataSetChanged();
            filterDialog.dismiss();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void processNumber(EditText resultsNumberView){

        String typedNumber = resultsNumberView.getText().toString();

        if (typedNumber.isEmpty()) {
            try {
                getJson = new GetJson(originalUrl);
                jsonString = getJson.jsonString;
                resultsList = getResultsArray();
                resultsAdapter.attachResultsList(resultsList);
            } catch (ExecutionException | JSONException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            filterDialog.dismiss();
        } else {
            try {
                int toInt = Integer.parseInt(typedNumber);
                if (toInt <= listCount && toInt >= 0) {
                    modifiedUrl = originalUrl + "?&$limit=" + typedNumber;
                    getJson = new GetJson(modifiedUrl);
                    jsonString = getJson.jsonString;
                    resultsList = getResultsArray();
                    resultsAdapter.attachResultsList(resultsList);
                    filterDialog.dismiss();
                } else {
                    throw new NumberFormatException(null);
                }
            } catch (NumberFormatException | JSONException e) {
                Toast.makeText(context, "Type an integer between 0 and " + listCount, Toast.LENGTH_SHORT).show();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    private ArrayList<Result> getResultsArray() throws JSONException {
        ArrayList<Result> resultsArrayList = new ArrayList<>();
        JSONArray resultsArray = new JSONArray(jsonString);

        for (int i = 0; i < resultsArray.length(); i++) {

            Result result = new Result();

            JSONObject resultObject = resultsArray.getJSONObject(i);
            result.setDate(resultObject.getString("draw_date").substring(0,10));
            result.setFirstNumbers(resultObject.getString("winning_numbers").substring(0,15));
            result.setRedNumber(resultObject.getString("winning_numbers").substring(15,17));

            resultsArrayList.add(result);
        }
        return resultsArrayList;
    }

    private void getSpinner(Spinner spinner) throws JSONException, ExecutionException, InterruptedException {

        ArrayAdapter<String> adapter;
        ArrayList<String> datesArrayList = new ArrayList<>();
        getJson = new GetJson(originalUrl);
        JSONArray jsonArray = new JSONArray(getJson.jsonString);

        String dateString;

        for (int i = 0; i < jsonArray.length(); i++) {

            JSONObject resultObject = jsonArray.getJSONObject(i);
            dateString = resultObject.getString("draw_date").substring(0, 10);
            datesArrayList.add(dateString);
        }

        String[] listFirstItem = new String[1];
        listFirstItem[0] = "";

        String[] entireListArray;

        datesArrayList.add(0, Arrays.toString(listFirstItem)
                .replace("[", "").replace("]", ""));
        entireListArray = datesArrayList.toArray(new String[0]);

        adapter = new ArrayAdapter<>(context,
                R.layout.spinner_style, entireListArray);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position != 0) {
                    dateSelected = true;
                    if(resultsNumberView == null) {
                        return;
                    }
                    resultsNumberView.getText().clear();
                    resultsAdapter.attachResultsList(resultsList);
                }
                else {
                    dateSelected = false;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
}