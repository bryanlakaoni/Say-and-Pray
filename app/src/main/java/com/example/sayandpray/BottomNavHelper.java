package com.example.sayandpray;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class BottomNavHelper {

    public static void setupBottomNavigation(Context context, BottomNavigationView bottomNavigationView, int selectedItemId) {

        bottomNavigationView.setSelectedItemId(selectedItemId);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == selectedItemId) {
                return true;
            }

            Intent intent = null;
            if (itemId == R.id.nav_home) {
                intent = new Intent(context, MainActivity.class);
            } else if (itemId == R.id.nav_prayer) {
                intent = new Intent(context, PrayersActivity.class);
            } else if (itemId == R.id.nav_input) {
                intent = new Intent(context, InputActivity.class);
            } else if (itemId == R.id.nav_favorite) {
                intent = new Intent(context, FavoriteActivity.class);
            }

            if (intent != null) {
                context.startActivity(intent);
                if (context instanceof Activity) {
                    ((Activity) context).overridePendingTransition(0, 0);
                    ((Activity) context).finish();
                }
                return true;
            }

            return false;
        });
    }
}