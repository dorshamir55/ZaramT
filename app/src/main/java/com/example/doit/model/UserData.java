package com.example.doit.model;


import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class UserData
{
    public static final String TABLE_NAME = "users";

    private String id;  // Auth uid + Firestore document id (Excluded)
    private String nickname;
    private String email;

    public UserData() {
    }

    public UserData(String nickname, String email) {
        this.nickname = nickname;
        this.email = email;
    }

    public UserData withId(String id) {
        this.id = id;
        return this;
    }

    public String getNickName() {
        return nickname;
    }

    public void setNickName(String name) {
        this.nickname = nickname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Exclude
    public String getId() {
        return id;
    }
}

