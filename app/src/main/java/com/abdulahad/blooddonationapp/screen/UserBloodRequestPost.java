package com.abdulahad.blooddonationapp.screen;

import static android.app.PendingIntent.getActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.abdulahad.blooddonationapp.R;
import com.abdulahad.blooddonationapp.adapter.BloodPostUserRequestAdapter;
import com.abdulahad.blooddonationapp.adapter.BloodRequestAdapter;
import com.abdulahad.blooddonationapp.model.BloodRequest;
import com.abdulahad.blooddonationapp.utils.SessionManager;
import com.abdulahad.blooddonationapp.utils.Urls;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class UserBloodRequestPost extends AppCompatActivity {

    private RecyclerView recyclerView;
    private BloodPostUserRequestAdapter adapter;
    private List<BloodRequest> requestList;


    SessionManager sessionManager;

    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_blood_request_post);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerView = findViewById(R.id.recyclerMyRequests);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        requestList = new ArrayList<>();
        adapter = new BloodPostUserRequestAdapter(this, requestList);
        recyclerView.setAdapter(adapter);

        progressBar = findViewById(R.id.progressBar);

        sessionManager = new SessionManager(this);

        loadMyRequests();
        // Back button
        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());
        // Status bar color
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.red));
        window.setNavigationBarColor(getResources().getColor(R.color.red));




    }

    private void loadMyRequests() {
        String userId = sessionManager.getUserPhone();
        String url = Urls.GET_DONORS_REQUEST_USER + "?user_id=" + userId;

       progressBar.setVisibility(View.VISIBLE);

        requestList.clear();


        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    progressBar.setVisibility(View.GONE);
                    try {
                        JSONObject obj = new JSONObject(response);
                        if (obj.getBoolean("success")) {
                            JSONArray array = obj.getJSONArray("requests");

                            for (int i = 0; i < array.length(); i++) {
                                JSONObject req = array.getJSONObject(i);

                                int id = req.getInt("id");
                                int userIdDb = req.getInt("user_id");
                                String name = req.getString("name");
                                String phone = req.getString("phone");
                                String bloodGroup = req.getString("blood_group");
                                String district = req.getString("district");
                                String address = req.getString("address");
                                String problem = req.getString("problem");
                                String image = req.getString("image");
                                String time = req.getString("request_time");


                                requestList.add(new BloodRequest(id, userIdDb, name, phone, bloodGroup, district, address, problem, image, time));
                            }

                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(this, "কোনো অনুরোধ পাওয়া যায়নি।", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "JSON error", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "ত্রুটি: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                });

        Volley.newRequestQueue(this).add(request);
    }
}
