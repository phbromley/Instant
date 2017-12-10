package hu.ait.android.instant;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;

import hu.ait.android.instant.adapter.PostsAdapter;

public class FragmentProfile extends Fragment {

    public static final String TAG = "FragmentProfile";

    private PostsAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View viewRoot = inflater.inflate(R.layout.fragment_profile, container, false);

        /*RecyclerView recyclerView = (RecyclerView) getActivity().findViewById(R.id.recyclerViewPosts3);
        adapter = new PostsAdapter(getActivity(), FirebaseAuth.getInstance().getCurrentUser().getUid());

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);*/

        return viewRoot;
    }
}
