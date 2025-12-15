package com.example.sayandpray;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.sayandpray.model.Reminder;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class ReminderActivity extends AppCompatActivity {
    DatabaseHelper db;
    ListView listReminder;
    ImageView backIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reminder);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        backIcon = findViewById(R.id.backIcon);
        if (backIcon != null) {
            backIcon.setOnClickListener(v -> finish());
        }

        listReminder = findViewById(R.id.listDataReminder);
        db = DatabaseHelper.getInstance(this);
        loadReminderData();
    }

    private void loadReminderData() {
        ArrayList<Reminder> remind = db.getListReminder();
        ListViewAdapterReminder adapter = new ListViewAdapterReminder(this, R.layout.item_reminder, remind);
        listReminder.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadReminderData();
    }
}