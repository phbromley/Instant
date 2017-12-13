package hu.ait.android.instant;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hu.ait.android.instant.adapter.PostsAdapter;
import hu.ait.android.instant.data.DataManager;
import hu.ait.android.instant.data.Post;
import hu.ait.android.instant.data.User;

public class FragmentProfile extends Fragment {

    public static final String TAG = "FragmentProfile";

    private PostsAdapter adapter;
    private String userId;
    private User userInfo;

    private User currentUser;

    private List<String> followers;
    private List<String> following;

    @BindView(R.id.ivAvatar)
    ImageView ivAvatar;

    @BindView(R.id.tvName)
    TextView tvName;

    @BindView(R.id.tvBiography)
    TextView tvBio;

    @BindView(R.id.tvPublications)
    TextView tvPosts;

    @BindView(R.id.tvFollowers)
    TextView tvFollowers;

    @BindView(R.id.tvFollowing)
    TextView tvFollowing;

    @BindView(R.id.btnFollow)
    Button btnFollow;

    @BindView(R.id.btnSettings)
    ImageButton btnSettings;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View viewRoot = inflater.inflate(R.layout.fragment_profile, container, false);

        ButterKnife.bind(this, viewRoot);

        userId = DataManager.getInstance().getData();

        currentUser = DataManager.getInstance().getCurrentUser();

        if(userId.equals(""))
            userId = currentUser.getUId();
        else {
            for(String uId: currentUser.getFollowing()) {
                if(uId.equals(userId)) {
                    toggleBtnFollow();
                    break;
                }
            }
        }

        DataManager.getInstance().destroy();

        setupProfile(viewRoot);

        return viewRoot;
    }

    private void setupProfile(View view) {
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewPosts3);
        adapter = new PostsAdapter(getActivity(), userId);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        initPostsListener();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference();

        DatabaseReference usersRef = ref.child("users");

        usersRef.orderByKey().equalTo(userId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                userInfo = dataSnapshot.getValue(User.class);

                tvName.setText(userInfo.getFullName());
                tvBio.setText(userInfo.getBiography());
                tvPosts.setText(String.valueOf(adapter.getItemCount()));

                if(userInfo.getPhotoURL() != null)
                    Glide.with(getActivity()).load(userInfo.getPhotoURL()).into(ivAvatar);

                followers = userInfo.getFollowers();
                following = userInfo.getFollowing();

                if(followers == null) {
                    tvFollowers.setText("NaN");
                } else {
                    tvFollowers.setText(String.valueOf(followers.size()));
                }

                if(following == null) {
                    tvFollowers.setText("NaN");
                } else {
                    tvFollowing.setText(String.valueOf(following.size()));
                }

                if(!userId.equals(currentUser.getUId())) {
                    btnFollow.setVisibility(View.VISIBLE);
                    btnSettings.setVisibility(View.GONE);
                }
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

    @OnClick(R.id.layoutFollowers)
    public void showFollowers() {
        DataManager.getInstance().setData(FragmentFollow.FOLLOWER + ":" + userId);
        ((BottomNavActivity)getActivity()).showFragment(FragmentFollow.TAG);
    }

    @OnClick(R.id.layoutFollowing)
    public void showFollowing() {
        DataManager.getInstance().setData(FragmentFollow.FOLLOWING + ":" + userId);
        ((BottomNavActivity)getActivity()).showFragment(FragmentFollow.TAG);
    }

    @OnClick(R.id.btnSettings)
    public void openSettings() {
        ((BottomNavActivity)getActivity()).showFragment(FragmentSettings.TAG);
    }

    @OnClick(R.id.btnFollow)
    public void followUser() {
        String uId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        List<String> myFollowing;

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference();

        DatabaseReference usersRef = ref.child("users");

        myFollowing = currentUser.getFollowing();

        if(btnFollow.getText().toString().equals(
                getActivity().getResources().getString(R.string.unfollow))) {
            for(int i = 0; i < myFollowing.size(); i++)
                if(myFollowing.get(i).equals(userId)) {
                    myFollowing.remove(i);
                    break;
                }
            makeUnfollower();
        } else {
            myFollowing.add(userInfo.getUId());
            makeFollower();
        }

        updateFollowCount();

        toggleBtnFollow();

        currentUser.setFollowing(myFollowing);

        usersRef.child(uId).setValue(currentUser);
    }

    private void updateFollowCount() {
        tvFollowers.setText(String.valueOf(followers.size()));
    }

    public void makeFollower() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference();

        DatabaseReference usersRef = ref.child("users");

        List<String> followers = userInfo.getFollowers();

        followers.add(currentUser.getUId());

        userInfo.setFollowers(followers);

        usersRef.child(userId).setValue(userInfo);
    }

    public void makeUnfollower() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference();

        DatabaseReference usersRef = ref.child("users");

        List<String> followers = userInfo.getFollowers();

        for(int i = 0; i < followers.size(); i++)
            if(followers.get(i).equals(currentUser.getUId())) {
                followers.remove(i);
                break;
            }

        userInfo.setFollowers(followers);

        usersRef.child(userId).setValue(userInfo);
    }

    private void toggleBtnFollow() {
        if(btnFollow.getText().toString().equals(
                getActivity().getResources().getString(R.string.unfollow))) {
            btnFollow.setText(
                    getActivity().getResources().getString(R.string.follow));
        } else {
            btnFollow.setText(
                    getActivity().getResources().getString(R.string.unfollow));
        }
    }

    private void initPostsListener() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("posts");
        reference.orderByChild("uid").equalTo(userId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Post post = dataSnapshot.getValue(Post.class);
                adapter.addPost(post, dataSnapshot.getKey());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                adapter.removePostByKey(dataSnapshot.getKey());
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
