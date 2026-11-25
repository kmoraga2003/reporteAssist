package com.quimica.reportassist.model;

import com.google.gson.annotations.SerializedName;

public class AttendanceRecord {
    @SerializedName("id")
    private Long id;

    @SerializedName("employeeId")
    private Long employeeId;

    @SerializedName("employeeName")
    private String employeeName;

    @SerializedName("employeeCode")
    private String employeeCode;

    @SerializedName("department")
    private String department;

    @SerializedName("attendanceDate")
    private String attendanceDate;

    @SerializedName("checkInTime")
    private String checkInTime;

    @SerializedName("checkOutTime")
    private String checkOutTime;

    @SerializedName("status")
    private String status; // ON_TIME, LATE, EARLY_EXIT, NORMAL

    @SerializedName("notes")
    private String notes;

    public Long getId() { return id; }
    public Long getEmployeeId() { return employeeId; }
    public String getEmployeeName() { return employeeName; }
    public String getEmployeeCode() { return employeeCode; }
    public String getDepartment() { return department; }
    public String getAttendanceDate() { return attendanceDate; }
    public String getCheckInTime() { return checkInTime; }
    public String getCheckOutTime() { return checkOutTime; }
    public String getStatus() { return status; }
    public String getNotes() { return notes; }

    public String getFormattedTimes() {
        String in = (checkInTime != null && checkInTime.length() >= 16) ? checkInTime.substring(11, 16) : "--:--";
        String out = (checkOutTime != null && checkOutTime.length() >= 16) ? checkOutTime.substring(11, 16) : "En jornada";
        return "Entrada: " + in + " | Salida: " + out;
    }
}
