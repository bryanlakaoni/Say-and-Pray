package com.example.sayandpray;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class ReminderReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("CEK_ALARM", "Alarm berbunyi! Masuk ke Receiver");

        String title = intent.getStringExtra("title");
        String message = intent.getStringExtra("message");
        int reminderId = intent.getIntExtra("id", 1);

        // --- PERBAIKAN DI SINI ---
        // Ganti ReminderActivity.class menjadi MainActivity.class
        Intent activityIntent = new Intent(context, MainActivity.class);

        // Flag agar aplikasi terbuka fresh atau tidak menumpuk
        activityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, activityIntent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "doa_channel")
                .setSmallIcon(R.drawable.ic_launcher_foreground) // Pastikan icon ini ada, atau ganti ic_alarm
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        // Cek izin lagi sebelum notify (Standar Android 13)
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            notificationManager.notify(reminderId, builder.build());
            Log.d("CEK_ALARM", "Notifikasi berhasil dimunculkan ID: " + reminderId);
        } else {
            Log.e("CEK_ALARM", "Gagal memunculkan notifikasi: Izin POST_NOTIFICATIONS tidak ada.");
        }
    }
}