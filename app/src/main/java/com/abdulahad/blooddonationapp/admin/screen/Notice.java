package com.abdulahad.blooddonationapp.admin.screen;

import static java.security.AccessController.getContext;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.abdulahad.blooddonationapp.R;
import com.abdulahad.blooddonationapp.screen.Login;
import com.abdulahad.blooddonationapp.utils.Urls;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Notice extends AppCompatActivity {

    private EditText editMarqueeText;
    private Button btnSubmitText;
    TextView textMarquee;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);

        editMarqueeText = findViewById(R.id.editMarqueeText);
        btnSubmitText = findViewById(R.id.btnSubmitText);
        textMarquee = findViewById(R.id.textMarquee);

        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());


        getMarqueText();




        btnSubmitText.setOnClickListener(v -> {
            String text = editMarqueeText.getText().toString().trim();

            if (text.isEmpty()) {
                Toast.makeText(this, "অনুগ্রহ করে কিছু লিখুন", Toast.LENGTH_SHORT).show();
            } else {
                sendMarqueeToServer(text);
            }
        });
    }

    private void getMarqueText() {

        StringRequest request = new StringRequest(Request.Method.GET, Urls.GET_MARQUEE_TEXT,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        if (obj.getBoolean("success")) {
                            String text = obj.getString("text");
                            textMarquee.setText(text); // TextView তে দেখাও
                        } else {
                            textMarquee.setText("No notice found.");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    error.printStackTrace();
                }
        );
        Volley.newRequestQueue(Notice.this).add(request);

    }

    private void sendMarqueeToServer(String text) {
        StringRequest request = new StringRequest(Request.Method.POST, Urls.MARQUE_TEXT_INSERT,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        boolean success = obj.getBoolean("success");
                        String message = obj.getString("message");

                        Toast.makeText(Notice.this, message, Toast.LENGTH_SHORT).show();
                        getMarqueText();

                        if (success) {
//                            textMarquee.setText(text); // ইনপুট টেক্সটই আবার সেট করো (বা নতুন টেক্সট চাইলে response থেকে parse করো)
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(Notice.this, "JSONException: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(Notice.this, "Volley Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("text", text); // PHP $_POST['text']
                return params;
            }
        };

        Volley.newRequestQueue(Notice.this).add(request);
    }









}
