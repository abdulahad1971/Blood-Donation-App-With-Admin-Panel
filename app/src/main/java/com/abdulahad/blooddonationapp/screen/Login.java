package com.abdulahad.blooddonationapp.screen;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.abdulahad.blooddonationapp.MainActivity;
import com.abdulahad.blooddonationapp.R;
import com.abdulahad.blooddonationapp.admin.AdminActivity;
import com.abdulahad.blooddonationapp.utils.SessionManager;
import com.abdulahad.blooddonationapp.utils.Urls;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity {

    EditText editPhone, editPassword;
    AppCompatButton btnLogin;
    TextView signUpBtn;

    SessionManager session; // সেশন ম্যানেজার ডিক্লেয়ার

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
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
        window.setNavigationBarColor(getResources().getColor(R.color.red));


        editPhone = findViewById(R.id.editPhone);
        editPassword = findViewById(R.id.editPassword);
        btnLogin = findViewById(R.id.btnSignup);  // XML এ btnSignup নামে
        signUpBtn = findViewById(R.id.signUp_btn);

        session = new SessionManager(this);  // SessionManager ইনিশিয়ালাইজ

        btnLogin.setOnClickListener(v -> {
            String phone = editPhone.getText().toString().trim();
            String password = editPassword.getText().toString().trim();

            if (phone.isEmpty() || password.isEmpty()) {
                Toast.makeText(Login.this, "সব ঘর পূরণ করুন", Toast.LENGTH_SHORT).show();
            } else {
                loginUser(phone, password);
            }
        });

        signUpBtn.setOnClickListener(v -> {
            startActivity(new Intent(Login.this, SignUp.class));
            finish();
        });
    }


    private void loginUser(String phone, String password) {
        ProgressDialog progressDialog = new ProgressDialog(Login.this);
        progressDialog.setMessage("দয়া করে অপেক্ষা করুন...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        // ✅ Admin check (shortcut)
        if (phone.equals("11111111111") && password.equals("123456")) {
            progressDialog.dismiss(); // Admin হলে ডায়ালগ বন্ধ
            Toast.makeText(Login.this, "Admin হিসেবে লগইন সফল", Toast.LENGTH_SHORT).show();
            String adminName = "Admin";

            session.createLoginSession(phone, adminName);

            Intent intent = new Intent(Login.this, AdminActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

        String URL = Urls.DOMAIN_URL + "login.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                response -> {
                    progressDialog.dismiss(); // ✅ Login response এ dismiss
                    try {
                        JSONObject obj = new JSONObject(response);
                        boolean success = obj.getBoolean("success");
                        String message = obj.getString("message");

                        Toast.makeText(Login.this, message, Toast.LENGTH_SHORT).show();

                        if (success) {
                            String userName = obj.optString("name", "User");
                            String userPhone = obj.optString("phone", phone);

                            session.createLoginSession(userPhone, userName);

                            Intent intent = new Intent(Login.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(Login.this, "JSON পার্সিং এরর", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    progressDialog.dismiss(); // ✅ Error হলেও dismiss
                    AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
                    builder.setTitle("ত্রুটি");
                    builder.setMessage("সার্ভার সংযোগে সমস্যা হয়েছে!\n\nError: " + error.getMessage());
                    builder.setPositiveButton("ঠিক আছে", (dialog, which) -> dialog.dismiss());
                    builder.show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("phone", phone);
                params.put("password", password);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(Login.this);
        requestQueue.add(stringRequest);
    }






}
