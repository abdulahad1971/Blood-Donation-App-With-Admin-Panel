package com.abdulahad.blooddonationapp.admin.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.abdulahad.blooddonationapp.R;
import com.abdulahad.blooddonationapp.admin.model.UserModel;
import com.abdulahad.blooddonationapp.utils.Urls;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private Context context;
    private List<UserModel> userList;



    public UserAdapter(Context context, List<UserModel> userList) {
        this.context = context;
        this.userList = userList;


    }

    @NonNull
    @Override
    public UserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.all_user_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        UserModel user = userList.get(position);

        holder.textName.setText(user.getName());
        holder.textBloodGroup.setText("রক্ত গ্রুপ: " + user.getBlood_group());
        holder.textDistrict.setText("জেলা: " + user.getDistrict());
        holder.textPhone.setText("ফোন: " + user.getPhone());
        holder.textAddress.setText("ঠিকানা: " + user.getAddress());

        if (user.getIs_donor().equals("1")) {
            holder.textDonorStatus.setText("রক্তদাতা: হ্যাঁ");
        } else {
            holder.textDonorStatus.setText("রক্তদাতা: না");
        }



        String imageUrl = Urls.DOMAIN_URL + user.getProfile_image();
        // পুরো URL বানানো

        Glide.with(context)
                .load(imageUrl)
                .placeholder(R.drawable.user)
                .error(R.drawable.user)
                .into(holder.imageProfile);



        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("ডিলিট নিশ্চিত করুন");
                builder.setMessage("আপনি কি নিশ্চিতভাবে ইউজারটি ডিলিট করতে চান?");
                builder.setPositiveButton("হ্যাঁ", (dialog, which) -> {

                    StringRequest stringRequest = new StringRequest(Request.Method.POST, Urls.DELETE_USER,
                            response -> {
                                try {
                                    JSONObject obj = new JSONObject(response);
                                    if (obj.getBoolean("success")) {
                                        Toast.makeText(context, "✅ ইউজার ডিলিট হয়েছে", Toast.LENGTH_SHORT).show();

                                        // রিসাইক্লারভিউ থেকে ইউজার রিমুভ করা
                                        userList.remove(position);
                                        notifyItemRemoved(position);
                                        notifyItemRangeChanged(position, userList.size());
                                    } else {
                                        Toast.makeText(context, "⛔ ডিলিট হয়নি", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            },
                            error -> {
                                Toast.makeText(context, "⛔ Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                            }) {
                        @Override
                        protected Map<String, String> getParams() {
                            Map<String, String> params = new HashMap<>();
                            params.put("id", user.getId());  // user ID পাঠানো
                            return params;
                        }
                    };

                    Volley.newRequestQueue(context).add(stringRequest);

                });

                builder.setNegativeButton("না", null);
                builder.show();
            }
        });





    }

    @Override
    public int getItemCount() {
        return userList.size();
    }





    public static class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView imageProfile;
        TextView textName, textBloodGroup, textDistrict, textPhone,textAddress,textDonorStatus;
        ImageView btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageProfile = itemView.findViewById(R.id.imageProfile);
            textName = itemView.findViewById(R.id.textName);
            textBloodGroup = itemView.findViewById(R.id.textBloodGroup);
            textDistrict = itemView.findViewById(R.id.textDistrict);
            textPhone = itemView.findViewById(R.id.textPhone);
            textAddress = itemView.findViewById(R.id.textAddress);
            textDonorStatus = itemView.findViewById(R.id.textDonorStatus);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
