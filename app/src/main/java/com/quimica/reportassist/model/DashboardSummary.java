package com.quimica.reportassist.model;

import com.google.gson.annotations.SerializedName;

public class DashboardSummary {
    @SerializedName("totalEmployees")
    private int totalEmployees;

    @SerializedName("presentToday")
    private int presentToday;

    @SerializedName("lateToday")
    private int lateToday;

    @SerializedName("absentToday")
    private int absentToday;

    @SerializedName("date")
    private String date;

    public int getTotalEmployees() { return totalEmployees; }
    public int getPresentToday() { return presentToday; }
    public int getLateToday() { return lateToday; }
    public int getAbsentToday() { return absentToday; }
    public String getDate() { return date; }
}
