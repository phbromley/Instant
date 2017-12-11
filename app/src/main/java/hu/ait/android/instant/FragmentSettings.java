package hu.ait.android.instant;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hu.ait.android.instant.data.Post;
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

    private String imgURL = null;

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

        usersRef.orderByKey().equalTo(user.getUid()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                userInfo = dataSnapshot.getValue(User.class);

                if(userInfo.getPhotoURL() != null)
                    Glide.with(getActivity()).load(userInfo.getPhotoURL()).into(ivEtAvatar);

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

        Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        this.startActivityForResult(intentCamera, 101);
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

        User newUserProfile = new User(newName, user.getUid(), newDisplayName, newBio);
        newUserProfile.setPhotoURL(imgURL);
        newUserProfile.setFollowers(userInfo.getFollowers());
        newUserProfile.setFollowing(userInfo.getFollowing());

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference();

        DatabaseReference usersRef = ref.child("users");

        usersRef.child(user.getUid()).setValue(newUserProfile);

        // ALSO make sure to eliminate settings fragment from fragment backstack ????
        ((BottomNavActivity)getActivity()).showFragment(FragmentProfile.TAG);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == Activity.RESULT_OK) {
            Bitmap img = (Bitmap) data.getExtras().get("data");
            ivEtAvatar.setImageBitmap(img);
            ivEtAvatar.setVisibility(View.VISIBLE);

            try {
                uploadAvatarImage();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void uploadAvatarImage() throws Exception {

        ivEtAvatar.setDrawingCacheEnabled(true);
        ivEtAvatar.buildDrawingCache();
        Bitmap bitmap = ivEtAvatar.getDrawingCache();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageInBytes = baos.toByteArray();

        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        String newImage = URLEncoder.encode(UUID.randomUUID().toString(), "UTF-8")+".jpg";
        StorageReference newImageRef = storageRef.child(newImage);
        StorageReference newImageImagesRef = storageRef.child("avatars/"+newImage);
        newImageRef.getName().equals(newImageImagesRef.getName());    // true
        newImageRef.getPath().equals(newImageImagesRef.getPath());    // false

        UploadTask uploadTask = newImageImagesRef.putBytes(imageInBytes);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Toast.makeText(getActivity(), exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                imgURL = taskSnapshot.getDownloadUrl().toString();

            }
        });
    }
}
