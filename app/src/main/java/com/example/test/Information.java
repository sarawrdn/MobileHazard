package com.example.test;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Information {

    @SerializedName("id")
    @Expose
    public String id;

    @SerializedName("hazard")
    @Expose
    public String hazard;

    @SerializedName("time")
    @Expose
    public String time;

    @SerializedName("date")
    @Expose
    public String date;

    @SerializedName("people")
    @Expose
    public String people;

    @SerializedName("lat")
    @Expose
    public String lat;

    @SerializedName("lng")
    @Expose
    public String lng;
}
