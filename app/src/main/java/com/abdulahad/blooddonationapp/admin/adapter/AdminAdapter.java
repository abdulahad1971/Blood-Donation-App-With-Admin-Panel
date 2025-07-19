package com.abdulahad.blooddonationapp.admin.adapter;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.abdulahad.blooddonationapp.R;
import com.abdulahad.blooddonationapp.admin.model.AdminItem;
import com.abdulahad.blooddonationapp.admin.screen.AllPost;
import com.abdulahad.blooddonationapp.admin.screen.Image;
import com.abdulahad.blooddonationapp.admin.screen.Notice;
import com.abdulahad.blooddonationapp.admin.screen.User;
import com.abdulahad.blooddonationapp.screen.Login;
import com.abdulahad.blooddonationapp.utils.SessionManager;

import java.util.List;

public class AdminAdapter extends RecyclerView.Adapter<AdminAdapter.ViewHolder> {

    private Context context;
    private List<AdminItem> itemList;

    public AdminAdapter(Context context, List<AdminItem> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @Override
    public AdminAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_admin_dashboard, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AdminAdapter.ViewHolder holder, int position) {
        AdminItem item = itemList.get(position);
        holder.title.setText(item.getTitle());
        holder.icon.setImageResource(item.getIcon());

        holder.cardView.setOnClickListener(v -> {
            // এখানে item অনুযায়ী কাজ করবে (position check করে)
            switch (position) {
                case 0:

                    Intent intent = new Intent(v.getContext(), Notice.class);
                    v.getContext().startActivity(intent);

                    break;
                case 1:
                    Intent image = new Intent(v.getContext(), Image.class);
                    v.getContext().startActivity(image);
                    break;
                case 2:
                    Intent user = new Intent(v.getContext(), User.class);
                    v.getContext().startActivity(user);
                    break;
                       case 3:
                    Intent post = new Intent(v.getContext(), AllPost.class);
                    v.getContext().startActivity(post);
                    break;


                    case 4:
                        SessionManager session = new SessionManager(context);
                        session.logout();
                        v.getContext().startActivity(new Intent(context, Login.class));
                    break;
                // আরো case চাইলে যোগ করো
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView title;
        CardView cardView;

        public ViewHolder(View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.icon);
            title = itemView.findViewById(R.id.title);
            cardView = (CardView) itemView;
        }
    }
}

