package com.example.powerballresults;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.powerballresults.model.SearchResult;

import java.util.ArrayList;
import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder>{

    private Context context;
    private List<SearchResult> outputList;

    public SearchAdapter(Context context) {
        this.context = context;
        outputList = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View cell = LayoutInflater.from(context).
                inflate(R.layout.row_search, parent,false);
        return new ViewHolder(cell);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView date, numbersCount, red;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            numbersCount = itemView.findViewById(R.id.nonRedCountId);
            red = itemView.findViewById(R.id.redOrNotId);
            date = itemView.findViewById(R.id.rowSearchDateId);
        }
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.date.setText(outputList.get(position).getDateString()+":");
        int regularNumbers = outputList.get(position).getNumberCount();
        boolean red = outputList.get(position).isRedOrNot();
        if(regularNumbers > 1){
            holder.numbersCount.setText(" won "+ outputList.get(position).getNumberCount() +" regular numbers");
            if(red) {
                holder.red.setText(" + a red ball");
            }
            else{
                holder.red.setText("");
            }
        }
        else if(regularNumbers == 1 && red){
            holder.numbersCount.setText(" won "+ outputList.get(position).getNumberCount() +" regular number");
            holder.red.setText(" + a red ball");
        }
        else if(regularNumbers == 0 && red){
            holder.numbersCount.setText("");
            holder.red.setText(" a red ball");
        }
    }

    @Override
    public int getItemCount() {
        return outputList.size();
    }

    public void attachList(ArrayList<SearchResult> searchOutput) {
        this.outputList = searchOutput;
    }
}