package com.abdulahad.blooddonationapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.abdulahad.blooddonationapp.R;
import com.abdulahad.blooddonationapp.model.BloodRequest;
import com.abdulahad.blooddonationapp.utils.Urls;
import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BloodRequestAdapter extends RecyclerView.Adapter<BloodRequestAdapter.RequestViewHolder> {

    private Context context;
    private List<BloodRequest> requestList;

    public BloodRequestAdapter(Context context, List<BloodRequest> requestList) {
        this.context = context;
        this.requestList = requestList;
    }

    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_blood_request, parent, false);
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

    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }

    public static class RequestViewHolder extends RecyclerView.ViewHolder {
        TextView txtName, txtGroup, txtDistrict, txtProblem,txtTime,txtAddress;
        ImageView imageView,callIcon;

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

}
