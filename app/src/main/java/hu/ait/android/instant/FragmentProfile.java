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
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hu.ait.android.instant.adapter.PostsAdapter;
import hu.ait.android.instant.data.Post;
import hu.ait.android.instant.data.User;

public class FragmentProfile extends Fragment {

    public static final String TAG = "FragmentProfile";

    private PostsAdapter adapter;
    private FirebaseUser user;

    private List<User> followers;
    private List<User> following;

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View viewRoot = inflater.inflate(R.layout.fragment_profile, container, false);

        ButterKnife.bind(this, viewRoot);

        // ALTER TO ALLOW FOR OTHER PROFILE VIEWING
        user = FirebaseAuth.getInstance().getCurrentUser();

        setupProfile(viewRoot);

        return viewRoot;
    }

    private void setupProfile(View view) {
        // set up to only show profile you're viewing -> should just be special querying
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewPosts3);
        adapter = new PostsAdapter(getActivity(), user.getUid());

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        initPostsListener();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference();

        DatabaseReference usersRef = ref.child("users");

        if(user.getPhotoUrl() != null)
            Glide.with(getActivity()).load(user.getPhotoUrl()).into(ivAvatar);

        usersRef.orderByKey().equalTo(user.getUid()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                User userInfo = dataSnapshot.getValue(User.class);

                tvName.setText(userInfo.getFullName());
                tvBio.setText(userInfo.getBiography());
                tvPosts.setText(String.valueOf(adapter.getItemCount()));

                followers = userInfo.getFollowers();
                following = userInfo.getFollowing();

                if(followers == null) {
                    tvFollowers.setText("0");
                } else {
                    tvFollowers.setText(String.valueOf(followers.size()));
                }

                if(following == null) {
                    tvFollowers.setText("0");
                } else {
                    tvFollowing.setText(String.valueOf(following.size()));
                }

                // if NOT your profile, make follow button visible and settings invisible
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

    @OnClick(R.id.btnSettings)
    public void openSettings() {
        ((BottomNavActivity)getActivity()).showFragment(FragmentSettings.TAG);
    }

    @OnClick(R.id.btnFollow)
    public void followUser() {

    }

    private void initPostsListener() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("posts");
        reference.orderByChild("uid").equalTo(user.getUid()).addChildEventListener(new ChildEventListener() {
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
