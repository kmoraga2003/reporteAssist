package com.quimica.reportassist.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.quimica.reportassist.R;
import com.quimica.reportassist.api.ApiClient;
import com.quimica.reportassist.api.ApiService;
import com.quimica.reportassist.model.LoginRequest;
import com.quimica.reportassist.model.LoginResponse;
import com.quimica.reportassist.util.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etEmail, etPassword, etServerIp;
    private Button btnLogin, btnQuickAdmin, btnQuickEmp1, btnQuickEmp2;
    private ProgressBar progressBar;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sessionManager = new SessionManager(this);

        // Si ya hay sesión abierta, redirigir directo
        if (sessionManager.isLoggedIn()) {
            navigateAfterLogin();
            return;
        }

        initViews();
        setupEvents();
    }

    private void initViews() {
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etServerIp = findViewById(R.id.etServerIp);
        btnLogin = findViewById(R.id.btnLogin);
        btnQuickAdmin = findViewById(R.id.btnQuickAdmin);
        btnQuickEmp1 = findViewById(R.id.btnQuickEmp1);
        btnQuickEmp2 = findViewById(R.id.btnQuickEmp2);
        progressBar = findViewById(R.id.progressBar);

        etServerIp.setText(sessionManager.getServerIp());
    }

    private void setupEvents() {
        btnLogin.setOnClickListener(v -> performLogin());

        // Accesos rápidos para demos y presentación ante el profesor
        btnQuickAdmin.setOnClickListener(v -> {
            etEmail.setText("admin@quimica.cl");
            etPassword.setText("admin123");
        });

        btnQuickEmp1.setOnClickListener(v -> {
            etEmail.setText("carlos.silva@quimica.cl");
            etPassword.setText("123456");
        });

        btnQuickEmp2.setOnClickListener(v -> {
            etEmail.setText("maria.gonzalez@quimica.cl");
            etPassword.setText("123456");
        });
    }

    private void performLogin() {
        String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
        String pass = etPassword.getText() != null ? etPassword.getText().toString().trim() : "";
        String host = etServerIp.getText() != null ? etServerIp.getText().toString().trim() : "10.0.2.2:8080";

        if (email.isEmpty()) {
            etEmail.setError("Ingresa tu correo");
            return;
        }
        if (pass.isEmpty()) {
            etPassword.setError("Ingresa tu contraseña");
            return;
        }

        setLoading(true);

        ApiService api = ApiClient.getService(host);
        LoginRequest request = new LoginRequest(email, pass);

        api.login(request).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                setLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse user = response.body();
                    sessionManager.saveSession(user, host);
                    Toast.makeText(LoginActivity.this, "¡Bienvenido " + user.getFullName() + "!", Toast.LENGTH_SHORT).show();
                    navigateAfterLogin();
                } else {
                    Toast.makeText(LoginActivity.this, "Credenciales incorrectas (" + response.code() + ")", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                setLoading(false);
                Toast.makeText(LoginActivity.this, "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void navigateAfterLogin() {
        if (sessionManager.isAdmin()) {
            startActivity(new Intent(this, AdminDashboardActivity.class));
        } else {
            startActivity(new Intent(this, EmployeeReportActivity.class));
        }
        finish();
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnLogin.setEnabled(!loading);
    }
}
