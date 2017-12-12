package hu.ait.android.instant;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import hu.ait.android.instant.adapter.FollowAdapter;
import hu.ait.android.instant.data.DataManager;
import hu.ait.android.instant.data.User;

/*  TODO NOTE ABOUT FOLLOWERS
*     WHEN YOU FOLLOW AN ACCOUNT YOU CAN CHANGE THAT ACCOUNT'S FOLLOWERS
*     LIST TO REFLECT THAT AND MAKE SURE EACH TIME DB PULLS THROUGH NEW DATA
*     LIKE IN NOTIFYITEMCHANGED()
* */

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

        user = DataManager.getInstance().getCurrentUser();

        initRecyclerView(viewRoot);

        return viewRoot;
    }

    @Override
    public void onPause() {
        adapter.makeFollowChanges();
        super.onPause();
    }

    private void initRecyclerView(View view) {
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewFollow);

        switch (DataManager.getInstance().getData()) {
            case FOLLOWER:
                adapter = new FollowAdapter(getActivity(), user.getUId(), user.getFollowers(), true);
                break;
            case FOLLOWING:
                adapter = new FollowAdapter(getActivity(), user.getUId(), user.getFollowing(), false);
                break;
            default:
                adapter = new FollowAdapter(getActivity(), user.getUId(), user.getFollowers(), true);
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
