package com.example.doit.model;

public interface EditImageNicknameListener {
    void onSkip(Runnable onFinish);
    void onImageAndNickname(String nickname, String profileImage, Runnable onFinish);
}
