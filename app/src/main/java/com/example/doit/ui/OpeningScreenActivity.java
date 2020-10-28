package com.example.doit.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.doit.R;
import com.google.firebase.auth.FirebaseAuth;

public class OpeningScreenActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opening_screen);
        auth = FirebaseAuth.getInstance();
        handler = new Handler();
    }

    @Override
    protected void onStart() {
        super.onStart();
        handler.postDelayed(() -> {
            startActivity(new Intent(OpeningScreenActivity.this, MainActivity.class));
            finish();
        }, 3000);
    }
}