package com.abdulahad.blooddonationapp.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.abdulahad.blooddonationapp.R;
import com.abdulahad.blooddonationapp.screen.DonorRequest;
import com.abdulahad.blooddonationapp.screen.Login;
import com.abdulahad.blooddonationapp.screen.ProfileActivity;
import com.abdulahad.blooddonationapp.screen.UserBloodRequestPost;
import com.abdulahad.blooddonationapp.utils.SessionManager;
import com.abdulahad.blooddonationapp.utils.Urls;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;

public class Profile extends Fragment {

    CardView logout, profile;
    CircleImageView imgProfile;
    TextView txtName, txtPhone;
    LinearLayout blood_request, blood_request_show;
    ProgressBar progressBar;  // ProgressBar variable

    SessionManager sessionManager;
    String userName, userPhone, userImage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        logout = view.findViewById(R.id.logout);
        profile = view.findViewById(R.id.profile);
        imgProfile = view.findViewById(R.id.profile_image);
        txtName = view.findViewById(R.id.txtName);
        txtPhone = view.findViewById(R.id.txtPhone);
        blood_request = view.findViewById(R.id.blood_request);
        blood_request_show = view.findViewById(R.id.blood_request_show);
        progressBar = view.findViewById(R.id.progressBar);  // Init progressBar

        blood_request.setOnClickListener(v -> startActivity(new Intent(getActivity(), DonorRequest.class)));

        sessionManager = new SessionManager(getActivity());

        // Load profile
        String phone = sessionManager.getUserPhone();
        Log.d("SESSION_PHONE", "Phone: " + phone);

        if (phone != null) {
            loadProfileData(phone);
        } else {
            Toast.makeText(getActivity(), "Phone missing", Toast.LENGTH_SHORT).show();
        }

        logout.setOnClickListener(v -> {
            SessionManager session = new SessionManager(getActivity());
            session.logout();
            startActivity(new Intent(getActivity(), Login.class));
        });

        profile.setOnClickListener(v -> startActivity(new Intent(getActivity(), ProfileActivity.class)));

        blood_request_show.setOnClickListener(v -> startActivity(new Intent(getActivity(), UserBloodRequestPost.class)));

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        String phone = sessionManager.getUserPhone();
        if (phone != null) {
            loadProfileData(phone);
        }
    }

    private void loadProfileData(String phone) {
        String url = Urls.DOMAIN_URL + "get_profile.php?phone=" + phone;

        progressBar.setVisibility(View.VISIBLE); // Show progress before request

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    progressBar.setVisibility(View.GONE); // Hide progress after response
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getString("status").equals("success")) {
                            JSONObject data = jsonObject.getJSONObject("data");

                            userName = data.getString("name");
                            userPhone = phone;
                            userImage = data.getString("profile_image");

                            txtName.setText("নাম : " + userName);
                            txtPhone.setText("ফোন নাম্বার : " + userPhone);

                            String imageUrl = Urls.DOMAIN_URL + userImage;
                            Glide.with(getActivity()).load(imageUrl)
                                    .placeholder(R.drawable.user)
                                    .into(imgProfile);

                        } else {
                            Toast.makeText(getActivity(), "User not found", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), "JSON parsing error", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    progressBar.setVisibility(View.GONE); // Hide progress on error
                    Toast.makeText(getActivity(), "Server Error", Toast.LENGTH_SHORT).show();
                });

        RequestQueue queue = Volley.newRequestQueue(getActivity());
        queue.add(request);
    }
}
