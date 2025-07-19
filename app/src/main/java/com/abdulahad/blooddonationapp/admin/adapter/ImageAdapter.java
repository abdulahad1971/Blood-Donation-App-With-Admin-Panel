package com.abdulahad.blooddonationapp.admin.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.abdulahad.blooddonationapp.R;
import com.abdulahad.blooddonationapp.admin.model.ImageModel;
import com.abdulahad.blooddonationapp.utils.Urls;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {

    private Context context;
    private List<ImageModel> imageList;


    public ImageAdapter(Context context, List<ImageModel> imageList) {
        this.context = context;
        this.imageList = imageList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_image_slider, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        String imageUrl = Urls.DOMAIN_URL +"admin/"+ imageList.get(position).getImageUrl();

//        https://fahimhasan.xyz/blood_app/admin/uploads/slider/slider_1752679966433.jpg

        Glide.with(context)
                .load(imageUrl)
                .placeholder(R.drawable.blood) // placeholder image
                .into(holder.imageView);


        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new AlertDialog.Builder(context)
                        .setTitle("Delete Image")
                        .setMessage("আপনি কি নিশ্চিতভাবে এই ছবিটি ডিলিট করতে চান?")
                        .setPositiveButton("হ্যাঁ", (dialog, which) -> {

                            String imageUrl = imageList.get(position).getImageUrl(); // Full URL
                            String filename = imageUrl.substring(imageUrl.lastIndexOf("/") + 1); // e.g. slider_123.jpg

                            StringRequest request = new StringRequest(Request.Method.POST, Urls.SLIDER_IMAGE_DELETE,
                                    response -> {
                                        if (response.contains("success")) {
                                            Toast.makeText(context, "✅ ডিলিট সফল", Toast.LENGTH_SHORT).show();

                                            // Remove item from list
                                            imageList.remove(position);
                                            notifyItemRemoved(position);
                                            notifyItemRangeChanged(position, imageList.size());

                                        } else {
                                            Toast.makeText(context, "❌ সার্ভার রেসপন্স ভুল", Toast.LENGTH_SHORT).show();
                                        }
                                    },
                                    error -> {
                                        Toast.makeText(context, "⛔ ডিলিট এরর: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                                        error.printStackTrace();
                                    }) {
                                @Override
                                protected Map<String, String> getParams() {
                                    Map<String, String> params = new HashMap<>();
                                    params.put("filename", filename);
                                    return params;
                                }
                            };

                            Volley.newRequestQueue(context).add(request);

                        })
                        .setNegativeButton("না", null)
                        .show();

            }
        });



    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        Button btnDelete;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageViewSlider);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
