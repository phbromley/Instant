package hu.ait.android.instant.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hu.ait.android.instant.BottomNavActivity;
import hu.ait.android.instant.FragmentProfile;
import hu.ait.android.instant.R;
import hu.ait.android.instant.data.DataManager;
import hu.ait.android.instant.data.User;

public class FollowAdapter extends RecyclerView.Adapter<FollowAdapter.ViewHolder> {
    private Context context;
    private List<String> userList;
    private List<Integer> changes;
    private String uId;
    private User userInfo;
    private boolean isFollower;

    public FollowAdapter(Context context, List<String> accounts, boolean isFollower) {
        this.context = context;
        userInfo = DataManager.getInstance().getCurrentUser();
        uId = userInfo.getUId();

        userList = accounts;
        changes = new ArrayList<>();

        this.isFollower = isFollower;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_users, parent, false);
        return new ViewHolder(row);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final User user = DataManager.getUser(userList.get(position));

        holder.tvLilName.setText(user.getFullName());
        holder.tvLilDisplayName.setText(user.getDisplayName());

        if (user.getPhotoURL() != null) {
            Glide.with(context).load(user.getPhotoURL()).into(holder.ivLilAvatar);
        } else {
            holder.ivLilAvatar.setImageResource(R.drawable.instant_logo);
        }

        if(isFollower) {
            boolean followsUser = false;
            for(String useri: userInfo.getFollowing()) {
                if (useri.equals(user.getUId())) {
                    followsUser = true;
                }
            }

            if(!followsUser) {
                holder.btnChangeFollow.setText(
                        context.getResources().getString(R.string.follow));
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
                DataManager.getInstance().setData(user.getUId());
                ((BottomNavActivity)context).showFragment(FragmentProfile.TAG);
            }
        });
    }

    public void makeFollowChanges() {
        List<String> following = userInfo.getFollowing();

        // add the accounts
        for(int i : changes) {
            if(i > 0)
                following.add(userList.get(i - 1));
        }

        Collections.sort(changes);

        // remove the accounts
        for(int i: changes) {
            if(i < 0)
                if(!isFollower)
                    following.remove(-i - 1);
                else {
                    String userId = userList.get(-i - 1);

                    for(int index = 0; index < following.size(); index++)
                        if(userId.equals(following.get(index))) {
                            following.remove(index);
                            break;
                        }
                }
            else
                break;
        }

        changes.clear();
        saveNewUserProfile(following);
        DataManager.getInstance().updateCurrentFollowing(following);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public void addUser(int index) {
        // deal with you following them
        for(int i = 0; i < changes.size(); i++) {
            if(changes.get(i) == -index) {
                changes.remove(i);
                return;
            }
        }

        changes.add(index);

        // deal with adding you to their followers
        makeFollower(userList.get(index - 1));
    }

    public void removeUser(int index) {
        // deal with you unfollowing them
        for(int i = 0; i < changes.size(); i++) {
            if(changes.get(i) == -index) {
                changes.remove(i);
                return;
            }
        }

        changes.add(-index);

        // deal with removing you from their followers
        makeUnfollower(userList.get(index - 1));
    }

    private void saveNewUserProfile(List<String> following) {
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

    public void makeFollower(String userId) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference();

        DatabaseReference usersRef = ref.child("users");

        User user = DataManager.getUser(userId);

        List<String> followers = user.getFollowers();

        followers.add(userInfo.getUId());

        user.setFollowers(followers);

        Log.d("TAG_NOPE", String.valueOf(user.getFollowers().size()));

        usersRef.child(user.getUId()).setValue(user);
    }

    public void makeUnfollower(String userId) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference();

        DatabaseReference usersRef = ref.child("users");

        User user = DataManager.getUser(userId);

        List<String> followers = user.getFollowers();

        for(int i = 0; i < followers.size(); i++)
            if(followers.get(i).equals(userInfo.getUId())) {
                followers.remove(i);
                break;
            }

        user.setFollowers(followers);

        Log.d("TAG_NOPE", String.valueOf(user.getFollowers().size()));

        //Map<String, Object> fuck = new HashMap<>();
        //fuck.put(user.getUId(), user);
        usersRef.child(user.getUId()).setValue(user);
        //usersRef.updateChildren(fuck);
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
