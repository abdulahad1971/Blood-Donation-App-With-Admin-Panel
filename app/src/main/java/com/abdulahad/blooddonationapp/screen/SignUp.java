package com.abdulahad.blooddonationapp.screen;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.abdulahad.blooddonationapp.R;
import com.abdulahad.blooddonationapp.utils.Urls;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SignUp extends AppCompatActivity {

    EditText editName, editPhone, editPassword, editAddress;
    CheckBox checkDonor;
    AppCompatButton btnSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextView login_btn;
        login_btn = findViewById(R.id.login_btn);
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUp.this, Login.class));
            }
        });

        // .....................Status bar color ..........................................
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(getResources().getColor(R.color.red));
        window.setNavigationBarColor(getResources().getColor(R.color.red));

//        window.setNavigationBarColor(getResources().getColor(R.color.red));
        // .....................Status bar color ..........................................

        Spinner spinnerBloodGroup = findViewById(R.id.spinnerBloodGroup);
        List<String> items = Arrays.asList(
                "এ পজিটিভ (A+)",
                "এ নেগেটিভ (A-)",
                "বি পজিটিভ (B+)",
                "বি নেগেটিভ (B-)",
                "এবি পজিটিভ (AB+)",
                "এবি নেগেটিভ (AB-)",
                "ও পজিটিভ (O+)",
                "ও নেগেটিভ (O-)"
        );

        ArrayAdapter<String> bloodAdapter = new ArrayAdapter<String>(this, R.layout.custom_spinner_blood_item, R.id.spinner_text, items) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);

                ImageView leftIcon = view.findViewById(R.id.spinner_left_icon);
                ImageView downIcon = view.findViewById(R.id.spinner_down_icon);
                TextView text = view.findViewById(R.id.spinner_text);

                text.setText("রক্তের গ্রুপ: " + getItem(position));

                // এখানে তুমি আইকন বা টেক্সট কাস্টমাইজ করতে পারো যদি দরকার হয়



                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                // Dropdown এর item জন্য সহজ TextView ব্যবহার করতে পারো
                TextView tv = new TextView(getContext());
                tv.setText(getItem(position));
                tv.setPadding(16, 16, 16, 16);
                return tv;
            }
        };
        spinnerBloodGroup.setAdapter(bloodAdapter);


        Spinner spinnerDistrict = findViewById(R.id.spinnerDistrict);
        List<String> districtItem = Arrays.asList(
                "ঢাকা", "গাজীপুর", "নারায়ণগঞ্জ", "টাঙ্গাইল", "কিশোরগঞ্জ", "মানিকগঞ্জ", "ময়মনসিংহ", "জামালপুর",
                "নেত্রকোনা", "শেরপুর", "নরসিংদী", "রাজবাড়ী", "ফরিদপুর", "গোপালগঞ্জ", "মাদারীপুর", "শরীয়তপুর",
                "খুলনা", "যশোর", "সাতক্ষীরা", "ঝিনাইদহ", "চুয়াডাঙ্গা", "মেহেরপুর", "নড়াইল", "বাগেরহাট",
                "কুষ্টিয়া", "বরিশাল", "পটুয়াখালী", "ভোলা", "বরগুনা", "ঝালকাঠি", "পিরোজপুর",
                "চট্টগ্রাম", "কক্সবাজার", "রাঙ্গামাটি", "খাগড়াছড়ি", "বান্দরবান", "ব্রাহ্মণবাড়িয়া", "কুমিল্লা", "ফেনী",
                "নোয়াখালী", "লক্ষ্মীপুর", "চাঁদপুর", "চাঁপাইনবাবগঞ্জ", "রাজশাহী", "নাটোর", "নওগাঁ", "পাবনা",
                "সিরাজগঞ্জ", "বগুড়া", "জয়পুরহাট", "দিনাজপুর", "ঠাকুরগাঁও", "পঞ্চগড়", "রংপুর", "কুড়িগ্রাম",
                "লালমনিরহাট", "নীলফামারী", "গাইবান্ধা", "সিলেট", "মৌলভীবাজার", "সুনামগঞ্জ", "হবিগঞ্জ", "মাগুরা"
        );


        ArrayAdapter<String> districtAdapter = new ArrayAdapter<String>(this, R.layout.custom_spinner_district_item, R.id.spinner_text, districtItem) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);

                ImageView leftIcon = view.findViewById(R.id.spinner_left_icon);
                ImageView downIcon = view.findViewById(R.id.spinner_down_icon);
                TextView text = view.findViewById(R.id.spinner_text);

                text.setText("জেলা : " + getItem(position));

                // এখানে তুমি আইকন বা টেক্সট কাস্টমাইজ করতে পারো যদি দরকার হয়


                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                // Dropdown এর item জন্য সহজ TextView ব্যবহার করতে পারো
                TextView tv = new TextView(getContext());
                tv.setText(getItem(position));
                tv.setPadding(16, 16, 16, 16);
                return tv;
            }
        };
        spinnerDistrict.setAdapter(districtAdapter);



        // Bind Views
        editName = findViewById(R.id.editName);
        editPhone = findViewById(R.id.editPhone);
        editPassword = findViewById(R.id.editPassword);
        editAddress = findViewById(R.id.editAddress);
        checkDonor = findViewById(R.id.checkDonor);
        btnSignup = findViewById(R.id.btnSignup);

        // Spinner binding আগেই করা আছে (spinnerBloodGroup, spinnerDistrict)

        btnSignup.setOnClickListener(v -> {
            String name = editName.getText().toString().trim();
            String phone = editPhone.getText().toString().trim();
            String password = editPassword.getText().toString().trim();
            String address = editAddress.getText().toString().trim();
            String bloodGroup = spinnerBloodGroup.getSelectedItem().toString();
            String district = spinnerDistrict.getSelectedItem().toString();
            boolean isDonor = checkDonor.isChecked();

            if (name.isEmpty() || phone.isEmpty() || password.isEmpty() || address.isEmpty()) {
                Toast.makeText(SignUp.this, "সব ঘর পূরণ করুন", Toast.LENGTH_SHORT).show();
            } else {
                registerUser(name, phone, password, address, bloodGroup, district, isDonor);
            }
        });





    }




    private void registerUser(String name, String phone, String password, String address, String bloodGroup, String district, boolean isDonor) {

        ProgressDialog progressDialog = new ProgressDialog(SignUp.this);
        progressDialog.setMessage("দয়া করে অপেক্ষা করুন...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        String URL = Urls.DOMAIN_URL + "signup.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                response -> {
                    progressDialog.dismiss();  // ✅ ডায়ালগ বন্ধ করুন

                    try {
                        JSONObject obj = new JSONObject(response);
                        boolean success = obj.getBoolean("success");
                        String message = obj.getString("message");

                        Toast.makeText(SignUp.this, message, Toast.LENGTH_SHORT).show();

                        if (success) {
                            startActivity(new Intent(SignUp.this, Login.class));
                            finish();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    progressDialog.dismiss();  // ✅ ডায়ালগ বন্ধ করুন
                    Log.d("truti",error.toString());
                    // ✅ AlertDialog for Error Handling
                    AlertDialog.Builder builder = new AlertDialog.Builder(SignUp.this);
                    builder.setTitle("ত্রুটি");
                    builder.setMessage("সার্ভার সংযোগে সমস্যা হয়েছে!\n\nError: " + error.getMessage());
                    builder.setPositiveButton("ঠিক আছে", (dialog, which) -> dialog.dismiss());
                    builder.show();
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("name", name);
                params.put("phone", phone);
                params.put("password", password);
                params.put("address", address);
                params.put("blood_group", bloodGroup);
                params.put("district", district);
                if (isDonor) {
                    params.put("is_donor", "1");
                }
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(SignUp.this);
        requestQueue.add(stringRequest);
    }


}