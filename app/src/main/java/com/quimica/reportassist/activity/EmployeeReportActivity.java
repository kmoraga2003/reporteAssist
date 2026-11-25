package com.quimica.reportassist.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.quimica.reportassist.R;
import com.quimica.reportassist.adapter.EmployeeAttendanceAdapter;
import com.quimica.reportassist.api.ApiClient;
import com.quimica.reportassist.api.ApiService;
import com.quimica.reportassist.model.AttendanceRecord;
import com.quimica.reportassist.util.SessionManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EmployeeReportActivity extends AppCompatActivity {

    private TextView tvEmpFullName, tvEmpDeptPos, tvTodayStatusBadge, tvTodayCheckIn, tvTodayCheckOut;
    private Button btnEmpLogout;
    private SwipeRefreshLayout empSwipeRefresh;
    private RecyclerView rvEmpHistory;

    private EmployeeAttendanceAdapter adapter;
    private SessionManager sessionManager;

    // Polling en tiempo real (5 segundos)
    private final Handler pollingHandler = new Handler(Looper.getMainLooper());
    private static final int POLLING_INTERVAL_MS = 5000;
    private final Runnable pollingRunnable = new Runnable() {
        @Override
        public void run() {
            loadPersonalData(false);
            pollingHandler.postDelayed(this, POLLING_INTERVAL_MS);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_report);

        sessionManager = new SessionManager(this);
        initViews();
        setupRecyclerView();
        setupEvents();
    }

    private void initViews() {
        tvEmpFullName = findViewById(R.id.tvEmpFullName);
        tvEmpDeptPos = findViewById(R.id.tvEmpDeptPos);
        tvTodayStatusBadge = findViewById(R.id.tvTodayStatusBadge);
        tvTodayCheckIn = findViewById(R.id.tvTodayCheckIn);
        tvTodayCheckOut = findViewById(R.id.tvTodayCheckOut);
        btnEmpLogout = findViewById(R.id.btnEmpLogout);
        empSwipeRefresh = findViewById(R.id.empSwipeRefresh);
        rvEmpHistory = findViewById(R.id.rvEmpHistory);

        tvEmpFullName.setText(sessionManager.getUserName());
        tvEmpDeptPos.setText(sessionManager.getUserPos() + " • " + sessionManager.getUserDept());
    }

    private void setupRecyclerView() {
        adapter = new EmployeeAttendanceAdapter(this);
        rvEmpHistory.setLayoutManager(new LinearLayoutManager(this));
        rvEmpHistory.setAdapter(adapter);
    }

    private void setupEvents() {
        empSwipeRefresh.setOnRefreshListener(() -> loadPersonalData(true));

        btnEmpLogout.setOnClickListener(v -> {
            sessionManager.logout();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    private void loadPersonalData(boolean showSpinner) {
        if (showSpinner) empSwipeRefresh.setRefreshing(true);

        Long empId = sessionManager.getUserId();
        ApiService api = ApiClient.getService(sessionManager.getServerIp());

        // 1. Cargar estado de hoy
        api.getTodayAttendanceForEmployee(empId).enqueue(new Callback<AttendanceRecord>() {
            @Override
            public void onResponse(Call<AttendanceRecord> call, Response<AttendanceRecord> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AttendanceRecord today = response.body();
                    String inTime = (today.getCheckInTime() != null && today.getCheckInTime().length() >= 16)
                            ? today.getCheckInTime().substring(11, 16) : "--:--";
                    String outTime = (today.getCheckOutTime() != null && today.getCheckOutTime().length() >= 16)
                            ? today.getCheckOutTime().substring(11, 16) : "En jornada";

                    tvTodayCheckIn.setText(inTime);
                    tvTodayCheckOut.setText(outTime);

                    if ("LATE".equalsIgnoreCase(today.getStatus())) {
                        tvTodayStatusBadge.setText("ATRASADO");
                        tvTodayStatusBadge.setBackgroundResource(R.drawable.bg_badge_late);
                        tvTodayStatusBadge.setTextColor(ContextCompat.getColor(EmployeeReportActivity.this, R.color.status_late));
                    } else {
                        tvTodayStatusBadge.setText("A TIEMPO");
                        tvTodayStatusBadge.setBackgroundResource(R.drawable.bg_badge_ontime);
                        tvTodayStatusBadge.setTextColor(ContextCompat.getColor(EmployeeReportActivity.this, R.color.status_ontime));
                    }
                } else {
                    // Sin marca hoy todavía
                    tvTodayCheckIn.setText("Pendiente");
                    tvTodayCheckOut.setText("Pendiente");
                    tvTodayStatusBadge.setText("SIN REGISTRO");
                    tvTodayStatusBadge.setBackgroundResource(R.drawable.bg_badge_absent);
                    tvTodayStatusBadge.setTextColor(ContextCompat.getColor(EmployeeReportActivity.this, R.color.status_absent));
                }
            }

            @Override
            public void onFailure(Call<AttendanceRecord> call, Throwable t) {
                // Silencioso en auto-refresh
            }
        });

        // 2. Cargar historial completo de asistencias
        api.getEmployeeAttendances(empId).enqueue(new Callback<List<AttendanceRecord>>() {
            @Override
            public void onResponse(Call<List<AttendanceRecord>> call, Response<List<AttendanceRecord>> response) {
                empSwipeRefresh.setRefreshing(false);
                if (response.isSuccessful() && response.body() != null) {
                    adapter.updateData(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<AttendanceRecord>> call, Throwable t) {
                empSwipeRefresh.setRefreshing(false);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPersonalData(true);
        pollingHandler.postDelayed(pollingRunnable, POLLING_INTERVAL_MS);
    }

    @Override
    protected void onPause() {
        super.onPause();
        pollingHandler.removeCallbacks(pollingRunnable);
    }
}
