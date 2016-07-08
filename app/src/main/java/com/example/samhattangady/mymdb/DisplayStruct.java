package com.example.samhattangady.mymdb;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by samhattangady on 1/3/16.
 */
public class DisplayStruct implements Parcelable{

    String name;
    String overview;
    String poster;
    String backdrop;
    String ratings;
    String popularity;

    public DisplayStruct(){
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPoster() {
        return this.poster;
    }

    public String getBackdrop() {
        return this.backdrop;
    }

    // Parcelling part
    public DisplayStruct(Parcel in) {
        String[] data = new String[6];

        in.readStringArray(data);
        this.name = data[0];
        this.overview = data[1];
        this.poster = data[2];
        this.backdrop = data[3];
        this.ratings = data[4];
        this.popularity = data[5];
    }

    public int describeContents(){
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[] {
                this.name,
                this.overview,
                this.poster,
                this.backdrop,
                this.ratings,
                this.popularity });
    }
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public DisplayStruct createFromParcel(Parcel in) {
            return new DisplayStruct(in);
        }

        public DisplayStruct[] newArray(int size) {
            return new DisplayStruct[size];
        }
    };

}
