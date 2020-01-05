package com.example.powerballresults;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.powerballresults.model.Result;

import java.util.ArrayList;
import java.util.List;


public class ResultsAdapter extends RecyclerView.Adapter<ResultsAdapter.ViewHolder> {

    private Context context;
    private List<Result> resultsList;

    public ResultsAdapter(Context context) {
        this.context = context;
        resultsList = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View cell = LayoutInflater.from(context).
                inflate(R.layout.row_result, parent,false);
        return new ViewHolder(cell);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView date, firstNumbers, redNumber;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            firstNumbers = itemView.findViewById(R.id.firstNumbersId);
            redNumber = itemView.findViewById(R.id.redNumberId);
            date = itemView.findViewById(R.id.rowResultDateId);
        }
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.firstNumbers.setText(resultsList.get(position).getFirstNumbers());
        holder.redNumber.setText(resultsList.get(position).getRedNumber());
        holder.date.setText(resultsList.get(position).getDate());
    }

    @Override
    public int getItemCount() {
        return resultsList.size();
    }

    public void attachResultsList(ArrayList<Result> resultsArray) {
        this.resultsList = resultsArray;
    }
}