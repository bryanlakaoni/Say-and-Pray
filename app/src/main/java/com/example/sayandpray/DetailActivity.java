package com.example.sayandpray;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Calendar;

public class DetailActivity extends AppCompatActivity {

    ImageView iconFav, iconBack, deleteIcon;
    TextView title, detail;
    DatabaseHelper db;
    Button reminderBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detail);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // --- 1. MINTA IZIN NOTIFIKASI (WAJIB UTK ANDROID 13+) ---
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }
        // --------------------------------------------------------

        int doaId = getIntent().getIntExtra("doaId", -1);
        String doaNama = getIntent().getStringExtra("namaDoa");
        String doaDetail = getIntent().getStringExtra("detailDoa");
        final boolean[] doaFav = {getIntent().getBooleanExtra("isFavDoa", false)};

        title = findViewById(R.id.judulDoa);
        detail = findViewById(R.id.isiDoa);

        title.setText(doaNama);
        detail.setText(doaDetail);

        iconFav = findViewById(R.id.favIcon);
        iconFav.setImageResource(doaFav[0] ? R.drawable.fav_filled : R.drawable.fav_outline);

        deleteIcon = findViewById(R.id.deleteIcon);

        db = DatabaseHelper.getInstance(this);

        iconFav.setOnClickListener(v -> {
            boolean newFav = !doaFav[0];
            boolean updated = db.updateFavorit(doaId, newFav);

            if (updated) {
                if (newFav) {
                    iconFav.setImageResource(R.drawable.fav_filled);
                } else {
                    iconFav.setImageResource(R.drawable.fav_outline);
                }
                doaFav[0] = newFav;
            }
        });

        deleteIcon.setOnClickListener(v -> {
            new android.app.AlertDialog.Builder(this)
                    .setTitle("Hapus Doa")
                    .setMessage("Apakah Anda yakin ingin menghapus doa ini secara permanen?")
                    .setPositiveButton("Ya, Hapus", (dialog, which) -> {
                        boolean isDeleted = db.deleteDoa(doaId);

                        if (isDeleted) {
                            Toast.makeText(DetailActivity.this, "Doa berhasil dihapus", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(DetailActivity.this, "Gagal menghapus doa", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Batal", null)
                    .show();
        });

        iconBack = findViewById(R.id.backIcon);
        iconBack.setOnClickListener(v -> {
            finish();
        });

        reminderBtn = findViewById(R.id.buttonReminder);

        reminderBtn.setOnClickListener(v -> {
            // --- 2. CEK IZIN ALARM PRESISI (ANDROID 12+) ---
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                if (alarmManager != null && !alarmManager.canScheduleExactAlarms()) {
                    Toast.makeText(this, "Izinkan 'Alarms & Reminders' agar pengingat berfungsi", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                    startActivity(intent);
                    return; // Stop, jangan lanjut buka jam dulu
                }
            }

            // --- 3. BUKA TIME PICKER ---
            Calendar now = Calendar.getInstance();
            int hour = now.get(Calendar.HOUR_OF_DAY);
            int minute = now.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(DetailActivity.this, (view, selectedHour, selectedMinute) -> {

                String waktu = String.format("Reminder diset pukul %02d:%02d", selectedHour, selectedMinute);
                // Toast.makeText(DetailActivity.this, waktu, Toast.LENGTH_SHORT).show();

                // Simpan ke Database
                long idBaru = db.insertReminder(doaId, selectedHour, selectedMinute);

                if (idBaru != -1) {
                    // Panggil fungsi Schedule Alarm
                    scheduleAlarm(this, (int) idBaru, selectedHour, selectedMinute, doaNama);
                    Toast.makeText(DetailActivity.this, "Reminder berhasil dibuat", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(DetailActivity.this, "Gagal menyimpan reminder", Toast.LENGTH_SHORT).show();
                }
            }, hour, minute, true);

            timePickerDialog.show();
        });
    }

    // --- FUNGSI UTK MENJADWALKAN ALARM KE SYSTEM ANDROID ---
    public static void scheduleAlarm(Context context, int reminderId, int hour, int minute, String titleDoa) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        // Jika waktu yang dipilih sudah lewat hari ini, set untuk besok
        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        Log.d("CEK_ALARM", "Set Alarm ID: " + reminderId + " | Waktu: " + calendar.getTime().toString());

        String jamFormat = String.format("%02d:%02d", hour, minute);

        Intent intent = new Intent(context, ReminderReceiver.class);
        intent.putExtra("title", "Reminder: " + titleDoa);
        intent.putExtra("message", "Halo! Ini sudah jam " + jamFormat + ", jangan lupa berdoa ya!");
        intent.putExtra("id", reminderId);

        // FLAG_IMMUTABLE wajib untuk Android 12+
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                reminderId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        if (alarmManager != null) {
            try {
                // Gunakan setExactAndAllowWhileIdle agar tetap bunyi di mode Doze/Sleep
                alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        calendar.getTimeInMillis(),
                        pendingIntent
                );
                Log.d("CEK_ALARM", "Sukses perintah ke AlarmManager");
            } catch (SecurityException e) {
                Log.e("CEK_ALARM", "Gagal set alarm: Izin ditolak! " + e.getMessage());
            }
        }
    }

    // Fungsi pembatalan alarm (jika nanti dibutuhkan di ReminderActivity)
    public static void cancelAlarm(Context context, int reminderId) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, ReminderReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                reminderId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
            Log.d("CEK_ALARM", "Alarm ID " + reminderId + " dibatalkan.");
        }
    }
}