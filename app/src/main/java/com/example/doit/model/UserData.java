package com.example.doit.model;


import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class UserData
{
    public static final String TABLE_NAME = "users";

    private String id;  // Auth uid + Firestore document id (Excluded)
    private String nickName;
    private String email;

    public UserData() {
    }

    public UserData(String nickName, String email) {
        this.nickName = nickName;
        this.email = email;
    }

    public UserData withId(String id) {
        this.id = id;
        return this;
    }

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

    @Exclude
    public String getId() {
        return id;
    }
}

