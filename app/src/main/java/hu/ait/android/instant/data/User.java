package hu.ait.android.instant.data;

// only necessary to store things related to the user that are not:
//   - email
//   - display name (username) ACTUALLY display name is used for account identification
//   - password
//   - photo URL (avatar)

import java.util.ArrayList;
import java.util.List;

public class User {

    private String fullName;
    private String uId;
    private String biography;

    private List<User> following;
    private List<User> followers;

    public User() {}

    public User(String fullName, String uId) {
        this.fullName = fullName;
        this.uId = uId;
        biography = "";

        following = new ArrayList<>();
        followers = new ArrayList<>();
    }

    public User(String fullName, String uId, String biography) {
        this.fullName = fullName;
        this.uId = uId;
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

    public List<User> getFollowers() {
        return followers;
    }

    public void setFollowers(List<User> followers) {
        this.followers = followers;
    }

    // FIGURE OUT HOW TO MAINTAIN FOLLOWERS
}
