package com.abdulahad.blooddonationapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.abdulahad.blooddonationapp.fragment.Donors;
import com.abdulahad.blooddonationapp.fragment.Favourite;
import com.abdulahad.blooddonationapp.fragment.Home;
import com.abdulahad.blooddonationapp.fragment.Profile;
import com.abdulahad.blooddonationapp.screen.Login;
import com.abdulahad.blooddonationapp.utils.SessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ImageView menu;
    View header;

    BottomNavigationView bottomnavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);

        SessionManager session = new SessionManager(this);
        if (!session.isLoggedIn()) {
            startActivity(new Intent(this, Login.class));
            finish();
            return;  // এই লাইনটি গুরুত্বপূর্ণ, নিচের কোড রান হবে না।
        }
        setContentView(R.layout.activity_main);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.red));
        window.setNavigationBarColor(getResources().getColor(R.color.red));

        if (!isConnected()) {
            showNoInternetDialog(); // যদি ইন্টারনেট না থাকে তাহলে AlertDialog দেখাবে
        }


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, v.getPaddingTop(), systemBars.right, v.getPaddingBottom());
            return insets;
        });

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        menu = findViewById(R.id.menu);

        header = navigationView.getHeaderView(0);

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)){
                    drawerLayout.closeDrawer(GravityCompat.START);
                }

                else {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.home) {
                    Toast.makeText(MainActivity.this, "হোম", Toast.LENGTH_SHORT).show();
                }

                else if (id == R.id.facebook_page) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/banglatechworld0"));
                    startActivity(intent);
                }

                else if (id == R.id.facebook_group) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/groups/424938587352795"));
                    startActivity(intent);
                }

                else if (id == R.id.youtube) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/@BanglaTechWorld0"));
                    startActivity(intent);
                }

                else if (id == R.id.developer) {
                    Toast.makeText(MainActivity.this, "ডেভেলপার: আব্দুল আহাদ", Toast.LENGTH_SHORT).show();
                }

                else if (id == R.id.share) {
                    String shareBody = "Hey, check out this Blood Donation App: "
                            + "http://play.google.com/store/apps/details?id=" + getPackageName();
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                    startActivity(Intent.createChooser(shareIntent, "Share via"));
                }

                else if (id == R.id.star) {
                    try {
                        Intent rateIntent = new Intent(Intent.ACTION_VIEW,
                                Uri.parse("market://details?id=" + getPackageName()));
                        startActivity(rateIntent);
                    } catch (android.content.ActivityNotFoundException e) {
                        Intent rateIntent = new Intent(Intent.ACTION_VIEW,
                                Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName()));
                        startActivity(rateIntent);
                    }
                }

                else if (id == R.id.logout) {
                    finishAffinity();
                }

                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });





        //......................................................
        //.......................................................
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.farmelayout,new Home());
        fragmentTransaction.commit();

        bottomnavigation = findViewById(R.id.bottomnavigation);

        bottomnavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {


                int itemId = item.getItemId();

                if (itemId==R.id.home){
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.farmelayout,new Home());
                    fragmentTransaction.commit();

                } else if (itemId==R.id.donor) {

                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.farmelayout,new Donors());
                    fragmentTransaction.commit();

                }
                else if (itemId==R.id.favourite) {

                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.farmelayout,new Favourite());
                    fragmentTransaction.commit();

                }

                else if (itemId==R.id.person) {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.farmelayout,new Profile());
                    fragmentTransaction.commit();
                }


                return true;
            }
        });

        //......................................................
        //.......................................................







    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(internetCheckReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
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