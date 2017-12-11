package hu.ait.android.instant;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hu.ait.android.instant.data.User;

public class FragmentSettings extends Fragment {

    public static final String TAG = "FragmentSettings";

    private FirebaseUser user;
    private User userInfo;

    @BindView(R.id.ivEtAvatar)
    ImageView ivEtAvatar;

    @BindView(R.id.etSetName)
    EditText etSetName;

    @BindView(R.id.etSetDisplayName)
    EditText etSetDispName;

    @BindView(R.id.etSetBio)
    EditText etSetBio;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View viewRoot = inflater.inflate(R.layout.fragment_settings, container, false);

        ButterKnife.bind(this, viewRoot);

        user = FirebaseAuth.getInstance().getCurrentUser();

        loadSettings(viewRoot);

        return viewRoot;
    }

    private void loadSettings(View view) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference();

        DatabaseReference usersRef = ref.child("users");

        if(user.getPhotoUrl() != null)
            Glide.with(getActivity()).load(user.getPhotoUrl()).into(ivEtAvatar);

        usersRef.orderByKey().equalTo(user.getUid()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                userInfo = dataSnapshot.getValue(User.class);

                etSetName.setText(userInfo.getFullName());
                etSetBio.setText(userInfo.getBiography());
                etSetDispName.setText(user.getDisplayName());
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

    @OnClick(R.id.btnEtAvatar)
    public void changeAvatar() {
        // leaving this for someone else to do - should not be hard
    }

    @OnClick(R.id.btnLogout)
    public void logout() {
        ((BottomNavActivity)getActivity()).logout();
    }

    @OnClick(R.id.btnDone)
    public void saveSettings() {
        // TODO should really check validity of these but who cares
        String newName = etSetName.getText().toString();
        String newDisplayName = etSetDispName.getText().toString();
        String newBio = etSetBio.getText().toString();

        if(!newDisplayName.equals(user.getDisplayName())) {
            user.updateProfile(
                    new UserProfileChangeRequest.Builder().
                            setDisplayName(newDisplayName).build()
            );
        }

        User newUserProfile = new User(newName, user.getUid(), newBio);
        newUserProfile.setFollowers(userInfo.getFollowers());
        newUserProfile.setFollowing(userInfo.getFollowing());

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference();

        DatabaseReference usersRef = ref.child("users");

        usersRef.child(user.getUid()).setValue(newUserProfile);

        // ALSO make sure to eliminate settings fragment from fragment backstack ????
        ((BottomNavActivity)getActivity()).showFragment(FragmentProfile.TAG);
    }


}
