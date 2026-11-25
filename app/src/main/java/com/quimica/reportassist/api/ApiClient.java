package com.quimica.reportassist.api;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.concurrent.TimeUnit;

public class ApiClient {

    private static Retrofit retrofit = null;
    private static String currentBaseUrl = "http://10.0.2.2:8080/";

    public static synchronized ApiService getService(String hostPort) {
        String formattedUrl = sanitizeUrl(hostPort);
        
        if (retrofit == null || !currentBaseUrl.equals(formattedUrl)) {
            currentBaseUrl = formattedUrl;

            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(new MockFallbackInterceptor())
                    .addInterceptor(logging)
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(10, TimeUnit.SECONDS)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(currentBaseUrl)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(ApiService.class);
    }

    private static String sanitizeUrl(String raw) {
        if (raw == null || raw.trim().isEmpty()) {
            return "http://10.0.2.2:8080/";
        }
        String clean = raw.trim();
        if (!clean.startsWith("http://") && !clean.startsWith("https://")) {
            clean = "http://" + clean;
        }
        if (!clean.endsWith("/")) {
            clean += "/";
        }
        return clean;
    }
}
