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

import java.util.List;

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

        // TODO
        // change to say unfollow if u follow

        if(userId.equals(""))
            userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

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

                if(!userId.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
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
        DataManager.getInstance().setData(FragmentFollow.FOLLOWER);
        ((BottomNavActivity)getActivity()).showFragment(FragmentFollow.TAG);
    }

    @OnClick(R.id.layoutFollowing)
    public void showFollowing() {
        DataManager.getInstance().setData(FragmentFollow.FOLLOWING);
        ((BottomNavActivity)getActivity()).showFragment(FragmentFollow.TAG);
    }

    @OnClick(R.id.btnSettings)
    public void openSettings() {
        ((BottomNavActivity)getActivity()).showFragment(FragmentSettings.TAG);
    }

    @OnClick(R.id.btnFollow)
    public void followUser() {
        String uId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        List<User> myFollowing;

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference();

        DatabaseReference usersRef = ref.child("users");

        currentUser = DataManager.getInstance().getCurrentUser();

        myFollowing = currentUser.getFollowing();

        if(btnFollow.getText().toString().equals(
                getActivity().getResources().getString(R.string.unfollow))) {
            for(int i = 0; i < myFollowing.size(); i++)
                if(myFollowing.get(i).getUId().equals(userId)) {
                    myFollowing.remove(i);
                    break;
                }
        } else {
            myFollowing.add(userInfo);
        }

        currentUser.setFollowing(myFollowing);

        usersRef.child(uId).setValue(currentUser);
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
