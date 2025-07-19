package com.abdulahad.blooddonationapp.admin.screen;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.abdulahad.blooddonationapp.R;
import com.abdulahad.blooddonationapp.admin.adapter.UserAdapter;
import com.abdulahad.blooddonationapp.admin.model.UserModel;
import com.abdulahad.blooddonationapp.utils.Urls;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class User extends AppCompatActivity {

    RecyclerView recyclerView;
    UserAdapter adapter;
    List<UserModel> userList = new ArrayList<>();
    ProgressDialog progressDialog; // 🔄 ProgressDialog

    TextView textTotalUsers;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        textTotalUsers = findViewById(R.id.textTotalUsers);

        // Status bar color
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(getResources().getColor(R.color.red));

        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        recyclerView = findViewById(R.id.recyclerViewUsers);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new UserAdapter(this, userList);
        recyclerView.setAdapter(adapter);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("লোড হচ্ছে...");
        progressDialog.setCancelable(false);

        fetchUsers();
    }

    private void fetchUsers() {
        progressDialog.show(); // ⏳ Show progress

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, Urls.ALL_USER, null,
                response -> {
                    progressDialog.dismiss(); // ✅ Hide progress

                    try {
                        if (response.getBoolean("success")) {
                            JSONArray usersArray = response.getJSONArray("users");
                            userList.clear();

                            for (int i = 0; i < usersArray.length(); i++) {
                                JSONObject obj = usersArray.getJSONObject(i);

                                UserModel user = new UserModel(
                                        obj.getString("id"),
                                        obj.getString("name"),
                                        obj.getString("phone"),
                                        obj.getString("blood_group"),
                                        obj.getString("district"),
                                        obj.getString("address"),
                                        obj.getString("profile_image"),
                                        obj.getString("is_donor")
                                );
                                userList.add(user);
                            }

                            adapter.notifyDataSetChanged();

                            // ✅ মোট ইউজার সংখ্যা দেখাও
                            textTotalUsers.setText("ব্যবহারকারী: " + userList.size() + " জন");
                        } else {
                            Toast.makeText(User.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(User.this, "ডেটা লোডিংয়ে সমস্যা", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    progressDialog.dismiss(); // ❌ Hide on error
                    error.printStackTrace();
                    Toast.makeText(User.this, "নেটওয়ার্ক ত্রুটি", Toast.LENGTH_SHORT).show();
                });

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }
}
