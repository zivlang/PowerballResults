package com.example.powerballresults.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class SearchResult implements Parcelable {

//    private ArrayList<String> searchWins;
//    private ArrayList<String> searchDates;
    private boolean redOrNot;
    private int numberCount;
    private String dateString;

    public SearchResult(Parcel in) {
//        searchWins = in.createStringArrayList();
//        searchDates = in.createStringArrayList();
        redOrNot = in.readByte() != 0;
        numberCount = in.readInt();
        dateString = in.readString();
    }

    public SearchResult() {}

    @Override
    public String toString() {
        return "SearchResult{" +
//                "searchWins=" + searchWins +
//                ", searchDates=" + searchDates +
                ", redOrNot=" + redOrNot +
                ", numberCount=" + numberCount +
                ", dateString='" + dateString + '\'' +
                '}';
    }

    public boolean isRedOrNot() {
        return redOrNot;
    }

    public void setRedOrNot(boolean redOrNot) {
        this.redOrNot = redOrNot;
    }

    public int getNumberCount() {
        return numberCount;
    }

    public void setNumberCount(int numberCount) {
        this.numberCount = numberCount;
    }

    public String getDateString() {
        return dateString;
    }

    public void setDateString(String dateString) {
        this.dateString = dateString;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
//        dest.writeStringList(searchWins);
//        dest.writeStringList(searchDates);
        dest.writeByte((byte) (redOrNot ? 1 : 0));
        dest.writeInt(numberCount);
        dest.writeString(dateString);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<SearchResult> CREATOR = new Creator<SearchResult>() {
        @Override
        public SearchResult createFromParcel(Parcel in) {
            return new SearchResult(in);
        }

        @Override
        public SearchResult[] newArray(int size) {
            return new SearchResult[size];
        }
    };
}