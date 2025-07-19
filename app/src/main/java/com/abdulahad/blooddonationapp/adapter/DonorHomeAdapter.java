package com.abdulahad.blooddonationapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.abdulahad.blooddonationapp.R;
import com.abdulahad.blooddonationapp.model.Donor;
import com.abdulahad.blooddonationapp.utils.FavoriteDbHelper;
import com.abdulahad.blooddonationapp.utils.Urls;
import com.bumptech.glide.Glide;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class DonorHomeAdapter extends RecyclerView.Adapter<DonorHomeAdapter.ViewHolder> {

    private Context context;
    private List<Donor> donorList;
    private String imageBaseUrl = Urls.DOMAIN_URL; // 🔁 adjust as needed

    public DonorHomeAdapter(Context context, List<Donor> donorList) {
        this.context = context;
        this.donorList = donorList;
    }

    @NonNull
    @Override
    public DonorHomeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_donor_home, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DonorHomeAdapter.ViewHolder holder, int position) {
        Donor donor = donorList.get(position);

        holder.txtName.setText("নাম: " + donor.getName());
        holder.txtPhone.setText("ফোন: " + donor.getPhone());
        holder.txtBloodGroup.setText("রক্ত গ্রুপ: " + donor.getBloodGroup());
        holder.txtDistrict.setText("জেলা: " + donor.getDistrict());
        holder.txtAddress.setText("ঠিকানা: " + donor.getAddress());

        String imageUrl = imageBaseUrl + donor.getProfileImage();
        Glide.with(context)
                .load(imageUrl)
                .placeholder(R.drawable.user)
                .into(holder.imgProfile);


        holder.btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = donor.getPhone(); // ফোন নাম্বার
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + phone));
                context.startActivity(intent);
            }
        });


        FavoriteDbHelper dbHelper = new FavoriteDbHelper(context);
        String phone = donor.getPhone();

        // ফেভারিট থাকলে আইকন সেট করুন
        if (dbHelper.isFavorite(phone)) {
            holder.btnFavorite.setImageResource(R.drawable.baseline_favorite_24);
        } else {
            holder.btnFavorite.setImageResource(R.drawable.baseline_favorite_border_24);
        }

        holder.btnFavorite.setOnClickListener(v -> {
            if (dbHelper.isFavorite(phone)) {
                dbHelper.removeFavorite(phone);
                holder.btnFavorite.setImageResource(R.drawable.baseline_favorite_border_24);
                Toast.makeText(context, "Favorite থেকে বাদ দেওয়া হয়েছে", Toast.LENGTH_SHORT).show();
            } else {
                dbHelper.addFavorite(phone);
                holder.btnFavorite.setImageResource(R.drawable.baseline_favorite_24);
                Toast.makeText(context, "Favorite এ যুক্ত হয়েছে", Toast.LENGTH_SHORT).show();
            }
        });








    }

    @Override
    public int getItemCount() {
        return donorList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView imgProfile;
        TextView txtName, txtPhone, txtBloodGroup, txtDistrict, txtAddress;
        ImageView btnCall,btnFavorite;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgProfile = itemView.findViewById(R.id.imgProfile);
            txtName = itemView.findViewById(R.id.txtName);
            txtPhone = itemView.findViewById(R.id.txtPhone);
            txtBloodGroup = itemView.findViewById(R.id.txtBloodGroup);
            txtDistrict = itemView.findViewById(R.id.txtDistrict);
            txtAddress = itemView.findViewById(R.id.txtAddress);
            btnCall = itemView.findViewById(R.id.btnCall);
            btnFavorite = itemView.findViewById(R.id.btnFavorite);
        }
    }
}
