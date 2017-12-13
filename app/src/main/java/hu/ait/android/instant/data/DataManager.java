package hu.ait.android.instant.data;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataManager {

    private String data;
    private User currentUser;
    private Map<String, User> cachedUsers;
    private Map<String, String> cachedNameToID;

    private DataManager() {
        data = "";
        currentUser = new User();
        cachedUsers = new HashMap<>();
        cachedNameToID = new HashMap<>();
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

                currentUser.setUId(FirebaseAuth.getInstance().getCurrentUser().getUid());
                currentUser.setFullName(userInfo.getFullName());
                currentUser.setDisplayName(userInfo.getDisplayName());
                currentUser.setBiography(userInfo.getBiography());
                currentUser.setPhotoURL(userInfo.getPhotoURL());
                currentUser.setFollowing(userInfo.getFollowing());
                currentUser.setFollowers(userInfo.getFollowers());

                loadCacheableUsers();
                loadCacheableUsersByName();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                User userInfo = dataSnapshot.getValue(User.class);

                currentUser.setUId(FirebaseAuth.getInstance().getCurrentUser().getUid());
                currentUser.setFullName(userInfo.getFullName());
                currentUser.setDisplayName(userInfo.getDisplayName());
                currentUser.setBiography(userInfo.getBiography());
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

    private void loadCacheableUsers() {
        for(String uId: currentUser.getFollowing()) {
            DataManager.getUser(uId);
        }

        for(String uId: currentUser.getFollowers()) {
            DataManager.getUser(uId);
        }

        cacheUser(currentUser);
    }

    private void loadCacheableUsersByName() {
        for(String displayName: currentUser.getFollowing()) {
            DataManager.getUser(displayName);
        }
    }

    public boolean containsUser(String uId) {
        return cachedUsers.containsKey(uId);
    }

    public boolean containsUserByName(String displayName) {
        return cachedNameToID.containsKey(displayName);
    }

    public User cachedUser(String uId) {
        return cachedUsers.get(uId);
    }

    public void cacheUser(User user) {
        cachedUsers.put(user.getUId(), user);
    }

    public void updateUser(User user) {
        if(cachedUsers.containsKey(user.getUId())) {
            cachedUsers.put(user.getUId(), user);
        }
    }

    public String cachedUserByName(String displayName) {
        return cachedNameToID.get(displayName);
    }

    public void cacheUserByName(User user) {
        cachedNameToID.put(user.getDisplayName(), user.getUId());
    }

    public void updateUserByName(User user) {
        if(cachedNameToID.containsKey(user.getDisplayName())) {
            cachedNameToID.put(user.getDisplayName(), user.getUId());
        }
    }

    public static User getUser(String uId) {
        if(DataManager.getInstance().containsUser(uId))
            return DataManager.getInstance().cachedUser(uId);

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
                user.setBiography(userInfo.getBiography());
                user.setPhotoURL(userInfo.getPhotoURL());
                user.setFollowing(userInfo.getFollowing());
                user.setFollowers(userInfo.getFollowers());
                DataManager.getInstance().cacheUser(user);
                DataManager.getInstance().cacheUserByName(user);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                User userInfo = dataSnapshot.getValue(User.class);

                DataManager.getInstance().updateUser(userInfo);
                DataManager.getInstance().updateUserByName(userInfo);

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

    public static User findUserByName(String displayName) {
        if (DataManager.getInstance().getCurrentUser().getDisplayName() == displayName) {
            return DataManager.getInstance().getCurrentUser();
        }
        if (DataManager.getInstance().containsUserByName(displayName)) {
            return getUser(DataManager.getInstance().cachedUserByName(displayName));
        }

        final User user = new User();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference();

        DatabaseReference usersRef = ref.child("users");

        usersRef.orderByChild("displayName").equalTo(displayName).addChildEventListener(new ChildEventListener() {
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
                User userInfo = dataSnapshot.getValue(User.class);
                Log.d("TAG_HAHA", String.valueOf(userInfo.getFollowers().size()));
                DataManager.getInstance().updateUser(userInfo);
                DataManager.getInstance().updateUserByName(userInfo);

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

//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
        if (user.getUId() == null) return null;

        return user;
    }

    public void updateCurrentFollowing(List<String> following) {
        currentUser.setFollowing(following);
    }

    public void destroyCurrentUser() {
        currentUser = null;
    }

    public static void signOut() {
        getInstance().destroyCurrentUser();
        instance = null;
    }
}
