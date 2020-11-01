package com.example.doit.ui;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.doit.R;
import com.example.doit.model.Answer;
import com.example.doit.model.Question;
import com.example.doit.model.QuestionPostData;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.type.DateTime;

import java.util.ArrayList;
import java.util.Calendar;
//import com.example.doit.service.MyFirebaseMessagingService;

public class MainActivity extends AppCompatActivity {
    private AppBarConfiguration mAppBarConfiguration;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        Question question = new Question("2", "What is it your favorite color?");
//        ArrayList<Answer> answers = new ArrayList<>();
//        answers.add(new Answer("1", "Red"));
//        answers.add(new Answer("2", "Blue"));
//        answers.add(new Answer("3", "Green"));
//        answers.add(new Answer("4", "Yellow"));
//        postQuestion(new QuestionPostData("22", question,  answers, Calendar.getInstance().getTime()));


        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.BLACK);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        navigationView.getMenu().findItem(R.id.nav_logout).setOnMenuItemClickListener(menuItem -> {
            logout();
            return false;
        });

        // which items considered as "top items" - will have a drawer icon instead of
        // back icon.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_my_profile)//, R.id.nav_my_ads, R.id.nav_notif_settings)
                .setDrawerLayout(drawer)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        auth = FirebaseAuth.getInstance();
        authStateListener = firebaseAuth -> { // onAuthChanged..
            // if signed-out
            if(firebaseAuth.getCurrentUser() == null) {
                startActivity(new Intent(MainActivity.this, SignInUpActivity.class));
                finish();
            }
        };

        // Show welcome
        View headerView = navigationView.getHeaderView(0);
        String welcome = getResources().getString(R.string.welcome);
        TextView welcomeTV = headerView.findViewById(R.id.welcome_nav_header);
        FirebaseUser currentUser = auth.getCurrentUser();
        welcomeTV.setText(welcome+currentUser.getDisplayName());
        welcomeTV.setMovementMethod(LinkMovementMethod.getInstance());

        // Check if FCM token was re-generated but the user auth uid couldn't be granted..
        /*String newToken = PreferenceManager.getDefaultSharedPreferences(this)
                .getString(MyFirebaseMessagingService.SP_TOKEN_KEY, null);

        if(newToken != null) {
            PreferenceManager.getDefaultSharedPreferences(this)
                    .edit()
                    .remove(MyFirebaseMessagingService.SP_TOKEN_KEY)
                    .commit();

            MyFirebaseMessagingService.updateTokenInFirestore(getApplicationContext(), newToken);
        }*/
    }

    private void logout() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.sign_out_title)
                .setMessage(R.string.sign_out_message)
                //.setIcon(R.mipmap.icon)
                .setPositiveButton(getString(R.string.yes), (dialog, which) -> {
                    FirebaseAuth.getInstance().signOut();
                })
                .setNegativeButton(getString(R.string.no), null)
                .create()
                .show();
    }

    private void postQuestion(QuestionPostData postData) {
        FirebaseFirestore.getInstance().collection(QuestionPostData.TABLE_NAME)
                .add(postData)
                .addOnSuccessListener(docRef -> {
                    LocalBroadcastManager.getInstance(getApplicationContext())
                            .sendBroadcast(new Intent("com.project.ACTION_RELOAD"));
                    //stopSelf();
                })
                .addOnFailureListener(ex -> {
                    ex.printStackTrace();
                    showMessageAndFinish("Something wrong");
                });
    }

    private void showMessageAndFinish(String msg) {
        try {
            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            //stopSelf();
        } catch(Throwable t) {
            t.printStackTrace();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(authStateListener != null)
            auth.removeAuthStateListener(authStateListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        auth.addAuthStateListener(authStateListener);
    }
}