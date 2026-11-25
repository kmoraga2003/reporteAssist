package com.quimica.reportassist.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.quimica.reportassist.R;
import com.quimica.reportassist.api.ApiClient;
import com.quimica.reportassist.api.ApiService;
import com.quimica.reportassist.model.LoginRequest;
import com.quimica.reportassist.model.LoginResponse;
import com.quimica.reportassist.util.SessionManager;

import java.util.concurrent.Executor;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etEmail, etPassword, etServerIp;
    private Button btnLogin, btnBiometric, btnQuickAdmin, btnQuickEmp1, btnQuickEmp2;
    private ProgressBar progressBar;
    private SessionManager sessionManager;

    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;

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
        setupBiometrics();
    }

    private void initViews() {
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etServerIp = findViewById(R.id.etServerIp);
        btnLogin = findViewById(R.id.btnLogin);
        btnBiometric = findViewById(R.id.btnBiometric);
        btnQuickAdmin = findViewById(R.id.btnQuickAdmin);
        btnQuickEmp1 = findViewById(R.id.btnQuickEmp1);
        btnQuickEmp2 = findViewById(R.id.btnQuickEmp2);
        progressBar = findViewById(R.id.progressBar);

        etServerIp.setText(sessionManager.getServerIp());
    }

    private void setupEvents() {
        btnLogin.setOnClickListener(v -> performLogin());

        btnBiometric.setOnClickListener(v -> {
            if (biometricPrompt != null && promptInfo != null) {
                biometricPrompt.authenticate(promptInfo);
            }
        });

        // Accesos rápidos para demos
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

    private void setupBiometrics() {
        BiometricManager biometricManager = BiometricManager.from(this);
        int canAuthenticate = biometricManager.canAuthenticate(
                BiometricManager.Authenticators.BIOMETRIC_STRONG | BiometricManager.Authenticators.BIOMETRIC_WEAK
        );

        if (canAuthenticate == BiometricManager.BIOMETRIC_SUCCESS) {
            btnBiometric.setVisibility(View.VISIBLE);

            Executor executor = ContextCompat.getMainExecutor(this);
            biometricPrompt = new BiometricPrompt(LoginActivity.this, executor, new BiometricPrompt.AuthenticationCallback() {
                @Override
                public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                    super.onAuthenticationError(errorCode, errString);
                    if (errorCode != BiometricPrompt.ERROR_NEGATIVE_BUTTON && errorCode != BiometricPrompt.ERROR_USER_CANCELED) {
                        Toast.makeText(LoginActivity.this, getString(R.string.biometric_error, errString), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                    super.onAuthenticationSucceeded(result);
                    Toast.makeText(LoginActivity.this, "¡Autenticación biométrica exitosa!", Toast.LENGTH_SHORT).show();
                    performLogin();
                }

                @Override
                public void onAuthenticationFailed() {
                    super.onAuthenticationFailed();
                    Toast.makeText(LoginActivity.this, getString(R.string.biometric_failed), Toast.LENGTH_SHORT).show();
                }
            });

            promptInfo = new BiometricPrompt.PromptInfo.Builder()
                    .setTitle(getString(R.string.biometric_title))
                    .setSubtitle(getString(R.string.biometric_subtitle))
                    .setNegativeButtonText(getString(R.string.biometric_negative))
                    .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG | BiometricManager.Authenticators.BIOMETRIC_WEAK)
                    .build();
        } else {
            // Si el hardware no lo soporta o no hay huella registrada, se mantiene oculto sin afectar nada
            btnBiometric.setVisibility(View.GONE);
        }
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
