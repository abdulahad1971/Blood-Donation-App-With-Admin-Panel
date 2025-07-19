package com.abdulahad.blooddonationapp.admin.screen;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.abdulahad.blooddonationapp.R;
import com.abdulahad.blooddonationapp.admin.adapter.ImageAdapter;
import com.abdulahad.blooddonationapp.admin.model.ImageModel;
import com.abdulahad.blooddonationapp.utils.Urls;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.dhaval2404.imagepicker.ImagePicker;

import org.json.JSONArray;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Image extends AppCompatActivity {

    private ImageView profileImage;
    private ImageView editIcon;
    private Uri selectedImageUri;
    private AppCompatButton submit;

    private RecyclerView recyclerView;
    private ImageAdapter adapter;
    private List<ImageModel> imageList = new ArrayList<>();

    ProgressDialog progressDialog; // 🔄 ProgressDialog

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_image);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("লোড হচ্ছে...");
        progressDialog.setCancelable(false);

        // Status Bar color
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(getResources().getColor(R.color.red));

        recyclerView = findViewById(R.id.recyclerViewSlider);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        adapter = new ImageAdapter(this, imageList);
        recyclerView.setAdapter(adapter);

        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        loadImages();

        // Views init
        submit = findViewById(R.id.submit);
        profileImage = findViewById(R.id.profile_image);
        editIcon = findViewById(R.id.edit_icon);

        // Image picker button
        editIcon.setOnClickListener(v -> {
            ImagePicker.with(Image.this)
                    .crop(16f, 9f) // 📷 Cover ratio
                    .compress(512) // Compress to ~512 KB
                    .maxResultSize(1280, 720)
                    .start();
        });

        // Submit button
        submit.setOnClickListener(v -> {
            if (selectedImageUri != null) {
                uploadImageToServer(selectedImageUri);
            } else {
                Toast.makeText(Image.this, "ছবি সিলেক্ট করুন", Toast.LENGTH_SHORT).show();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });




    }

    @Override
    protected void onResume() {
        super.onResume();
        loadImages();
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



    private void uploadImageToServer(Uri uri) {
        try {
            // Convert to Bitmap
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, 800, 400, true);

            // Compress
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
            byte[] imageBytes = baos.toByteArray();

            // Encode to Base64
            String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);

            // Upload
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Urls.SLIDER_IMAGE_UPLOAD,
                    response -> {
                        if (response.contains("success")) {
                            Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
                            profileImage.setImageResource(R.drawable.blood);
                            Log.d("eeddddddddddf",response.toString());
                        } else {
                            Toast.makeText(this, "⚠️ সার্ভার রেসপন্স ভুল", Toast.LENGTH_SHORT).show();
                        }
                    },
                    error -> {
                        Toast.makeText(this, "⛔ ত্রুটি: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        error.printStackTrace();
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("image", encodedImage);
                    params.put("filename", "slider_" + System.currentTimeMillis() + ".jpg");
                    return params;
                }
            };



            Volley.newRequestQueue(this).add(stringRequest);

        } catch (Exception e) {
            Toast.makeText(this, "⛔ ত্রুটি: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }


    private void loadImages() {
        progressDialog.show(); // ⏳ Show progress
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, Urls.SHOW_IMAGE, null,
                response -> {
                    progressDialog.dismiss(); // ✅ Hide progress
                    try {
                        boolean success = response.getBoolean("success");
                        if (success) {
                            JSONArray imagesArray = response.getJSONArray("images");
                            imageList.clear();

                            for (int i = 0; i < imagesArray.length(); i++) {
                                String imageUrl = imagesArray.getString(i);
                                imageList.add(new ImageModel(imageUrl));
                            }

                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(this, "No images found", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Parsing error", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    progressDialog.dismiss(); // ❌ Hide on error
                    error.printStackTrace();
                    Toast.makeText(this, "Network error", Toast.LENGTH_SHORT).show();
                });

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }
}