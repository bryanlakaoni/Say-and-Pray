package com.example.sayandpray;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class InputActivity extends AppCompatActivity {

    EditText edtJudul, edtIsi;
    Button btnSimpan;
    DatabaseHelper db;
    BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_input);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        bottomNav = findViewById(R.id.bottom_navigation);
        BottomNavHelper.setupBottomNavigation(this, bottomNav, R.id.nav_input);

        edtJudul = findViewById(R.id.edtJudulDoa);
        edtIsi = findViewById(R.id.edtIsiDoa);
        btnSimpan = findViewById(R.id.submitInputDoa);
        db = DatabaseHelper.getInstance(this);

        btnSimpan.setOnClickListener(v -> {
            String judul = edtJudul.getText().toString().trim();
            String isi = edtIsi.getText().toString().trim();

            if (judul.isEmpty() || isi.isEmpty()) {
                Toast.makeText(InputActivity.this, "Judul dan Isi doa tidak boleh kosong!", Toast.LENGTH_SHORT).show();
            } else {
                boolean isInserted = db.insertDoa(judul, isi);

                if (isInserted) {
                    Toast.makeText(InputActivity.this, "Doa berhasil disimpan!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(InputActivity.this, PrayersActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(InputActivity.this, "Gagal menyimpan doa", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}