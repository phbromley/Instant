package hu.ait.android.instant.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import hu.ait.android.instant.R;
import hu.ait.android.instant.data.User;

public class FollowAdapter extends RecyclerView.Adapter<FollowAdapter.ViewHolder> {
    private Context context;
    private List<User> userList;
    private List<String> userKeys;
    private String uId;
    private DatabaseReference usersRef;

    public FollowAdapter(Context context, String uId, List<User> accounts) {
        this.context = context;
        this.uId = uId;

        userList = accounts;
        userKeys = new ArrayList<String>();

        usersRef = FirebaseDatabase.getInstance().getReference();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_user, parent, false);
        return new ViewHolder(row);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        User userInfo = userList.get(position);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference();

        DatabaseReference usersRef = ref.child("users");

        // TODO
        // CHANGE THIS SO ITS THE USER
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        holder.tvLilName.setText(userInfo.getFullName());
        holder.tvLilDisplayName.setText(user.getDisplayName());

        if (user.getPhotoUrl() != null) {
            Glide.with(context).load(user.getPhotoUrl()).into(holder.ivLilAvatar);
        } else {
            holder.ivLilAvatar.setImageResource(R.drawable.instant_logo);
        }

        // TODO
        // ALSO CHANGE AND CHECK TO SEE IF FOLLOWING OR NOT
        holder.btnChangeFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        // TODO
        // ALLOW THIS TO OPEN THIS PERSON'S PROFILE
        holder.ivLilAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public void addUser(User user, String key) {
        userList.add(user);
        userKeys.add(key);

        notifyDataSetChanged();
    }

    public void removeUser(int index) {
        usersRef.child(userKeys.get(index)).removeValue();

        userList.remove(index);
        userKeys.remove(index);
        notifyItemRemoved(index);
    }

    public void removeUserByKey(String key) {
        int index = userKeys.indexOf(key);
        if (index != -1) {
            userList.remove(index);
            userKeys.remove(index);
            notifyItemRemoved(index);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvLilName;
        public TextView tvLilDisplayName;
        public Button btnChangeFollow;
        public ImageView ivLilAvatar;

        public ViewHolder(View itemView) {
            super(itemView);

            tvLilName = itemView.findViewById(R.id.tvLilName);
            tvLilDisplayName = itemView.findViewById(R.id.tvLilDisplayName);
            btnChangeFollow = itemView.findViewById(R.id.btnFollow);
            ivLilAvatar = itemView.findViewById(R.id.ivLilAvatar);
        }
    }
}
