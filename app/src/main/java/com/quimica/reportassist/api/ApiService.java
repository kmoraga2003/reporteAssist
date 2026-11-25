package com.quimica.reportassist.api;

import com.quimica.reportassist.model.AttendanceRecord;
import com.quimica.reportassist.model.DashboardSummary;
import com.quimica.reportassist.model.LoginRequest;
import com.quimica.reportassist.model.LoginResponse;
import com.quimica.reportassist.model.ReportItem;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    // 1. Iniciar sesión
    @POST("api/auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    // 2. RE-01: Reporte de Atrasos (post 09:30 AM)
    @GET("api/reports/late")
    Call<List<ReportItem>> getLateArrivals(
            @Query("startDate") String startDate,
            @Query("endDate") String endDate
    );

    // 3. RE-02: Reporte de Salidas Anticipadas (pre 17:30 PM)
    @GET("api/reports/early-exit")
    Call<List<ReportItem>> getEarlyExits(
            @Query("startDate") String startDate,
            @Query("endDate") String endDate
    );

    // 4. RE-03: Reporte de Inasistencias
    @GET("api/reports/absences")
    Call<List<ReportItem>> getAbsences(
            @Query("date") String date
    );

    // 5. Historial de asistencias de un empleado específico
    @GET("api/attendances")
    Call<List<AttendanceRecord>> getEmployeeAttendances(
            @Query("employeeId") Long employeeId
    );

    // 6. Asistencia de hoy para un empleado
    @GET("api/attendances/my-today/{employeeId}")
    Call<AttendanceRecord> getTodayAttendanceForEmployee(
            @Path("employeeId") Long employeeId
    );

    // 7. Resumen estadístico del día para Admin
    @GET("api/attendances/summary/today")
    Call<DashboardSummary> getTodaySummary();
}
