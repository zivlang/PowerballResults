package com.example.powerballresults;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.powerballresults.model.SearchResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class Fragment2 extends Fragment {

    private String text;
    private ArrayList<SearchResult> searchResultsList;

    private int[][] winsArraysArray;
    private int[] typedNumbersArray;
    private String[] datesArray;

    private int length;
    private boolean red;

    private EditText et;

    private Context context;
    private RecyclerView rv;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.layout_fragment2, container, false);

        et = view.findViewById(R.id.etId);
        rv = view.findViewById(R.id.searchRvId);
        Button searchBtn = view.findViewById(R.id.searchBtnId);

        context = getActivity();

        length = et.getText().length();

        text = et.getText().toString();

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = et.getText().toString();
                if(length != 0){
                    if(text.substring(length - 1).matches("[ ]")){
                        System.out.println("matches:  ");
                        erase();
                    }
                    typedNumbersArray = stringToIntArray(et.getText().toString());
                    length = typedNumbersArray.length;
                    if(length < 6){
                        if(length == 0){
                            Toast.makeText(getActivity(), "No numbers were typed", Toast.LENGTH_SHORT).show();
                        }
                        else if(length != 5) {
                            Toast.makeText(getActivity(), "Type " + (6 - length) + " more numbers", Toast.LENGTH_SHORT).show();
                            et.append(" ");
                        }
                        else {
                            et.append(" ");
                            Toast.makeText(getActivity(), "Type 1 more number", Toast.LENGTH_SHORT).show();
                        }
                    }
                    if(typedNumbersArray.length > 6){
                        Toast.makeText(getActivity(), "The search includes up to 6 numbers", Toast.LENGTH_SHORT).show();
                    }
                    if(length == 6) {
                        try {
                            getWinsFromJson();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        alignArrays();
                        showResults(view);
                    }
                }
                else {
                    Toast.makeText(getActivity(), "No numbers were typed", Toast.LENGTH_SHORT).show();
                }
            }
        });

        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                length = et.getText().length();

                text = et.getText().toString();

                if(length > 0 && text.substring(0, 1).matches("[ ]")){
                    erase();
                }

                if(length > 2 && text.substring(length - 2, length - 1).matches("[ ]") && text.substring(length - 1, length).matches("[ ]")) {
                    erase();
                }
                if (!text.matches("[0-9 / ]+") && length > 0) {
                    Toast.makeText(getActivity(), "Use digits only", Toast.LENGTH_SHORT).show();
                    erase();
                }
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });
        return view;
    }

    private void erase() {
        StringBuilder sb = new StringBuilder(text);
        sb = sb.deleteCharAt(text.length() - 1);
        et.setText(sb.toString());
        et.setSelection(length);
    }
    private void showResults(View view) {

        SearchAdapter searchAdapter = new SearchAdapter(context);
        rv = view.findViewById(R.id.searchRvId);

        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        rv.setLayoutManager(layoutManager);
        rv.setHasFixedSize(true);

        DividerItemDecoration dividerItemDecoration =
                new DividerItemDecoration(context, layoutManager.getOrientation());

        rv.addItemDecoration(dividerItemDecoration);
        rv.setAdapter(searchAdapter);

        searchAdapter.attachList(searchResultsList);
    }

    private void alignArrays(){

        searchResultsList = new ArrayList<>();
        int count = 0;

        SearchResult searchResult;
        //iterating over each nested array
        for(int i = 1; i < winsArraysArray.length; i++){
            for (int j = 0; j < winsArraysArray[i].length - 1; j++) {
                //iterating over the array of typed numbers
                for(int k = 0; k < typedNumbersArray.length - 1; k++) {
                    // in case of matching regular numbers, the match is counted
                    if (winsArraysArray[i][j] == typedNumbersArray[k]) {
                        count++;
                    }
                }
            }
            //in case of matching red numbers
            if(typedNumbersArray.length == 6) {
                if (typedNumbersArray[5] == winsArraysArray[i][5]) {
                    red = true;
                }
            }
            // checking if the winning criteria are met
            if (red || count > 2) {
                // if so, the search results are saved
                searchResult = new SearchResult();
                searchResult.setNumberCount(count);
                searchResult.setDateString(datesArray[i]);
                searchResult.setRedOrNot(red);
                searchResultsList.add(searchResult);
            }
                // resetting
            red = false;
            count = 0;
        }
    }

    private void getWinsFromJson() throws JSONException {

        JSONArray jsonArray = null;
        try {
            String originalUrl = "https://data.ny.gov/resource/d6yy-54nr.json";
            GetJson getJson = new GetJson(originalUrl);
            jsonArray = new JSONArray(getJson.jsonString);
            winsArraysArray = new int[jsonArray.length()][6];
            datesArray = new String[jsonArray.length()];
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < winsArraysArray.length; i++) {

            String numberString;
            String dateString;

            JSONObject resultObject;
            if (jsonArray != null) {

                resultObject = jsonArray.getJSONObject(i);

                numberString = resultObject.getString("winning_numbers").substring(0,17);
                dateString = resultObject.getString("draw_date").substring(0,10);
                datesArray[i] = dateString;
                winsArraysArray[i] = stringToIntArray(numberString);
            }
        }
    }

    private int[] stringToIntArray(String numbersString){

        String[] stringNumbers = numbersString.split(" ");
        int[] intNumbers = new int[stringNumbers.length];
        for (int k = 0; k < intNumbers.length; k++) {
            intNumbers[k] = Integer.parseInt(stringNumbers[k]);
        }
        return intNumbers;
    }
}