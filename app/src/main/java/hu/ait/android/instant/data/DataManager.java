package hu.ait.android.instant.data;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DataManager {

    private String data;
    private User currentUser;

    private DataManager() {
        data = "";
        currentUser = new User();
        findCurrentUser();
    }

    private static DataManager instance = null;

    public static DataManager getInstance() {
        if(instance == null) {
            instance = new DataManager();
        }

        return instance;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void destroy() {
        data = "";
    }

    public User getCurrentUser() { return currentUser; }

    private void findCurrentUser() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference();

        DatabaseReference usersRef = ref.child("users");

        usersRef.orderByKey().equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                User userInfo = dataSnapshot.getValue(User.class);

                currentUser.setUId(userInfo.getUId());
                currentUser.setFullName(userInfo.getFullName());
                currentUser.setDisplayName(userInfo.getDisplayName());
                currentUser.setPhotoURL(userInfo.getPhotoURL());
                currentUser.setFollowing(userInfo.getFollowing());
                currentUser.setFollowers(userInfo.getFollowers());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                User userInfo = dataSnapshot.getValue(User.class);

                currentUser.setUId(userInfo.getUId());
                currentUser.setFullName(userInfo.getFullName());
                currentUser.setDisplayName(userInfo.getDisplayName());
                currentUser.setPhotoURL(userInfo.getPhotoURL());
                currentUser.setFollowing(userInfo.getFollowing());
                currentUser.setFollowers(userInfo.getFollowers());
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

    public static User getUser(String uId) {
        final User user = new User();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference();

        DatabaseReference usersRef = ref.child("users");

        usersRef.orderByKey().equalTo(uId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                User userInfo = dataSnapshot.getValue(User.class);

                user.setUId(userInfo.getUId());
                user.setFullName(userInfo.getFullName());
                user.setDisplayName(userInfo.getDisplayName());
                user.setPhotoURL(userInfo.getPhotoURL());
                user.setFollowing(userInfo.getFollowing());
                user.setFollowers(userInfo.getFollowers());
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

        return user;
    }
}
