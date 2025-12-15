package com.example.sayandpray;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.sayandpray.model.Product;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class FavoriteActivity extends AppCompatActivity {

    DatabaseHelper db;
    ListView listFavorite;
    BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_favorite);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        bottomNav = findViewById(R.id.bottom_navigation);
        BottomNavHelper.setupBottomNavigation(this, bottomNav, R.id.nav_favorite);

        listFavorite = findViewById(R.id.listDataFavorite);
        db = DatabaseHelper.getInstance(this);

        loadFavoriteData();
        listFavorite.setOnItemClickListener(((parent, v, position, id) -> {
            Product selected = (Product) parent.getItemAtPosition(position);

            Intent intent = new Intent(FavoriteActivity.this, DetailActivity.class);
            intent.putExtra("doaId", selected.getIdDoa());
            intent.putExtra("namaDoa", selected.getNamaDoa());
            intent.putExtra("detailDoa", selected.getDetailDoa());
            intent.putExtra("isFavDoa", selected.getIsFavDoa());

            startActivity(intent);
        }));
    }

    private void loadFavoriteData() {
        ArrayList<Product> favorites = db.getListDoaFavorit();
        ListViewAdapter adapter = new ListViewAdapter(this, R.layout.item, favorites);
        listFavorite.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadFavoriteData();
    }
}