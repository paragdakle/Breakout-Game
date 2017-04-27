package com.hci.project.breakout.activity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Comparator;

/**
 * Created by rauna on 4/25/2017.
 */

public class Scorer implements Parcelable, Comparable<Scorer> {
    private static final String separator = "\t";
    private final String terminator = "\n";

    private String name;
    private int score;

    public Scorer(String name, int score)
    {
        this.name = name;
        this.score = score;
    }

    public Scorer(Parcel in)
    {
        this.score = in.readInt();
        this.name = in.readString();
    }

    public String getName()
    {
        return this.name;
    }

    public int getScore()
    {
        return this.score;
    }

    @Override
    public String toString()
    {
        return this.name + separator + this.score + this.terminator;
    }

    public String toSharedPreferenceString()
    {
        return this.name + separator + this.score;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeInt(this.score);
    }

    public static final Creator<Scorer> CREATOR = new Creator<Scorer>() {
        @Override
        public Scorer createFromParcel(Parcel in) {
            return new Scorer(in);
        }

        @Override
        public Scorer[] newArray(int size) {
            return new Scorer[size];
        }
    };

    @Override
    public int compareTo(Scorer s1)
    {
        if (score < s1.score)
        {
            return 1;
        } else if (score > s1.score)
        {
            return -1;
        }
        return 0;
    }

    public static Scorer stringToScorer(String strScorer) {
        String[] attrs = strScorer.split(separator);
        if(attrs.length == 2) {
            return new Scorer(attrs[0], Integer.parseInt(attrs[1]));
        }
        return null;
    }
}
