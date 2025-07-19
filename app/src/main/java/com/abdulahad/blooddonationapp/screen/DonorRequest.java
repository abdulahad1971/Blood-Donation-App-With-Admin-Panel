package com.abdulahad.blooddonationapp.screen;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.abdulahad.blooddonationapp.R;
import com.abdulahad.blooddonationapp.utils.SessionManager;
import com.abdulahad.blooddonationapp.utils.Urls;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.dhaval2404.imagepicker.ImagePicker;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DonorRequest extends AppCompatActivity {

    private ImageView profileImage, editIcon;
    private Uri selectedImageUri;
    private EditText editName, editPhone, editAddress, editplm;
    private Spinner spinnerBloodGroup, spinnerDistrict;
    private SessionManager sessionManager;
    private Button btnSubmit;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_donor_request);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        sessionManager = new SessionManager(this);

        profileImage = findViewById(R.id.profile_image);
        editIcon = findViewById(R.id.edit_icon);
        editName = findViewById(R.id.editName);
        editPhone = findViewById(R.id.editPhone);
        editAddress = findViewById(R.id.editAddress);
        spinnerBloodGroup = findViewById(R.id.spinnerBloodGroup);
        spinnerDistrict = findViewById(R.id.spinnerDistrict);
        editplm = findViewById(R.id.editplm);
        btnSubmit = findViewById(R.id.btn);

        // ProgressDialog Initialize
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("দয়া করে অপেক্ষা করুন...");
        progressDialog.setCancelable(false);

        // Status Bar color
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(getResources().getColor(R.color.red));
        window.setNavigationBarColor(getResources().getColor(R.color.red));


        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        // Spinner Data
        List<String> bloodGroups = Arrays.asList("এ পজিটিভ (A+)", "এ নেগেটিভ (A-)", "বি পজিটিভ (B+)", "বি নেগেটিভ (B-)",
                "এবি পজিটিভ (AB+)", "এবি নেগেটিভ (AB-)", "ও পজিটিভ (O+)", "ও নেগেটিভ (O-)");
        ArrayAdapter<String> bloodAdapter = new ArrayAdapter<>(this, R.layout.custom_spinner_blood_item, R.id.spinner_text, bloodGroups);
        spinnerBloodGroup.setAdapter(bloodAdapter);

        List<String> districts = Arrays.asList("ঢাকা", "গাজীপুর", "নারায়ণগঞ্জ", "টাঙ্গাইল", "কিশোরগঞ্জ", "মানিকগঞ্জ", "ময়মনসিংহ", "জামালপুর",
                "নেত্রকোনা", "শেরপুর", "নরসিংদী", "রাজবাড়ী", "ফরিদপুর", "গোপালগঞ্জ", "মাদারীপুর", "শরীয়তপুর",
                "খুলনা", "যশোর", "সাতক্ষীরা", "ঝিনাইদহ", "চুয়াডাঙ্গা", "মেহেরপুর", "নড়াইল", "বাগেরহাট",
                "কুষ্টিয়া", "বরিশাল", "পটুয়াখালী", "ভোলা", "বরগুনা", "ঝালকাঠি", "পিরোজপুর",
                "চট্টগ্রাম", "কক্সবাজার", "রাঙ্গামাটি", "খাগড়াছড়ি", "বান্দরবান", "ব্রাহ্মণবাড়িয়া", "কুমিল্লা", "ফেনী",
                "নোয়াখালী", "লক্ষ্মীপুর", "চাঁদপুর", "চাঁপাইনবাবগঞ্জ", "রাজশাহী", "নাটোর", "নওগাঁ", "পাবনা",
                "সিরাজগঞ্জ", "বগুড়া", "জয়পুরহাট", "দিনাজপুর", "ঠাকুরগাঁও", "পঞ্চগড়", "রংপুর", "কুড়িগ্রাম",
                "লালমনিরহাট", "নীলফামারী", "গাইবান্ধা", "সিলেট", "মৌলভীবাজার", "সুনামগঞ্জ", "হবিগঞ্জ", "মাগুরা");
        ArrayAdapter<String> districtAdapter = new ArrayAdapter<>(this, R.layout.custom_spinner_district_item, R.id.spinner_text, districts);
        spinnerDistrict.setAdapter(districtAdapter);

        // Image picker button
        editIcon.setOnClickListener(v -> {
            ImagePicker.with(DonorRequest.this)
                    .crop(16f, 9f) // 📷 Cover ratio
                    .compress(512) // Compress to ~512 KB
                    .maxResultSize(1280, 720)
                    .start();
        });

        btnSubmit.setOnClickListener(v -> submitRequest());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            if (selectedImageUri != null && profileImage != null) {
                profileImage.setImageURI(selectedImageUri);
            }
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show();
        }
    }


    private String encodeImageToBase64(Uri imageUri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
            byte[] imageBytes = baos.toByteArray();
            return android.util.Base64.encodeToString(imageBytes, android.util.Base64.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void submitRequest() {
        String name = editName.getText() != null ? editName.getText().toString().trim() : "";
        String phone = editPhone.getText() != null ? editPhone.getText().toString().trim() : "";
        String address = editAddress.getText() != null ? editAddress.getText().toString().trim() : "";
        String problem = editplm.getText() != null ? editplm.getText().toString().trim() : "";
        String bloodGroup = spinnerBloodGroup.getSelectedItem() != null ? spinnerBloodGroup.getSelectedItem().toString() : "";
        String district = spinnerDistrict.getSelectedItem() != null ? spinnerDistrict.getSelectedItem().toString() : "";

        if (name.isEmpty() || phone.isEmpty() || address.isEmpty() || problem.isEmpty() || selectedImageUri == null) {
            Toast.makeText(this, "সব ফিল্ড পূরণ করুন ও ছবি দিন", Toast.LENGTH_SHORT).show();
            return;
        }

        String encodedImage = encodeImageToBase64(selectedImageUri);
        if (encodedImage == null) {
            Toast.makeText(this, "ছবি এনকোড করতে সমস্যা হয়েছে", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = sessionManager.getUserPhone();
        if (userId == null || userId.isEmpty()) {
            Toast.makeText(this, "ব্যবহারকারী শনাক্ত করা যায়নি। অনুগ্রহ করে পুনরায় লগইন করুন।", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Urls.ADD_DONORS_REQUEST,
                response -> {
                    if (progressDialog.isShowing()) progressDialog.dismiss();

                    try {
                        JSONObject json = new JSONObject(response);
                        if (json.optBoolean("success", false)) {

                            AlertDialog.Builder builder = new AlertDialog.Builder(this);
                            builder.setTitle("সফল");
                            builder.setMessage("রক্তের অনুরোধ সফলভাবে পাঠানো হয়েছে");
                            builder.setIcon(R.drawable.checkmark); // আপনার drawable ফোল্ডারে success আইকন থাকলে
                            builder.setPositiveButton("ঠিক আছে", (dialog, which) -> {
                                dialog.dismiss();
                                finish();  // রিকোয়েস্ট সফল হলে এই activity থেকে বের হয়ে যাবে
                            });
                            builder.setCancelable(false);
                            builder.show();

                        } else {
                            Toast.makeText(this, json.optString("message", "অনুরোধ পাঠাতে সমস্যা হয়েছে"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "JSON পার্সিং ত্রুটি হয়েছে", Toast.LENGTH_SHORT).show();
                        Log.e("DonorRequest", "JSONException: " + e.getMessage());
                    }
                },
                error -> {
                    if (progressDialog.isShowing()) progressDialog.dismiss();

                    NetworkResponse networkResponse = error.networkResponse;
                    if (networkResponse != null) {
                        String body = new String(networkResponse.data, StandardCharsets.UTF_8);
                        Log.e("ServerError", "Status code: " + networkResponse.statusCode + ", Body: " + body);
                        Toast.makeText(this, "সার্ভার থেকে ত্রুটি পাওয়া গেছে: " + networkResponse.statusCode, Toast.LENGTH_LONG).show();
                    } else {
                        Log.e("ServerError", "No network response");
                        Toast.makeText(this, "নেটওয়ার্ক সমস্যার কারণে অনুরোধ পাঠানো যায়নি", Toast.LENGTH_LONG).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", userId);
                params.put("name", name);
                params.put("phone", phone);
                params.put("blood_group", bloodGroup);
                params.put("district", district);
                params.put("address", address);
                params.put("problem", problem);
                params.put("image", encodedImage);
                return params;
            }
        };

        // Set retry policy (optional) - increases timeout to 30s to avoid timeout errors on slow networks
        stringRequest.setRetryPolicy(new com.android.volley.DefaultRetryPolicy(
                30000,
                com.android.volley.DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                com.android.volley.DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        Volley.newRequestQueue(this).add(stringRequest);
    }
}
