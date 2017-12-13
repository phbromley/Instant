package hu.ait.android.instant.data;

import java.util.ArrayList;
import java.util.List;

public class User {

    private String fullName;
    private String displayName;
    private String uId;
    private String biography;
    private String photoURL;

    private List<String> following;
    private List<String> followers;

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

    public List<String> getFollowing() {
        return following;
    }

    public void setFollowing(List<String> following) {
        this.following = following;
    }

    public void addFollowing(User user) {
        following.add(user.getUId());
    }

    public void removeFollowing(User user) {
        for(int i = 0; i < following.size(); i++)
            if(following.get(i).equals(user.getUId())) {
                following.remove(i);
                break;
            }
    }

    public void addFollower(User user) { followers.add(user.getUId()); }

    public void removeFollower(User user) {
        for(int i = 0; i < followers.size(); i++)
            if(followers.get(i).equals(user.getUId())) {
                followers.remove(i);
                break;
            }
    }

    public List<String> getFollowers() {
        return followers;
    }

    public void setFollowers(List<String> followers) {
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
