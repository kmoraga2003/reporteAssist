package com.quimica.reportassist.model;

import com.google.gson.annotations.SerializedName;

public class ReportItem {
    @SerializedName("employeeId")
    private Long employeeId;

    @SerializedName("employeeCode")
    private String employeeCode;

    @SerializedName("employeeName")
    private String employeeName;

    @SerializedName("department")
    private String department;

    @SerializedName("date")
    private String date;

    @SerializedName("eventTime")
    private String eventTime;

    @SerializedName("detail")
    private String detail;

    public Long getEmployeeId() { return employeeId; }
    public String getEmployeeCode() { return employeeCode; }
    public String getEmployeeName() { return employeeName; }
    public String getDepartment() { return department; }
    public String getDate() { return date; }
    public String getEventTime() { return eventTime; }
    public String getDetail() { return detail; }
}
