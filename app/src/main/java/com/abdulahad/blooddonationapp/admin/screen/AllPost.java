package com.abdulahad.blooddonationapp.admin.screen;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import com.abdulahad.blooddonationapp.adapter.BloodPostUserRequestAdapter;
import com.abdulahad.blooddonationapp.adapter.BloodRequestAdapter;
import com.abdulahad.blooddonationapp.model.BloodRequest;
import com.abdulahad.blooddonationapp.utils.Urls;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AllPost extends AppCompatActivity {

    private RecyclerView donors_recyclerView;
    private BloodPostUserRequestAdapter donors_adapter;
    private List<BloodRequest> request_donorList;

    ProgressBar progressBar,progressBar2;

    TextView totalDonorRequest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_all_post);
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

        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        progressBar2 = findViewById(R.id.progressBar2);
        totalDonorRequest = findViewById(R.id.totalDonorRequest);

        donors_recyclerView = findViewById(R.id.recyclerDonorsRequest);
        donors_recyclerView.setLayoutManager(new LinearLayoutManager(AllPost.this));
        request_donorList = new ArrayList<>();
        donors_adapter = new BloodPostUserRequestAdapter(AllPost.this, request_donorList);
        donors_recyclerView.setAdapter(donors_adapter);

        loadDonorRequests();

    }

    private void loadDonorRequests() {

        progressBar2.setVisibility(View.VISIBLE);
        request_donorList.clear();

        StringRequest request = new StringRequest(Request.Method.GET, Urls.GET_DONORS_REQUEST,
                response -> {
                    progressBar2.setVisibility(View.GONE);
                    try {
                        // এখানে response থেকে JSONObject নেবে
                        JSONObject jsonObject = new JSONObject(response);
                        boolean success = jsonObject.getBoolean("success");
                        if (success) {
                            JSONArray array = jsonObject.getJSONArray("requests");  // "requests" থেকে JSONArray নাও
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject obj = array.getJSONObject(i);

                                int id = obj.getInt("id");
                                int userID = obj.getInt("user_id");
                                String name = obj.getString("name");
                                String phone = obj.getString("phone");
                                String bloodGroup = obj.getString("blood_group");
                                String district = obj.getString("district");
                                String address = obj.getString("address");
                                String problem = obj.getString("problem");
                                String image = obj.getString("image");
                                String time = obj.getString("request_time");

                                request_donorList.add(new BloodRequest(id, userID, name, phone, bloodGroup, district, address, problem, image, time));
                            }
                            if (donors_adapter != null)
                                donors_adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(AllPost.this, "কোনো অনুরোধ পাওয়া যায়নি।", Toast.LENGTH_SHORT).show();
                        }

                        totalDonorRequest.setText("রক্তের অনুরোধ করেছেন: " + convertToBanglaNumber(request_donorList.size()) + " টি");
                    } catch (JSONException e) {
                        progressBar2.setVisibility(View.GONE);
                        e.printStackTrace();
                        Toast.makeText(AllPost.this, "ডেটা লোড করতে সমস্যা হয়েছে", Toast.LENGTH_SHORT).show();
                        Log.d("ResponseError", response.toString());
                    }

                },
                error -> {

                    Toast.makeText(AllPost.this, "ত্রুটি: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                });

        Volley.newRequestQueue(AllPost.this).add(request);
    }


    private String convertToBanglaNumber(int number) {
        String engNum = String.valueOf(number);
        StringBuilder banglaNum = new StringBuilder();

        for (char c : engNum.toCharArray()) {
            switch (c) {
                case '0': banglaNum.append('০'); break;
                case '1': banglaNum.append('১'); break;
                case '2': banglaNum.append('২'); break;
                case '3': banglaNum.append('৩'); break;
                case '4': banglaNum.append('৪'); break;
                case '5': banglaNum.append('৫'); break;
                case '6': banglaNum.append('৬'); break;
                case '7': banglaNum.append('৭'); break;
                case '8': banglaNum.append('৮'); break;
                case '9': banglaNum.append('৯'); break;
                default: banglaNum.append(c); break;
            }
        }
        return banglaNum.toString();
    }






}