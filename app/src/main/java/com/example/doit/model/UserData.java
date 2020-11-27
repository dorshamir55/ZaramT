package com.example.doit.model;


import androidx.annotation.NonNull;

import com.example.doit.ui.EditImageNicknameFragment;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@IgnoreExtraProperties
public class UserData implements Serializable {
    public static final String TABLE_NAME = "users";
    public static final String DEFAULT_IMAGE = "_none_profile_image.png";

    @NonNull
    private String id;  // Auth uid + Firestore document id (Excluded)
    private String nickName;
    private String email;
    private String profileImageName;
    private List<String> votedQuestionPostsIdList;
    private List<String> postedQuestionPostsIdList;

    public UserData() {
    }

    public UserData(String nickName, String email) {
        this.nickName = nickName;
        this.email = email;
        this.profileImageName = DEFAULT_IMAGE;
        this.votedQuestionPostsIdList = new ArrayList<>();
        this.postedQuestionPostsIdList = new ArrayList<>();
    }

    public UserData withId(String id) {
        this.id = id;
        return this;
    }

    @Exclude
    public String getId() {
        return id;
    }

    @Exclude
    public void setId(String id) { this.id = id; }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfileImageName() {
        return profileImageName;
    }

    public void setProfileImageName(String profileImageName) {
        this.profileImageName = profileImageName;
    }

    public List<String> getVotedQuestionPostsIdList() {
        return votedQuestionPostsIdList;
    }

    public void setVotedQuestionPostsIdList(List<String> votedQuestionPostsIdList) {
        this.votedQuestionPostsIdList = votedQuestionPostsIdList;
    }

    public List<String> getPostedQuestionPostsIdList() {
        return postedQuestionPostsIdList;
    }

    public void setPostedQuestionPostsIdList(List<String> postedQuestionPostsIdList) {
        this.postedQuestionPostsIdList = postedQuestionPostsIdList;
    }
}

