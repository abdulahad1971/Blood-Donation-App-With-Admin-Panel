package com.abdulahad.blooddonationapp.screen;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.abdulahad.blooddonationapp.R;
import com.abdulahad.blooddonationapp.utils.SessionManager;
import com.abdulahad.blooddonationapp.utils.Urls;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    CircleImageView imgProfile;
    TextView txtName, txtPhone, txtBloodGroup, txtDistrict, txtAddress;
    ProgressBar progressBar;

    String userName, userPhone, userBloodGroup, userDistrict, userAddress, userImage;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.red));
        window.setNavigationBarColor(getResources().getColor(R.color.red));


        sessionManager = new SessionManager(this);

        // Views
        txtName = findViewById(R.id.txtName);
        txtPhone = findViewById(R.id.txtPhone);
        txtBloodGroup = findViewById(R.id.txtBloodGroup);
        txtDistrict = findViewById(R.id.txtDistrict);
        txtAddress = findViewById(R.id.txtAddress);
        imgProfile = findViewById(R.id.imgProfile);
        progressBar = findViewById(R.id.profileProgressBar); // New ProgressBar

        // Back button
        findViewById(R.id.backButton).setOnClickListener(v -> finish());

        // Edit button
        findViewById(R.id.edit).setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, ProfileEdit.class);
            intent.putExtra("name", userName);
            intent.putExtra("phone", userPhone);
            intent.putExtra("blood", userBloodGroup);
            intent.putExtra("district", userDistrict);
            intent.putExtra("address", userAddress);
            intent.putExtra("image", userImage);
            startActivity(intent);
        });

        // Logout
        findViewById(R.id.logout).setOnClickListener(v -> {
            sessionManager.logout();
            Intent intent = new Intent(ProfileActivity.this, Login.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });

        // Change password
        findViewById(R.id.change_password).setOnClickListener(v -> showChangePasswordDialog());

        // Load profile
        String phone = sessionManager.getUserPhone();
        if (phone != null) {
            loadProfileData(phone);
        } else {
            Toast.makeText(this, "Phone missing", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        String phone = sessionManager.getUserPhone();
        if (phone != null) {
            loadProfileData(phone);
        }
    }

    private void loadProfileData(String phone) {
        progressBar.setVisibility(View.VISIBLE);

        String url = Urls.DOMAIN_URL + "get_profile.php?phone=" + phone;

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    progressBar.setVisibility(View.GONE);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getString("status").equals("success")) {
                            JSONObject data = jsonObject.getJSONObject("data");

                            userName = data.getString("name");
                            userBloodGroup = data.getString("blood_group");
                            userDistrict = data.getString("district");
                            userAddress = data.getString("address");
                            userImage = data.getString("profile_image");
                            userPhone = phone;

                            txtName.setText("নাম : " + userName);
                            txtPhone.setText("ফোন নাম্বার : " + userPhone);
                            txtBloodGroup.setText("রক্ত গ্রুপ: " + userBloodGroup);
                            txtDistrict.setText("জেলা: " + userDistrict);
                            txtAddress.setText("ঠিকানা: " + userAddress);

                            String imageUrl = Urls.DOMAIN_URL + userImage;
                            Glide.with(this).load(imageUrl)
                                    .placeholder(R.drawable.user)
                                    .into(imgProfile);
                        } else {
                            Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(this, "JSON error", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Server Error", Toast.LENGTH_SHORT).show();
                });

        Volley.newRequestQueue(this).add(request);
    }

    private void showChangePasswordDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_change_password, null);

        EditText edtOldPassword = dialogView.findViewById(R.id.edtOldPassword);
        EditText edtNewPassword = dialogView.findViewById(R.id.edtNewPassword);
        EditText edtConfirmPassword = dialogView.findViewById(R.id.edtConfirmPassword);

        androidx.appcompat.app.AlertDialog dialog = new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("পাসওয়ার্ড পরিবর্তন করুন")
                .setView(dialogView)
                .setPositiveButton("আপডেট", null)
                .setNegativeButton("বাতিল", (d, i) -> d.dismiss())
                .create();

        dialog.setOnShowListener(dlg -> {
            dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                String oldPass = edtOldPassword.getText().toString().trim();
                String newPass = edtNewPassword.getText().toString().trim();
                String confirmPass = edtConfirmPassword.getText().toString().trim();

                if (oldPass.isEmpty()) {
                    edtOldPassword.setError("পুরানো পাসওয়ার্ড দিন");
                    return;
                }
                if (newPass.isEmpty()) {
                    edtNewPassword.setError("নতুন পাসওয়ার্ড দিন");
                    return;
                }
                if (!newPass.equals(confirmPass)) {
                    edtConfirmPassword.setError("পাসওয়ার্ড মিলছে না");
                    return;
                }

                updatePasswordOnServer(oldPass, newPass, dialog);
            });
        });

        dialog.show();
    }

    private void updatePasswordOnServer(String oldPassword, String newPassword, androidx.appcompat.app.AlertDialog dialog) {
        String phone = sessionManager.getUserPhone();
        if (phone == null) {
            Toast.makeText(this, "ইউজার সেশন নেই", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        StringRequest request = new StringRequest(Request.Method.POST, Urls.URL_CHANGE_PASSWORD,
                response -> {
                    progressBar.setVisibility(View.GONE);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getBoolean("status")) {
                            Toast.makeText(this, "পাসওয়ার্ড সফলভাবে পরিবর্তিত হয়েছে", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        } else {
                            Toast.makeText(this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(this, "সার্ভার রেসপন্স ভুল", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "ত্রুটি: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("phone", phone);
                params.put("old_password", oldPassword);
                params.put("new_password", newPassword);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }
}
