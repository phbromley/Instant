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

import butterknife.ButterKnife;
import hu.ait.android.instant.adapter.FollowAdapter;
import hu.ait.android.instant.data.DataManager;
import hu.ait.android.instant.data.User;

public class FragmentFollow extends Fragment {

    public static final String TAG = "FragmentFollow";
    public static final String FOLLOWER = "FOLLOWER";
    public static final String FOLLOWING = "FOLLOWING";

    private FollowAdapter adapter;
    private User user;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View viewRoot = inflater.inflate(R.layout.fragment_follow, container, false);

        ButterKnife.bind(this, viewRoot);

        String[] data = DataManager.getInstance().getData().split(":");

        String followString = data[0];
        String userId = data[1];

        if(userId.equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
            user = DataManager.getInstance().getCurrentUser();
        else {
            user = DataManager.getUser(userId);
            DataManager.getInstance();
        }

        initRecyclerView(viewRoot, followString);

        return viewRoot;
    }

    @Override
    public void onPause() {
        adapter.makeFollowChanges();
        super.onPause();
    }

    private void initRecyclerView(View view, String fString) {
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewFollow);



        switch (fString) {
            case FOLLOWER:
                adapter = new FollowAdapter(getActivity(), user.getFollowers(), true);
                break;
            case FOLLOWING:
                adapter = new FollowAdapter(getActivity(), user.getFollowing(), false);
                break;
            default:
                adapter = new FollowAdapter(getActivity(), user.getFollowers(), true);
                break;
        }

        DataManager.getInstance().destroy();

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

}
