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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import hu.ait.android.instant.BottomNavActivity;
import hu.ait.android.instant.FragmentProfile;
import hu.ait.android.instant.R;
import hu.ait.android.instant.data.DataManager;
import hu.ait.android.instant.data.User;

public class FollowAdapter extends RecyclerView.Adapter<FollowAdapter.ViewHolder> {
    private Context context;
    private List<User> userList;
    private List<Integer> changes;
    private String uId;
    private User userInfo;
    private boolean isFollower;

    public FollowAdapter(Context context, String uId, List<User> accounts, boolean isFollower) {
        this.context = context;
        this.uId = uId;

        loadUser();

        userList = accounts;
        changes = new ArrayList<>();

        this.isFollower = isFollower;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_user, parent, false);
        return new ViewHolder(row);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final User user = userList.get(position);

        holder.tvLilName.setText(user.getFullName());
        holder.tvLilDisplayName.setText(user.getDisplayName());

        if (user.getPhotoURL() != null) {
            Glide.with(context).load(user.getPhotoURL()).into(holder.ivLilAvatar);
        } else {
            holder.ivLilAvatar.setImageResource(R.drawable.instant_logo);
        }

        // look through and change all not following to say follow
        if(isFollower) {
            for(User useri: userInfo.getFollowing())
                if(useri.getUId().equals(user.getUId())){
                    holder.btnChangeFollow.setText(
                            context.getResources().getString(R.string.unfollow));
                }
        }

        holder.btnChangeFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.btnChangeFollow.getText().toString().equals(
                        context.getResources().getString(R.string.unfollow))) {
                    holder.btnChangeFollow.setText(
                            context.getResources().getString(R.string.follow));

                    // deal w unfollowing
                    removeUser(position + 1);
                } else {
                    holder.btnChangeFollow.setText(
                            context.getResources().getString(R.string.unfollow));

                    // deal w following
                    addUser(position + 1);
                }
            }
        });

        holder.ivLilAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DataManager.getInstance().setData(userInfo.getUId());
                ((BottomNavActivity)context).showFragment(FragmentProfile.TAG);
            }
        });
    }

    public void makeFollowChanges() {
        List<User> following = userInfo.getFollowing();

        // add the accounts
        for(int i : changes) {
            if(i > 0)
                following.add(userList.get(i - 1));
        }

        Collections.sort(changes);

        // remove the accounts
        for(int i: changes) {
            if(i < 0)
                following.remove(userList.get(i + 1));
            else
                break;
        }

        saveNewUserProfile(following);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public void addUser(int index) {
        for(int i = 0; i < changes.size(); i++) {
            if(changes.get(i) == -index) {
                changes.remove(i);
                return;
            }
        }

        changes.add(index);
    }

    public void removeUser(int index) {
        for(int i = 0; i < changes.size(); i++) {
            if(changes.get(i) == -index) {
                changes.remove(i);
                return;
            }
        }

        changes.add(-index);
    }

    private void saveNewUserProfile(List<User> following) {
        User newUserProfile = new User(userInfo.getFullName(), uId,
                userInfo.getDisplayName(), userInfo.getBiography());
        newUserProfile.setPhotoURL(userInfo.getPhotoURL());
        newUserProfile.setFollowers(userInfo.getFollowers());
        newUserProfile.setFollowing(following);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference();

        DatabaseReference usersRef = ref.child("users");

        usersRef.child(uId).setValue(newUserProfile);
    }

    private void loadUser() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference();

        DatabaseReference usersRef = ref.child("users");

        usersRef.orderByKey().equalTo(uId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                userInfo = dataSnapshot.getValue(User.class);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
            btnChangeFollow = itemView.findViewById(R.id.btnLilFollow);
            ivLilAvatar = itemView.findViewById(R.id.ivLilAvatar);
        }
    }
}
