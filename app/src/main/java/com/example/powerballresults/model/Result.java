package com.example.powerballresults.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Result implements Parcelable {

    public static final String RESULT = "firstNumbers";
    public static final String DATE = "date";

    private String firstNumbers;
    private String redNumber;
    private String date;

    public Result() {}

    private Result(Parcel in) {
        firstNumbers = in.readString();
        redNumber = in.readString();
        date = in.readString();
    }

    public static final Creator<Result> CREATOR = new Creator<Result>() {
        @Override
        public Result createFromParcel(Parcel in) {
            return new Result(in);
        }

        @Override
        public Result[] newArray(int size) {
            return new Result[size];
        }
    };

    public String getRedNumber() {
        return redNumber;
    }

    public void setRedNumber(String redNumber) {
        this.redNumber = redNumber;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getFirstNumbers() {
        return firstNumbers;
    }

    public void setFirstNumbers(String firstNumbers) {
        this.firstNumbers = firstNumbers;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(firstNumbers);
        dest.writeString(redNumber);
        dest.writeString(date);
    }

    @Override
    public String toString() {
        return "Result{" +
                "firstNumbers='" + firstNumbers + '\'' +
                ", redNumber='" + redNumber + '\'' +
                ", date='" + date + '\'' +
                '}';
    }
}