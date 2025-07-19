package com.abdulahad.blooddonationapp.fragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.abdulahad.blooddonationapp.R;
import com.abdulahad.blooddonationapp.adapter.DonorAdapter;
import com.abdulahad.blooddonationapp.model.Donor;
import com.abdulahad.blooddonationapp.utils.FavoriteDbHelper;
import com.abdulahad.blooddonationapp.utils.Urls;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Favourite extends Fragment {

    private RecyclerView recyclerView;
    private TextView emptyView;
    private DonorAdapter adapter;
    private List<Donor> allDonors = new ArrayList<>();
    private List<Donor> favoriteDonors = new ArrayList<>();
    private FavoriteDbHelper dbHelper;

    ProgressBar progressBar;

    private static final String URL = Urls.DOMAIN_URL + "get_donors.php";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favourite, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewFavorite);
        emptyView = view.findViewById(R.id.emptyView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        progressBar = view.findViewById(R.id.progressBar);


        dbHelper = new FavoriteDbHelper(getContext());


        loadDonors();

        return view;
    }

    private void loadDonors() {
        progressBar.setVisibility(View.VISIBLE);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, URL, null,
                response -> {
            progressBar.setVisibility(View.GONE);
                    try {
                        boolean success = response.getBoolean("success");
                        if (success) {
                            JSONArray donorsArray = response.getJSONArray("donors");
                            allDonors.clear();

                            for (int i = 0; i < donorsArray.length(); i++) {
                                JSONObject obj = donorsArray.getJSONObject(i);
                                String name = obj.getString("name");
                                String phone = obj.getString("phone");
                                String bloodGroup = obj.getString("blood_group");
                                String district = obj.getString("district");
                                String address = obj.getString("address");
                                String imageUrl = obj.getString("profile_image");

                                allDonors.add(new Donor(name, phone, bloodGroup, district, address, imageUrl));
                            }

                            filterFavorites();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                error -> {
            progressBar.setVisibility(View.GONE);
                    error.printStackTrace();
                });

        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(request);
    }

    private void filterFavorites() {
        favoriteDonors.clear();
        for (Donor donor : allDonors) {
            if (dbHelper.isFavorite(donor.getPhone())) {
                favoriteDonors.add(donor);
            }
        }

        adapter = new DonorAdapter(getContext(), favoriteDonors, new DonorAdapter.OnFavoriteChangeListener() {
            @Override
            public void onFavoriteChanged() {
                filterFavorites();
            }
        });

        recyclerView.setAdapter(adapter);

        if (favoriteDonors.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
    }
}
