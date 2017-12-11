package hu.ait.android.instant.data;

import java.util.ArrayList;
import java.util.List;

public class User {

    private String fullName;
    private String displayName;
    private String uId;
    private String biography;
    private String photoURL;

    private List<User> following;
    private List<User> followers;

    public User() {
        following = new ArrayList<>();
        followers = new ArrayList<>();
    }

    public User(String fullName, String uId, String displayName) {
        this.fullName = fullName;
        this.uId = uId;
        this.displayName = displayName;
        biography = "";

        following = new ArrayList<>();
        followers = new ArrayList<>();
    }

    public User(String fullName, String uId, String displayName, String biography) {
        this.fullName = fullName;
        this.uId = uId;
        this.displayName = displayName;
        this.biography = biography;

        following = new ArrayList<>();
        followers = new ArrayList<>();
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUId() {
        return uId;
    }

    public void setUId(String uId) {
        this.uId = uId;
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    public List<User> getFollowing() {
        return following;
    }

    public void setFollowing(List<User> following) {
        this.following = following;
    }

    public void addFollowing(User user) {
        following.add(user);
    }

    public void removeFollowing(User user) {
        following.remove(user);
    }

    // TODO
    // FIGURE OUT HOW TO MAINTAIN FOLLOWERS

    public List<User> getFollowers() {
        return followers;
    }

    public void setFollowers(List<User> followers) {
        this.followers = followers;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getPhotoURL() {
        return photoURL;
    }

    public void setPhotoURL(String photoURL) {
        this.photoURL = photoURL;
    }
}
