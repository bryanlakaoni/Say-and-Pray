package com.example.sayandpray;

import android.os.Bundle;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CalendarActivity extends AppCompatActivity {
    private TextView tvLiturgicalInfo;
    private ImageView backIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_calendar);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        CalendarView calendarView = findViewById(R.id.calendarView);
        TextView tvDateSelected = findViewById(R.id.tvDateSelected);
        tvLiturgicalInfo = findViewById(R.id.tvLiturgicalInfo);

        backIcon = findViewById(R.id.backIcon);
        if (backIcon != null) {
            backIcon.setOnClickListener(v -> finish());
        }

        if (calendarView != null) {
            calendarView.setOnDateChangeListener((view1, year, month, dayOfMonth) -> {
                int realMonth = month + 1;
                String dateString = dayOfMonth + " - " + realMonth + " - " + year;

                if (tvDateSelected != null) {
                    tvDateSelected.setText("Tanggal: " + dateString);
                }
                tvLiturgicalInfo.setText("Sedang memuat data...");
                fetchLiturgicalData(year, realMonth, dayOfMonth);
            });
        }
    }

    private void fetchLiturgicalData(int year, int month, int day) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://calapi.inadiutorium.cz/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        LiturgyApiService service = retrofit.create(LiturgyApiService.class);
        Call<LiturgyResponse> call = service.getLiturgyDay(year, month, day);

        call.enqueue(new Callback<LiturgyResponse>() {
            @Override
            public void onResponse(Call<LiturgyResponse> call, Response<LiturgyResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LiturgyResponse data = response.body();
                    String season = data.season;
                    String celebrationName = "Hari Biasa";

                    if (data.celebrations != null && !data.celebrations.isEmpty()) {
                        celebrationName = data.celebrations.get(0).title;
                    }

                    String hasil = "Masa: " + season + "\nPerayaan: " + celebrationName;

                    tvLiturgicalInfo.setText(hasil);
                } else {
                    tvLiturgicalInfo.setText("Gagal mengambil data.");
                }
            }

            @Override
            public void onFailure(Call<LiturgyResponse> call, Throwable t) {
                tvLiturgicalInfo.setText("Error: " + t.getMessage());
            }
        });
    }
}