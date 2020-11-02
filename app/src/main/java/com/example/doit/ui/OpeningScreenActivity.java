package com.example.doit.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.doit.R;
import com.example.doit.model.LocalHelper;
import com.google.firebase.auth.FirebaseAuth;

public class OpeningScreenActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private Handler handler;
    private LocalHelper localHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opening_screen);
        auth = FirebaseAuth.getInstance();
        handler = new Handler();
        localHelper = new LocalHelper(this);

        localHelper.loadLocale();
    }

    @Override
    protected void onStart() {
        super.onStart();
        handler.postDelayed(() -> {
            if(auth.getCurrentUser() != null)
                startActivity(new Intent(OpeningScreenActivity.this, MainActivity.class));
            else
                startActivity(new Intent(OpeningScreenActivity.this, SignInUpActivity.class));
            finish();
        }, 2000);
    }
}