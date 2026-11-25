package com.quimica.reportassist.model;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {
    @SerializedName("id")
    private Long id;

    @SerializedName("code")
    private String code;

    @SerializedName("fullName")
    private String fullName;

    @SerializedName("email")
    private String email;

    @SerializedName("department")
    private String department;

    @SerializedName("position")
    private String position;

    @SerializedName("role")
    private String role; // "ADMIN" o "EMPLEADO"

    @SerializedName("message")
    private String message;

    public Long getId() { return id; }
    public String getCode() { return code; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getDepartment() { return department; }
    public String getPosition() { return position; }
    public String getRole() { return role; }
    public String getMessage() { return message; }

    public boolean isAdmin() {
        return "ADMIN".equalsIgnoreCase(role);
    }
}
