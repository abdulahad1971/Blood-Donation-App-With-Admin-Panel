package com.abdulahad.blooddonationapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.abdulahad.blooddonationapp.R;
import com.abdulahad.blooddonationapp.model.Donor;
import com.abdulahad.blooddonationapp.utils.FavoriteDbHelper;
import com.abdulahad.blooddonationapp.utils.Urls;
import com.bumptech.glide.Glide;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;

public class DonorAdapter extends RecyclerView.Adapter<DonorAdapter.ViewHolder> {

    private Context context;
    private List<Donor> donorList;
    private String imageBaseUrl = Urls.DOMAIN_URL;
    private OnFavoriteChangeListener favoriteChangeListener;

    public DonorAdapter(Context context, List<Donor> donorList, OnFavoriteChangeListener listener) {
        this.context = context;
        this.donorList = donorList;
        this.favoriteChangeListener = listener;
    }

    @NonNull
    @Override
    public DonorAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_donor, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DonorAdapter.ViewHolder holder, int position) {
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

        holder.btnCall.setOnClickListener(v -> {
            String phone = donor.getPhone();
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + phone));
            context.startActivity(intent);
        });

        FavoriteDbHelper dbHelper = new FavoriteDbHelper(context);
        String phone = donor.getPhone();

        if (dbHelper.isFavorite(phone)) {
            holder.btnFavorite.setImageResource(R.drawable.baseline_favorite_24);
        } else {
            holder.btnFavorite.setImageResource(R.drawable.baseline_favorite_border_24);
        }

        holder.btnFavorite.setOnClickListener(v -> {
            if (dbHelper.isFavorite(phone)) {
                dbHelper.removeFavorite(phone);
                donorList.remove(position);
                notifyItemRemoved(position);
                Toast.makeText(context, "Favorite থেকে বাদ দেওয়া হয়েছে", Toast.LENGTH_SHORT).show();

                if (favoriteChangeListener != null) {
                    favoriteChangeListener.onFavoriteChanged();
                }

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
        ImageView btnCall, btnFavorite;

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

    // ✅ Interface to notify fragment
    public interface OnFavoriteChangeListener {
        void onFavoriteChanged();
    }



    public void filterList(List<Donor> filteredList) {
        this.donorList = filteredList;
        notifyDataSetChanged();
    }

}
