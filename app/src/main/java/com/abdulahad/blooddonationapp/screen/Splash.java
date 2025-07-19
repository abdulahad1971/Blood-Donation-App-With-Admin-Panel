package com.abdulahad.blooddonationapp.screen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.view.Window;
import android.view.WindowManager;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.abdulahad.blooddonationapp.MainActivity;
import com.abdulahad.blooddonationapp.R;
import com.abdulahad.blooddonationapp.admin.AdminActivity;
import com.abdulahad.blooddonationapp.onboarding.NavigationActivity;
import com.abdulahad.blooddonationapp.utils.SessionManager;

public class Splash extends AppCompatActivity {

    private boolean hasMovedForward = false; // যেন checkFlow একবারই চলে

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.red));
        window.setNavigationBarColor(getResources().getColor(R.color.red));

        if (isConnected()) {
            new Handler(Looper.getMainLooper()).postDelayed(this::checkFlow, 2000);
        } else {
            showNoInternetDialog();
        }
    }

    private void checkFlow() {
        if (hasMovedForward) return; // একবারই যেন যায়
        hasMovedForward = true;

        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        boolean hasSeenOnboarding = prefs.getBoolean("hasSeenOnboarding", false);

        SessionManager session = new SessionManager(this);
        boolean isLoggedIn = session.isLoggedIn();
        boolean isAdmin = session.isAdmin();

        Intent intent;
        if (!hasSeenOnboarding) {
            intent = new Intent(Splash.this, NavigationActivity.class);
        } else if (!isLoggedIn) {
            intent = new Intent(Splash.this, Login.class);
        } else if (isAdmin) {
            intent = new Intent(Splash.this, AdminActivity.class);
        } else {
            intent = new Intent(Splash.this, MainActivity.class);
        }

        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(internetCheckReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        // Resume হলে আবার ইন্টারনেট চেক করে checkFlow কল করবে
        if (isConnected() && !hasMovedForward) {
            checkFlow();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(internetCheckReceiver);
    }

    public boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }

    public void showNoInternetDialog() {
        new AlertDialog.Builder(this)
                .setTitle("ইন্টারনেট সংযোগ নেই")
                .setMessage("অনুগ্রহ করে আপনার ইন্টারনেট সংযোগ চালু করুন।")
                .setCancelable(false)
                .setPositiveButton("Wi-Fi চালু করুন", (dialog, which) -> {
                    startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                })
                .setNegativeButton("বাহির হোন", (dialog, which) -> {
                    finishAffinity(); // অ্যাপ বন্ধ করে দেবে
                })
                .show();
    }

    BroadcastReceiver internetCheckReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!isConnected()) {
                showNoInternetDialog();
            }
        }
    };
}
