package com.example.doit.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;

import com.example.doit.R;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private AppBarConfiguration mAppBarConfiguration;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        navigationView.getMenu().findItem(R.id.nav_logout).setOnMenuItemClickListener(menuItem -> {
            //logout();
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

        /*auth = FirebaseAuth.getInstance();
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
        String newToken = PreferenceManager.getDefaultSharedPreferences(this)
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

    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}