package com.abdulahad.blooddonationapp.screen;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.abdulahad.blooddonationapp.R;
import com.abdulahad.blooddonationapp.utils.SessionManager;
import com.abdulahad.blooddonationapp.utils.Urls;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.github.dhaval2404.imagepicker.ImagePicker;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

// ... [same imports]

public class ProfileEdit extends AppCompatActivity {

    private CircleImageView profileImage;
    private ImageView editIcon;
    private Uri selectedImageUri;

    private EditText editName, editPhone, editAddress;
    private Spinner spinnerBloodGroup, spinnerDistrict;
    private CheckBox checkDonor;
    private SessionManager sessionManager;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile_edit);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Status Bar color
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(getResources().getColor(R.color.red));
        window.setNavigationBarColor(getResources().getColor(R.color.red));


        sessionManager = new SessionManager(this);

        // View bindings
        profileImage = findViewById(R.id.profile_image);
        editIcon = findViewById(R.id.edit_icon);
        editName = findViewById(R.id.editName);
        editPhone = findViewById(R.id.editPhone);
        editAddress = findViewById(R.id.editAddress);
        spinnerBloodGroup = findViewById(R.id.spinnerBloodGroup);
        spinnerDistrict = findViewById(R.id.spinnerDistrict);
        checkDonor = findViewById(R.id.checkDonor);

        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        // Spinner Data
        List<String> bloodGroups = Arrays.asList(
                "এ পজিটিভ (A+)", "এ নেগেটিভ (A-)", "বি পজিটিভ (B+)", "বি নেগেটিভ (B-)",
                "এবি পজিটিভ (AB+)", "এবি নেগেটিভ (AB-)", "ও পজিটিভ (O+)", "ও নেগেটিভ (O-)"
        );
        ArrayAdapter<String> bloodAdapter = new ArrayAdapter<>(this, R.layout.custom_spinner_blood_item, R.id.spinner_text, bloodGroups);
        spinnerBloodGroup.setAdapter(bloodAdapter);

        List<String> districts = Arrays.asList(
                "ঢাকা", "গাজীপুর", "নারায়ণগঞ্জ", "টাঙ্গাইল", "কিশোরগঞ্জ", "মানিকগঞ্জ", "ময়মনসিংহ", "জামালপুর",
                "নেত্রকোনা", "শেরপুর", "নরসিংদী", "রাজবাড়ী", "ফরিদপুর", "গোপালগঞ্জ", "মাদারীপুর", "শরীয়তপুর",
                "খুলনা", "যশোর", "সাতক্ষীরা", "ঝিনাইদহ", "চুয়াডাঙ্গা", "মেহেরপুর", "নড়াইল", "বাগেরহাট",
                "কুষ্টিয়া", "বরিশাল", "পটুয়াখালী", "ভোলা", "বরগুনা", "ঝালকাঠি", "পিরোজপুর",
                "চট্টগ্রাম", "কক্সবাজার", "রাঙ্গামাটি", "খাগড়াছড়ি", "বান্দরবান", "ব্রাহ্মণবাড়িয়া", "কুমিল্লা", "ফেনী",
                "নোয়াখালী", "লক্ষ্মীপুর", "চাঁদপুর", "চাঁপাইনবাবগঞ্জ", "রাজশাহী", "নাটোর", "নওগাঁ", "পাবনা",
                "সিরাজগঞ্জ", "বগুড়া", "জয়পুরহাট", "দিনাজপুর", "ঠাকুরগাঁও", "পঞ্চগড়", "রংপুর", "কুড়িগ্রাম",
                "লালমনিরহাট", "নীলফামারী", "গাইবান্ধা", "সিলেট", "মৌলভীবাজার", "সুনামগঞ্জ", "হবিগঞ্জ", "মাগুরা"
        );
        ArrayAdapter<String> districtAdapter = new ArrayAdapter<>(this, R.layout.custom_spinner_district_item, R.id.spinner_text, districts);
        spinnerDistrict.setAdapter(districtAdapter);

        // Load data from intent
        Intent intent = getIntent();
        if (intent != null) {
            String name = intent.getStringExtra("name");
            String phone = intent.getStringExtra("phone");
            String blood = intent.getStringExtra("blood");
            String district = intent.getStringExtra("district");
            String address = intent.getStringExtra("address");
            String image = intent.getStringExtra("image");

            editName.setText(name);
            editPhone.setText(phone);
            editAddress.setText(address);

            // Spinner set
            if (blood != null) {
                int pos = bloodAdapter.getPosition(blood);
                spinnerBloodGroup.setSelection(pos);
            }
            if (district != null) {
                int pos = districtAdapter.getPosition(district);
                spinnerDistrict.setSelection(pos);
            }

            // Load profile image
            if (image != null && !image.isEmpty()) {
                String imageUrl = Urls.DOMAIN_URL + image;
                Glide.with(this).load(imageUrl).placeholder(R.drawable.user).into(profileImage);
            }
        }

        // Pick new image
        editIcon.setOnClickListener(v -> {
            ImagePicker.with(ProfileEdit.this)
                    .cropSquare()
                    .compress(512)
                    .maxResultSize(512, 512)
                    .start();
        });



        // Update Button
        findViewById(R.id.btnSignup).setOnClickListener(v -> updateProfile());
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            selectedImageUri = data.getData();
            profileImage.setImageURI(selectedImageUri);
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show();
        }
    }






    private void updateProfile() {
        String name = editName.getText().toString().trim();
        String phone = editPhone.getText().toString().trim();
        String bloodGroup = spinnerBloodGroup.getSelectedItem().toString();
        String district = spinnerDistrict.getSelectedItem().toString();
        String address = editAddress.getText().toString().trim();
        boolean isDonor = checkDonor.isChecked();

        if (TextUtils.isEmpty(name)) {
            editName.setError("নাম দিতে হবে");
            return;
        }
        if (TextUtils.isEmpty(phone)) {
            editPhone.setError("ফোন নম্বর দিতে হবে");
            return;
        }
        if (TextUtils.isEmpty(address)) {
            editAddress.setError("ঠিকানা দিতে হবে");
            return;
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("আপডেট করা হচ্ছে...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        String imageBase64 = null;
        if (selectedImageUri != null) {
            try {
                InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int read;
                while ((read = inputStream.read(buffer)) != -1) {
                    baos.write(buffer, 0, read);
                }
                byte[] imageBytes = baos.toByteArray();
                imageBase64 = android.util.Base64.encodeToString(imageBytes, android.util.Base64.DEFAULT);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        String finalImageBase64 = imageBase64;

        StringRequest request = new StringRequest(Request.Method.POST, Urls.URL_UPDATE_PROFILE, response -> {
            progressDialog.dismiss();
            Toast.makeText(ProfileEdit.this, "আপডেট সফল হয়েছে", Toast.LENGTH_SHORT).show();
            finish();
        }, error -> {
            progressDialog.dismiss();
            Log.e("VolleyError", "Error: ", error);
            Toast.makeText(ProfileEdit.this, "আপডেট ব্যর্থ: " + error.getMessage(), Toast.LENGTH_SHORT).show();
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("name", name);
                params.put("phone", phone);
                params.put("blood_group", bloodGroup);
                params.put("district", district);
                params.put("address", address);
                params.put("is_donor", isDonor ? "1" : "0");
                if (finalImageBase64 != null) {
                    params.put("profile_image", finalImageBase64);
                }
                return params;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(
                15000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }
}
