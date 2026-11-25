package com.quimica.reportassist.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.quimica.reportassist.model.LoginResponse;

public class SessionManager {

    private static final String PREF_NAME = "ReportAssistSession";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_USER_CODE = "userCode";
    private static final String KEY_USER_NAME = "userName";
    private static final String KEY_USER_EMAIL = "userEmail";
    private static final String KEY_USER_DEPT = "userDept";
    private static final String KEY_USER_POS = "userPos";
    private static final String KEY_USER_ROLE = "userRole";
    private static final String KEY_SERVER_IP = "serverIp";

    private final SharedPreferences prefs;
    private final SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public void saveSession(LoginResponse user, String serverIp) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putLong(KEY_USER_ID, user.getId() != null ? user.getId() : -1L);
        editor.putString(KEY_USER_CODE, user.getCode());
        editor.putString(KEY_USER_NAME, user.getFullName());
        editor.putString(KEY_USER_EMAIL, user.getEmail());
        editor.putString(KEY_USER_DEPT, user.getDepartment());
        editor.putString(KEY_USER_POS, user.getPosition());
        editor.putString(KEY_USER_ROLE, user.getRole());
        editor.putString(KEY_SERVER_IP, serverIp);
        editor.apply();
    }

    public void saveServerIp(String serverIp) {
        editor.putString(KEY_SERVER_IP, serverIp);
        editor.apply();
    }

    public String getServerIp() {
        return prefs.getString(KEY_SERVER_IP, "10.0.2.2:8080");
    }

    public boolean isLoggedIn() {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public Long getUserId() {
        return prefs.getLong(KEY_USER_ID, -1L);
    }

    public String getUserName() {
        return prefs.getString(KEY_USER_NAME, "Usuario");
    }

    public String getUserEmail() {
        return prefs.getString(KEY_USER_EMAIL, "");
    }

    public String getUserRole() {
        return prefs.getString(KEY_USER_ROLE, "EMPLEADO");
    }

    public String getUserDept() {
        return prefs.getString(KEY_USER_DEPT, "Planta");
    }

    public String getUserPos() {
        return prefs.getString(KEY_USER_POS, "Operario");
    }

    public boolean isAdmin() {
        return "ADMIN".equalsIgnoreCase(getUserRole());
    }

    public void logout() {
        String savedIp = getServerIp();
        editor.clear();
        editor.putString(KEY_SERVER_IP, savedIp); // Conservar la IP configurada
        editor.apply();
    }
}
