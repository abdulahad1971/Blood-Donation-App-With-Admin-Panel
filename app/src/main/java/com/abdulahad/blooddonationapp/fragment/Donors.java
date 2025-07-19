package com.abdulahad.blooddonationapp.fragment;

import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.abdulahad.blooddonationapp.R;
import com.abdulahad.blooddonationapp.adapter.DonorAdapter;
import com.abdulahad.blooddonationapp.model.Donor;
import com.abdulahad.blooddonationapp.utils.Urls;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Donors extends Fragment {

    private RecyclerView recyclerView;
    private DonorAdapter adapter;
    private List<Donor> donorList;
    private SwipeRefreshLayout swipeRefreshLayout; // SwipeRefreshLayout ডিক্লেয়ার

    private static final String URL = Urls.DOMAIN_URL + "get_donors.php";

    ProgressBar progressBar;
    private SearchView searchView;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_donors, container, false);


        searchView = view.findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterDonors(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterDonors(newText);
                return true;
            }
        });






        // SwipeRefreshLayout ইনিশিয়ালাইজ
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setProgressViewOffset(true, 100, 200);
        // SwipeRefreshLayout এর রঙ কাস্টম করো (multiple colors দিলে একটার পর একটা ঘুরবে)
        swipeRefreshLayout.setColorSchemeResources(
                R.color.red,
                R.color.teal_700,
                R.color.purple_700
        );





        progressBar = view.findViewById(R.id.progressBar);


        recyclerView = view.findViewById(R.id.recyclerViewDonors);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        donorList = new ArrayList<>();
        adapter = new DonorAdapter(getContext(), donorList, () -> {
            // আপনি চাইলে ফেভারিটে ক্লিকের পর কিছু কাজ করতে পারেন
            // এই ক্ষেত্রে কিছু করা না লাগলেও রেখে দিন future use এর জন্য
        });
        recyclerView.setAdapter(adapter);

        // ডোনার লোড
        loadDonors();

        // Swipe করলে নতুন করে ডাটা লোড হবে
        swipeRefreshLayout.setOnRefreshListener(this::loadDonors);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadDonors();
    }

    private void loadDonors() {
        // রিফ্রেশ আইকন দেখাও
        swipeRefreshLayout.setRefreshing(true);
        progressBar.setVisibility(View.VISIBLE);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, URL, null,
                response -> {
                    progressBar.setVisibility(View.GONE);
                    try {
                        boolean success = response.getBoolean("success");
                        if (success) {
                            JSONArray donorsArray = response.getJSONArray("donors");
                            donorList.clear();

                            for (int i = 0; i < donorsArray.length(); i++) {
                                JSONObject obj = donorsArray.getJSONObject(i);
                                String name = obj.getString("name");
                                String phone = obj.getString("phone");
                                String bloodGroup = obj.getString("blood_group");
                                String district = obj.getString("district");
                                String address = obj.getString("address");
                                String imageUrl = obj.getString("profile_image");

                                donorList.add(new Donor(name, phone, bloodGroup, district, address, imageUrl));
                            }

                            adapter.notifyDataSetChanged();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    // রিফ্রেশ বন্ধ করো
                    swipeRefreshLayout.setRefreshing(false);
                },
                error -> {
                    error.printStackTrace();
                    progressBar.setVisibility(View.GONE);
                    swipeRefreshLayout.setRefreshing(false); // এরর হলেও রিফ্রেশ বন্ধ
                });

        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(request);
    }




    private void filterDonors(String keyword) {
        List<Donor> filteredList = new ArrayList<>();

        for (Donor donor : donorList) {
            if (donor.getName().toLowerCase().contains(keyword.toLowerCase()) ||
                    donor.getDistrict().toLowerCase().contains(keyword.toLowerCase()) ||
                    donor.getBloodGroup().toLowerCase().contains(keyword.toLowerCase())) {
                filteredList.add(donor);
            }
        }

        adapter.filterList(filteredList);
    }

}
