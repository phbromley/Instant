package hu.ait.android.instant;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.ButterKnife;
import hu.ait.android.instant.adapter.FollowAdapter;
import hu.ait.android.instant.data.User;

public class FragmentFollow extends Fragment {

    public static final String TAG = "FragmentFollow";

    private FollowAdapter adapter;
    private FirebaseUser user;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View viewRoot = inflater.inflate(R.layout.fragment_follow, container, false);

        ButterKnife.bind(this, viewRoot);

        user = FirebaseAuth.getInstance().getCurrentUser();

        initRecyclerView(viewRoot);

        return viewRoot;
    }

    private void initRecyclerView(View view) {
        final RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewFollow);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference();

        DatabaseReference usersRef = ref.child("users");

        usersRef.orderByKey().equalTo(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User userInfo = dataSnapshot.getValue(User.class);

                // TODO
                // FIND A WAY TO CHECK IF ITS FOLLOWING OR FOLLOWERS
                adapter = new FollowAdapter(getActivity(), user.getUid(), userInfo.getFollowers());

                LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                layoutManager.setReverseLayout(true);
                layoutManager.setStackFromEnd(true);
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
