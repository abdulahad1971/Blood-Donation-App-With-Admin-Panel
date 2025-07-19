package com.abdulahad.blooddonationapp.admin;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.abdulahad.blooddonationapp.R;
import com.abdulahad.blooddonationapp.admin.adapter.AdminAdapter;
import com.abdulahad.blooddonationapp.admin.model.AdminItem;

import java.util.ArrayList;
import java.util.List;

public class AdminActivity extends AppCompatActivity {


    RecyclerView recyclerView;
    AdminAdapter adapter;
    List<AdminItem> itemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Status bar color
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(getResources().getColor(R.color.red));

        recyclerView = findViewById(R.id.adminRecyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        itemList = new ArrayList<>();
        itemList.add(new AdminItem("বিজ্ঞপ্তি", R.drawable.text));
        itemList.add(new AdminItem("ইমেজ স্লাইডার", R.drawable.photo_gallery));
        itemList.add(new AdminItem("ব্যবহারকারী", R.drawable.user));
        itemList.add(new AdminItem("পোস্ট", R.drawable.blood));
        itemList.add(new AdminItem("লগআউট", R.drawable.baseline_logout_24));


        adapter = new AdminAdapter(this, itemList);
        recyclerView.setAdapter(adapter);
    }
}