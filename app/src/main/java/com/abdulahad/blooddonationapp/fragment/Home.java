package com.abdulahad.blooddonationapp.fragment;

import android.os.Bundle;

import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.abdulahad.blooddonationapp.R;
import com.abdulahad.blooddonationapp.adapter.BloodRequestAdapter;
import com.abdulahad.blooddonationapp.adapter.DonorHomeAdapter;
import com.abdulahad.blooddonationapp.model.BloodRequest;
import com.abdulahad.blooddonationapp.model.Donor;
import com.abdulahad.blooddonationapp.utils.Urls;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.facebook.shimmer.ShimmerFrameLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Home extends Fragment {

    private RecyclerView recyclerView;
    private DonorHomeAdapter adapter;
    private List<Donor> donorList;

    private TextView textMarquee;
    private static final String URL = Urls.DOMAIN_URL + "get_donors.php";
    private TextView totalDonor, totalDonorRequest;

    private RecyclerView donors_recyclerView;
    private BloodRequestAdapter donors_adapter;
    private List<BloodRequest> request_donorList;

    private ProgressBar progressBar, progressBar2;

    private ShimmerFrameLayout shimmerLayout;
    private NestedScrollView nestedScrollView;

    private RequestQueue requestQueue;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        shimmerLayout = view.findViewById(R.id.shimmerLayout);
        nestedScrollView = view.findViewById(R.id.nestedScrollView);

        // Shimmer চালু করো
        shimmerLayout.startShimmer();

        // ৩.৫ সেকেন্ড পর বন্ধ করো এবং কন্টেন্ট দেখাও
        new android.os.Handler().postDelayed(() -> {
            if (shimmerLayout != null && nestedScrollView != null) {
                shimmerLayout.stopShimmer();
                shimmerLayout.setVisibility(View.GONE);
                nestedScrollView.setVisibility(View.VISIBLE);
            }
        }, 3500);

        textMarquee = view.findViewById(R.id.textMarquee);
        getMarqueText();

        totalDonor = view.findViewById(R.id.totalDonor);
        totalDonorRequest = view.findViewById(R.id.totalDonorRequest);

        progressBar = view.findViewById(R.id.progressBar);
        progressBar2 = view.findViewById(R.id.progressBar2);

        ImageSlider image_slider = view.findViewById(R.id.image_slider);
        ArrayList<SlideModel> imageList = new ArrayList<>();

        requestQueue = Volley.newRequestQueue(requireContext());

        JsonObjectRequest imageRequest = new JsonObjectRequest(Request.Method.GET, Urls.SHOW_IMAGE, null,
                response -> {
                    try {
                        if (response != null) {
                            boolean success = response.optBoolean("success", false);
                            if (success) {
                                JSONArray imagesArray = response.optJSONArray("images");
                                if (imagesArray != null) {
                                    for (int i = 0; i < imagesArray.length(); i++) {
                                        String imageUrl = imagesArray.optString(i);
                                        imageList.add(new SlideModel(Urls.DOMAIN_URL + "admin/" + imageUrl, ScaleTypes.CENTER_CROP));
                                    }
                                    image_slider.setImageList(imageList);
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                error -> error.printStackTrace());

        requestQueue.add(imageRequest);

        recyclerView = view.findViewById(R.id.recyclerViewDonors);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        donorList = new ArrayList<>();
        adapter = new DonorHomeAdapter(getContext(), donorList);
        recyclerView.setAdapter(adapter);

        donors_recyclerView = view.findViewById(R.id.recyclerDonorsRequest);
        donors_recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        request_donorList = new ArrayList<>();
        donors_adapter = new BloodRequestAdapter(getActivity(), request_donorList);
        donors_recyclerView.setAdapter(donors_adapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadDonors();
        loadDonorRequests();
    }

    private void loadDonors() {
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, URL, null,
                response -> {
                    if (progressBar != null) progressBar.setVisibility(View.GONE);

                    Log.d("ResponseError", response.toString());

                    try {
                        if (response != null && response.optBoolean("success", false)) {
                            JSONArray donorsArray = response.optJSONArray("donors");
                            if (donorsArray != null) {
                                donorList.clear();

                                for (int i = 0; i < donorsArray.length(); i++) {
                                    JSONObject obj = donorsArray.optJSONObject(i);
                                    if (obj != null) {
                                        String name = obj.optString("name");
                                        String phone = obj.optString("phone");
                                        String bloodGroup = obj.optString("blood_group");
                                        String district = obj.optString("district");
                                        String address = obj.optString("address");
                                        String imageUrl = obj.optString("profile_image");

                                        donorList.add(new Donor(name, phone, bloodGroup, district, address, imageUrl));
                                    }
                                }
                                if (adapter != null)
                                    adapter.notifyDataSetChanged();

                                if (totalDonor != null)
                                    totalDonor.setText("রক্তদাতার তালিকা মোট : " + convertToBanglaNumber(donorList.size()) + " জন");
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "ডেটা প্রসেসিং সমস্যা", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    if (progressBar != null) progressBar.setVisibility(View.GONE);
                    error.printStackTrace();
                    Toast.makeText(getContext(), "ডেটা লোড করতে ব্যর্থ", Toast.LENGTH_SHORT).show();
                });

        if (requestQueue != null) requestQueue.add(request);
    }

    private void loadDonorRequests() {
        if (progressBar2 != null) progressBar2.setVisibility(View.VISIBLE);
        request_donorList.clear();

        StringRequest request = new StringRequest(Request.Method.GET, Urls.GET_DONORS_REQUEST,
                response -> {
                    if (progressBar2 != null) progressBar2.setVisibility(View.GONE);
                    try {
                        if (response != null) {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.optBoolean("success", false);
                            if (success) {
                                JSONArray array = jsonObject.optJSONArray("requests");
                                if (array != null) {
                                    for (int i = 0; i < array.length(); i++) {
                                        JSONObject obj = array.optJSONObject(i);
                                        if (obj != null) {
                                            int id = obj.optInt("id");
                                            int userID = obj.optInt("user_id");
                                            String name = obj.optString("name");
                                            String phone = obj.optString("phone");
                                            String bloodGroup = obj.optString("blood_group");
                                            String district = obj.optString("district");
                                            String address = obj.optString("address");
                                            String problem = obj.optString("problem");
                                            String image = obj.optString("image");
                                            String time = obj.optString("request_time");

                                            request_donorList.add(new BloodRequest(id, userID, name, phone, bloodGroup, district, address, problem, image, time));
                                        }
                                    }
                                }
                                if (donors_adapter != null)
                                    donors_adapter.notifyDataSetChanged();

                                if (totalDonorRequest != null)
                                    totalDonorRequest.setText("রক্তের অনুরোধ করেছেন: " + convertToBanglaNumber(request_donorList.size()) + " জন");
                            } else {
                                Toast.makeText(getActivity(), "কোনো অনুরোধ পাওয়া যায়নি।", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), "ডেটা লোড করতে সমস্যা হয়েছে", Toast.LENGTH_SHORT).show();
                        Log.d("ResponseError", response);
                    }
                },
                error -> {
                    if (progressBar2 != null) progressBar2.setVisibility(View.GONE);
                    error.printStackTrace();
                    Toast.makeText(getActivity(), "ডেটা লোড করতে ব্যর্থ", Toast.LENGTH_SHORT).show();
                });

        if (requestQueue != null) requestQueue.add(request);
    }


    private void getMarqueText() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(getActivity()); // বা context ব্যবহার করুন
        }

        StringRequest stringRequest = new StringRequest(Request.Method.GET, Urls.GET_MARQUEE_TEXT,
                response -> {
                    try {
                        Log.d("MarqueeResponse", response);
                        JSONObject jsonObject = new JSONObject(response);
                        boolean success = jsonObject.optBoolean("success", false);
                        if (success) {
                            String marqueeText = jsonObject.optString("text", "").trim();
                            if (!marqueeText.isEmpty() && textMarquee != null) {
                                textMarquee.setText(marqueeText);
                                textMarquee.setSelected(true); // ★ খুব গুরুত্বপূর্ণ
                            }
                        } else {
                            Log.e("Marquee", "Success false: " + jsonObject.optString("message"));
                        }
                    } catch (JSONException e) {
                        Log.e("MarqueeError", "JSON Exception", e);
                    }
                },
                error -> Log.e("MarqueeError",error.toString())
        );

        requestQueue.add(stringRequest);
    }






    private String convertToBanglaNumber(int number) {
        String numberStr = String.valueOf(number);
        StringBuilder banglaNumber = new StringBuilder();
        for (char c : numberStr.toCharArray()) {
            switch (c) {
                case '0': banglaNumber.append('০'); break;
                case '1': banglaNumber.append('১'); break;
                case '2': banglaNumber.append('২'); break;
                case '3': banglaNumber.append('৩'); break;
                case '4': banglaNumber.append('৪'); break;
                case '5': banglaNumber.append('৫'); break;
                case '6': banglaNumber.append('৬'); break;
                case '7': banglaNumber.append('৭'); break;
                case '8': banglaNumber.append('৮'); break;
                case '9': banglaNumber.append('৯'); break;
                default: banglaNumber.append(c);
            }
        }
        return banglaNumber.toString();
    }
}
