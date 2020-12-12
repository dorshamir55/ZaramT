package com.example.doit.model;

import android.net.Uri;
import android.widget.ImageView;

public class StatisticElement {
    String titleElement;
    String value;
    Uri imageUri;
    int position;

    public StatisticElement(String titleElement, String value, Uri imageUri, int position) {
        this.titleElement = titleElement;
        this.value = value;
        this.imageUri = imageUri;
        this.position = position;
    }

    public String getTitleElement() {
        return titleElement;
    }

    public void setTitleElement(String titleElement) {
        this.titleElement = titleElement;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Uri getImageUri() {
        return imageUri;
    }

    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
