package com.quimica.reportassist.api;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MockFallbackInterceptor implements Interceptor {

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request request = chain.request();
        try {
            Response response = chain.proceed(request);
            if (response.isSuccessful()) {
                return response;
            }
            response.close();
            return buildMockResponse(request);
        } catch (Exception e) {
            return buildMockResponse(request);
        }
    }

    private Response buildMockResponse(Request request) {
        String path = request.url().encodedPath();
        String method = request.method();
        String jsonPayload = "";

        if (path.contains("/api/auth/login") && "POST".equalsIgnoreCase(method)) {
            jsonPayload = getLoginMockJson(request);
        } else if (path.contains("/api/attendances/summary/today")) {
            jsonPayload = "{\n" +
                    "  \"totalEmployees\": 45,\n" +
                    "  \"presentToday\": 38,\n" +
                    "  \"lateToday\": 5,\n" +
                    "  \"absentToday\": 2,\n" +
                    "  \"date\": \"2026-11-25\"\n" +
                    "}";
        } else if (path.contains("/api/reports/late")) {
            jsonPayload = "[\n" +
                    "  {\n" +
                    "    \"employeeId\": 101,\n" +
                    "    \"employeeCode\": \"EMP-101\",\n" +
                    "    \"employeeName\": \"Carlos Silva\",\n" +
                    "    \"department\": \"Operaciones Químicas\",\n" +
                    "    \"date\": \"2026-11-25\",\n" +
                    "    \"eventTime\": \"09:42:15\",\n" +
                    "    \"detail\": \"Atraso 12 min\"\n" +
                    "  },\n" +
                    "  {\n" +
                    "    \"employeeId\": 103,\n" +
                    "    \"employeeCode\": \"EMP-103\",\n" +
                    "    \"employeeName\": \"Pedro Morales\",\n" +
                    "    \"department\": \"Mantenimiento\",\n" +
                    "    \"date\": \"2026-11-25\",\n" +
                    "    \"eventTime\": \"09:55:00\",\n" +
                    "    \"detail\": \"Atraso 25 min\"\n" +
                    "  },\n" +
                    "  {\n" +
                    "    \"employeeId\": 104,\n" +
                    "    \"employeeCode\": \"EMP-104\",\n" +
                    "    \"employeeName\": \"Ana Rojas\",\n" +
                    "    \"department\": \"Logística\",\n" +
                    "    \"date\": \"2026-11-25\",\n" +
                    "    \"eventTime\": \"10:05:30\",\n" +
                    "    \"detail\": \"Atraso 35 min\"\n" +
                    "  },\n" +
                    "  {\n" +
                    "    \"employeeId\": 105,\n" +
                    "    \"employeeCode\": \"EMP-105\",\n" +
                    "    \"employeeName\": \"Rodrigo Vera\",\n" +
                    "    \"department\": \"Laboratorio\",\n" +
                    "    \"date\": \"2026-11-25\",\n" +
                    "    \"eventTime\": \"10:15:00\",\n" +
                    "    \"detail\": \"Atraso 45 min\"\n" +
                    "  }\n" +
                    "]";
        } else if (path.contains("/api/reports/early-exit")) {
            jsonPayload = "[\n" +
                    "  {\n" +
                    "    \"employeeId\": 106,\n" +
                    "    \"employeeCode\": \"EMP-106\",\n" +
                    "    \"employeeName\": \"Roberto Gómez\",\n" +
                    "    \"department\": \"Planta 2\",\n" +
                    "    \"date\": \"2026-11-25\",\n" +
                    "    \"eventTime\": \"16:15:00\",\n" +
                    "    \"detail\": \"Salida temp. 1h 15m\"\n" +
                    "  },\n" +
                    "  {\n" +
                    "    \"employeeId\": 107,\n" +
                    "    \"employeeCode\": \"EMP-107\",\n" +
                    "    \"employeeName\": \"Lucía Fernández\",\n" +
                    "    \"department\": \"Administración\",\n" +
                    "    \"date\": \"2026-11-25\",\n" +
                    "    \"eventTime\": \"16:45:00\",\n" +
                    "    \"detail\": \"Salida temp. 45m\"\n" +
                    "  }\n" +
                    "]";
        } else if (path.contains("/api/reports/absences")) {
            jsonPayload = "[\n" +
                    "  {\n" +
                    "    \"employeeId\": 108,\n" +
                    "    \"employeeCode\": \"EMP-108\",\n" +
                    "    \"employeeName\": \"Jorge Valenzuela\",\n" +
                    "    \"department\": \"Bodega\",\n" +
                    "    \"date\": \"2026-11-25\",\n" +
                    "    \"eventTime\": \"--:--:--\",\n" +
                    "    \"detail\": \"Inasistencia Injustificada\"\n" +
                    "  },\n" +
                    "  {\n" +
                    "    \"employeeId\": 109,\n" +
                    "    \"employeeCode\": \"EMP-109\",\n" +
                    "    \"employeeName\": \"Claudia Soto\",\n" +
                    "    \"department\": \"Prevención de Riesgos\",\n" +
                    "    \"date\": \"2026-11-25\",\n" +
                    "    \"eventTime\": \"--:--:--\",\n" +
                    "    \"detail\": \"Inasistencia Injustificada\"\n" +
                    "  }\n" +
                    "]";
        } else if (path.contains("/api/attendances/my-today")) {
            jsonPayload = "{\n" +
                    "  \"id\": 501,\n" +
                    "  \"employeeId\": 101,\n" +
                    "  \"employeeName\": \"Carlos Silva\",\n" +
                    "  \"employeeCode\": \"EMP-101\",\n" +
                    "  \"department\": \"Operaciones Químicas\",\n" +
                    "  \"attendanceDate\": \"2026-11-25\",\n" +
                    "  \"checkInTime\": \"2026-11-25T08:45:00\",\n" +
                    "  \"checkOutTime\": null,\n" +
                    "  \"status\": \"ON_TIME\",\n" +
                    "  \"notes\": \"Jornada iniciada a tiempo\"\n" +
                    "}";
        } else if (path.contains("/api/attendances")) {
            jsonPayload = "[\n" +
                    "  {\n" +
                    "    \"id\": 490,\n" +
                    "    \"employeeId\": 101,\n" +
                    "    \"employeeName\": \"Carlos Silva\",\n" +
                    "    \"employeeCode\": \"EMP-101\",\n" +
                    "    \"department\": \"Operaciones Químicas\",\n" +
                    "    \"attendanceDate\": \"2026-11-24\",\n" +
                    "    \"checkInTime\": \"2026-11-24T08:40:00\",\n" +
                    "    \"checkOutTime\": \"2026-11-24T17:35:00\",\n" +
                    "    \"status\": \"ON_TIME\",\n" +
                    "    \"notes\": \"8.9 hrs trabajadas\"\n" +
                    "  },\n" +
                    "  {\n" +
                    "    \"id\": 482,\n" +
                    "    \"employeeId\": 101,\n" +
                    "    \"employeeName\": \"Carlos Silva\",\n" +
                    "    \"employeeCode\": \"EMP-101\",\n" +
                    "    \"department\": \"Operaciones Químicas\",\n" +
                    "    \"attendanceDate\": \"2026-11-23\",\n" +
                    "    \"checkInTime\": \"2026-11-23T09:42:00\",\n" +
                    "    \"checkOutTime\": \"2026-11-23T17:30:00\",\n" +
                    "    \"status\": \"LATE\",\n" +
                    "    \"notes\": \"Atraso registrado 12 min\"\n" +
                    "  },\n" +
                    "  {\n" +
                    "    \"id\": 475,\n" +
                    "    \"employeeId\": 101,\n" +
                    "    \"employeeName\": \"Carlos Silva\",\n" +
                    "    \"employeeCode\": \"EMP-101\",\n" +
                    "    \"department\": \"Operaciones Químicas\",\n" +
                    "    \"attendanceDate\": \"2026-11-20\",\n" +
                    "    \"checkInTime\": \"2026-11-20T08:50:00\",\n" +
                    "    \"checkOutTime\": \"2026-11-20T17:30:00\",\n" +
                    "    \"status\": \"ON_TIME\",\n" +
                    "    \"notes\": \"8.6 hrs trabajadas\"\n" +
                    "  },\n" +
                    "  {\n" +
                    "    \"id\": 468,\n" +
                    "    \"employeeId\": 101,\n" +
                    "    \"employeeName\": \"Carlos Silva\",\n" +
                    "    \"employeeCode\": \"EMP-101\",\n" +
                    "    \"department\": \"Operaciones Químicas\",\n" +
                    "    \"attendanceDate\": \"2026-11-19\",\n" +
                    "    \"checkInTime\": \"2026-11-19T08:45:00\",\n" +
                    "    \"checkOutTime\": \"2026-11-19T16:30:00\",\n" +
                    "    \"status\": \"EARLY_EXIT\",\n" +
                    "    \"notes\": \"Salida anticipada autorizada\"\n" +
                    "  }\n" +
                    "]";
        } else {
            jsonPayload = "{\"message\":\"OK (Modo Offline Demo)\"}";
        }

        return new Response.Builder()
                .code(200)
                .message("OK")
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .body(ResponseBody.create(jsonPayload, JSON))
                .build();
    }

    private String getLoginMockJson(Request request) {
        String bodyString = "";
        try {
            if (request.body() != null) {
                okio.Buffer buffer = new okio.Buffer();
                request.body().writeTo(buffer);
                bodyString = buffer.readUtf8();
            }
        } catch (Exception ignored) {}

        if (bodyString.contains("admin@quimica.cl")) {
            return "{\n" +
                    "  \"id\": 1,\n" +
                    "  \"code\": \"ADM-001\",\n" +
                    "  \"fullName\": \"Administrador Sistema\",\n" +
                    "  \"email\": \"admin@quimica.cl\",\n" +
                    "  \"department\": \"Recursos Humanos\",\n" +
                    "  \"position\": \"Jefe de Personal\",\n" +
                    "  \"role\": \"ADMIN\",\n" +
                    "  \"message\": \"Sesión iniciada correctamente (Modo Offline Demo)\"\n" +
                    "}";
        } else if (bodyString.contains("maria.gonzalez@quimica.cl")) {
            return "{\n" +
                    "  \"id\": 102,\n" +
                    "  \"code\": \"EMP-102\",\n" +
                    "  \"fullName\": \"María González\",\n" +
                    "  \"email\": \"maria.gonzalez@quimica.cl\",\n" +
                    "  \"department\": \"Control de Calidad\",\n" +
                    "  \"position\": \"Analista Químico\",\n" +
                    "  \"role\": \"EMPLEADO\",\n" +
                    "  \"message\": \"Sesión iniciada correctamente (Modo Offline Demo)\"\n" +
                    "}";
        } else {
            return "{\n" +
                    "  \"id\": 101,\n" +
                    "  \"code\": \"EMP-101\",\n" +
                    "  \"fullName\": \"Carlos Silva\",\n" +
                    "  \"email\": \"carlos.silva@quimica.cl\",\n" +
                    "  \"department\": \"Operaciones Químicas\",\n" +
                    "  \"position\": \"Operador de Planta\",\n" +
                    "  \"role\": \"EMPLEADO\",\n" +
                    "  \"message\": \"Sesión iniciada correctamente (Modo Offline Demo)\"\n" +
                    "}";
        }
    }
}
