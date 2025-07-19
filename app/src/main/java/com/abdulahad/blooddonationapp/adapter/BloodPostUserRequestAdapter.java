package com.abdulahad.blooddonationapp.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.abdulahad.blooddonationapp.R;
import com.abdulahad.blooddonationapp.model.BloodRequest;
import com.abdulahad.blooddonationapp.utils.Urls;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class BloodPostUserRequestAdapter extends RecyclerView.Adapter<BloodPostUserRequestAdapter.RequestViewHolder> {

    private Context context;
    private List<BloodRequest> requestList;

    public BloodPostUserRequestAdapter(Context context, List<BloodRequest> requestList) {
        this.context = context;
        this.requestList = requestList;
    }

    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user_blood_request, parent, false);
        return new RequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestViewHolder holder, int position) {
        BloodRequest request = requestList.get(position);

        holder.txtName.setText(request.getName());
        holder.txtGroup.setText("রক্তের গ্রুপ: " + request.getBloodGroup());
        holder.txtDistrict.setText("জেলা: " + request.getDistrict());
        holder.txtProblem.setText("সমস্যা: " + request.getProblem());
        holder.txtTime.setText("পোস্ট করা হইসে: "+convertToBanglaDateTime(request.getRequestedAt()));
        holder.txtAddress.setText("ঠিকানা : "+request.getAddress());


        String imageUrl = Urls.DOMAIN_URL + "uploads/" + request.getImage();
        Glide.with(context).load(imageUrl).placeholder(R.drawable.b).into(holder.imageView);

        holder.callIcon.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + request.getPhone())); // যেখানে donorPhone হচ্ছে ইউজারের ফোন নাম্বার
            context.startActivity(intent);
        });

        holder.btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("ডিলিট নিশ্চিত করুন")
                    .setMessage("আপনি কি এই অনুরোধটি ডিলিট করতে চান?")
                    .setPositiveButton("হ্যাঁ", (dialog, which) -> deleteRequest(request.getId(), position))
                    .setNegativeButton("না", null)
                    .show();
        });


    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }

    public static class RequestViewHolder extends RecyclerView.ViewHolder {
        TextView txtName, txtGroup, txtDistrict, txtProblem,txtTime,txtAddress;
        ImageView imageView,callIcon;

        Button btnDelete;
        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtName);
            txtGroup = itemView.findViewById(R.id.txtGroup);
            txtDistrict = itemView.findViewById(R.id.txtDistrict);
            txtProblem = itemView.findViewById(R.id.txtProblem);
            imageView = itemView.findViewById(R.id.imageView);
            callIcon = itemView.findViewById(R.id.callIcon);
            txtTime = itemView.findViewById(R.id.txtTime);
            txtAddress = itemView.findViewById(R.id.txtAddress);

             btnDelete = itemView.findViewById(R.id.btnDelete);

        }
    }
    private String convertToBanglaDateTime(String originalTime) {
        try {
            // 1. Parse original date string
            SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
            Date date = originalFormat.parse(originalTime);

            // 2. Format date to: "d MMMM yyyy | hh:mm a" (12-ঘণ্টা ফরম্যাট)
            SimpleDateFormat banglaFormat = new SimpleDateFormat("d MMMM yyyy | hh:mm a", new Locale("bn", "BD"));
            String formatted = banglaFormat.format(date);

            // 3. ইংরেজি সংখ্যা বাংলায় রূপান্তর
            return formatted.replace("0", "০")
                    .replace("1", "১")
                    .replace("2", "২")
                    .replace("3", "৩")
                    .replace("4", "৪")
                    .replace("5", "৫")
                    .replace("6", "৬")
                    .replace("7", "৭")
                    .replace("8", "৮")
                    .replace("9", "৯")
                    .replace("AM", "পূর্বাহ্ণ")
                    .replace("PM", "অপরাহ্ণ");
        } catch (Exception e) {
            return "";
        }
    }

    private void deleteRequest(int id, int position) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("ডিলিট করা হচ্ছে...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, Urls.DELETE_BLOOD_REQUEST,
                response -> {
                    progressDialog.dismiss();
                    try {
                        JSONObject obj = new JSONObject(response);
                        if (obj.getBoolean("success")) {
                            Toast.makeText(context, "ডিলিট সফল হয়েছে", Toast.LENGTH_SHORT).show();
                            requestList.remove(position);
                            notifyItemRemoved(position);
                        } else {
                            Toast.makeText(context, obj.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(context, "JSON Error", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    progressDialog.dismiss();
                    Toast.makeText(context, "ত্রুটি: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", String.valueOf(id));
                return params;
            }
        };

        Volley.newRequestQueue(context).add(request);
    }


}
