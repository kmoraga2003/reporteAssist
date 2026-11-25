package com.quimica.reportassist.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.quimica.reportassist.R;
import com.quimica.reportassist.adapter.ReportAdapter;
import com.quimica.reportassist.api.ApiClient;
import com.quimica.reportassist.api.ApiService;
import com.quimica.reportassist.model.DashboardSummary;
import com.quimica.reportassist.model.ReportItem;
import com.quimica.reportassist.util.SessionManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminDashboardActivity extends AppCompatActivity {

    private TextView tvAdminEmail, tvCountLate, tvCountEarly, tvCountAbsent, tvEmptyMessage;
    private Button btnLogout, btnTabLate, btnTabEarly, btnTabAbsent;
    private SwipeRefreshLayout swipeRefresh;
    private RecyclerView rvReports;
    private LinearLayout layoutEmptyState;

    private ReportAdapter adapter;
    private SessionManager sessionManager;
    private ReportAdapter.ReportType currentReportType = ReportAdapter.ReportType.LATE;

    // Polling en tiempo real (5 segundos)
    private final Handler pollingHandler = new Handler(Looper.getMainLooper());
    private static final int POLLING_INTERVAL_MS = 5000;
    private final Runnable pollingRunnable = new Runnable() {
        @Override
        public void run() {
            loadReports(false);
            loadSummaryCounters();
            pollingHandler.postDelayed(this, POLLING_INTERVAL_MS);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        sessionManager = new SessionManager(this);
        initViews();
        setupRecyclerView();
        setupTabs();
        setupEvents();
    }

    private void initViews() {
        tvAdminEmail = findViewById(R.id.tvAdminEmail);
        tvCountLate = findViewById(R.id.tvCountLate);
        tvCountEarly = findViewById(R.id.tvCountEarly);
        tvCountAbsent = findViewById(R.id.tvCountAbsent);
        tvEmptyMessage = findViewById(R.id.tvEmptyMessage);
        btnLogout = findViewById(R.id.btnLogout);
        btnTabLate = findViewById(R.id.btnTabLate);
        btnTabEarly = findViewById(R.id.btnTabEarly);
        btnTabAbsent = findViewById(R.id.btnTabAbsent);
        swipeRefresh = findViewById(R.id.swipeRefresh);
        rvReports = findViewById(R.id.rvReports);
        layoutEmptyState = findViewById(R.id.layoutEmptyState);

        tvAdminEmail.setText(sessionManager.getUserEmail() + " (" + sessionManager.getServerIp() + ")");
    }

    private void setupRecyclerView() {
        adapter = new ReportAdapter(this);
        rvReports.setLayoutManager(new LinearLayoutManager(this));
        rvReports.setAdapter(adapter);
    }

    private void setupTabs() {
        btnTabLate.setOnClickListener(v -> selectTab(ReportAdapter.ReportType.LATE));
        btnTabEarly.setOnClickListener(v -> selectTab(ReportAdapter.ReportType.EARLY));
        btnTabAbsent.setOnClickListener(v -> selectTab(ReportAdapter.ReportType.ABSENT));
    }

    private void selectTab(ReportAdapter.ReportType type) {
        currentReportType = type;
        updateTabStyles();
        loadReports(true);
    }

    private void updateTabStyles() {
        int activeBg = ContextCompat.getColor(this, R.color.primary);
        int inactiveBg = Color.parseColor("#E2E8F0");
        int activeText = ContextCompat.getColor(this, R.color.surface_white);
        int inactiveText = ContextCompat.getColor(this, R.color.text_primary);

        btnTabLate.setBackgroundColor(currentReportType == ReportAdapter.ReportType.LATE ? activeBg : inactiveBg);
        btnTabLate.setTextColor(currentReportType == ReportAdapter.ReportType.LATE ? activeText : inactiveText);

        btnTabEarly.setBackgroundColor(currentReportType == ReportAdapter.ReportType.EARLY ? activeBg : inactiveBg);
        btnTabEarly.setTextColor(currentReportType == ReportAdapter.ReportType.EARLY ? activeText : inactiveText);

        btnTabAbsent.setBackgroundColor(currentReportType == ReportAdapter.ReportType.ABSENT ? activeBg : inactiveBg);
        btnTabAbsent.setTextColor(currentReportType == ReportAdapter.ReportType.ABSENT ? activeText : inactiveText);
    }

    private void setupEvents() {
        swipeRefresh.setOnRefreshListener(() -> {
            loadReports(true);
            loadSummaryCounters();
        });

        btnLogout.setOnClickListener(v -> {
            sessionManager.logout();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    private void loadReports(boolean showSpinner) {
        if (showSpinner) swipeRefresh.setRefreshing(true);

        ApiService api = ApiClient.getService(sessionManager.getServerIp());
        Call<List<ReportItem>> call;

        if (currentReportType == ReportAdapter.ReportType.LATE) {
            call = api.getLateArrivals(null, null);
        } else if (currentReportType == ReportAdapter.ReportType.EARLY) {
            call = api.getEarlyExits(null, null);
        } else {
            call = api.getAbsences(null);
        }

        call.enqueue(new Callback<List<ReportItem>>() {
            @Override
            public void onResponse(Call<List<ReportItem>> call, Response<List<ReportItem>> response) {
                swipeRefresh.setRefreshing(false);
                if (response.isSuccessful() && response.body() != null) {
                    List<ReportItem> data = response.body();
                    adapter.updateData(data, currentReportType);

                    if (data.isEmpty()) {
                        layoutEmptyState.setVisibility(View.VISIBLE);
                        rvReports.setVisibility(View.GONE);
                    } else {
                        layoutEmptyState.setVisibility(View.GONE);
                        rvReports.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<ReportItem>> call, Throwable t) {
                swipeRefresh.setRefreshing(false);
            }
        });
    }

    private void loadSummaryCounters() {
        ApiService api = ApiClient.getService(sessionManager.getServerIp());
        api.getTodaySummary().enqueue(new Callback<DashboardSummary>() {
            @Override
            public void onResponse(Call<DashboardSummary> call, Response<DashboardSummary> response) {
                if (response.isSuccessful() && response.body() != null) {
                    DashboardSummary s = response.body();
                    tvCountLate.setText(String.valueOf(s.getLateToday()));
                    tvCountAbsent.setText(String.valueOf(s.getAbsentToday()));
                    // Para salidas tempranas o presentes
                    tvCountEarly.setText(String.valueOf(s.getPresentToday()));
                }
            }

            @Override
            public void onFailure(Call<DashboardSummary> call, Throwable t) {
                // Silencioso en auto-refresh
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadReports(true);
        loadSummaryCounters();
        pollingHandler.postDelayed(pollingRunnable, POLLING_INTERVAL_MS);
    }

    @Override
    protected void onPause() {
        super.onPause();
        pollingHandler.removeCallbacks(pollingRunnable);
    }
}
