package com.example.doit.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.doit.R;
import com.example.doit.model.AnswerInPost;
import com.example.doit.model.Consumer;
import com.example.doit.model.ContextWrapper;
import com.example.doit.model.Keyboard;
import com.example.doit.model.LocalHelper;
import com.example.doit.model.QuestionPostData;
import com.example.doit.model.UserData;
import com.example.doit.service.UploadPostService;
import com.example.doit.viewmodel.IMainViewModel;
import com.example.doit.viewmodel.MainViewModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.auth.User;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
//import com.example.doit.service.MyFirebaseMessagingService;

public class MainActivity extends AppCompatActivity implements EditImageNicknameFragment.EditImageNicknameFragmentClickListener,
        HomeFragment.VotesFragmentClickListener {
    public static boolean isSignInNow = true;
    private IMainViewModel viewModel = null;
    private AppBarConfiguration mAppBarConfiguration;
    private NavigationView navigationView;
    private FragmentManager manager;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    public UserData userData;
    private DrawerLayout drawer;

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //return super.onOptionsItemSelected(item);

        int itemId = item.getItemId();
        switch(itemId) {
            // Android home
            case android.R.id.home:
                drawer.openDrawer(GravityCompat.START);
                return true;
            // manage other entries if you have it ...
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        Map<String, UserData> users = new HashMap<>();
//        users.put("Lala", new UserData("Ronaldo", "dor123@321.com"));
//
//        usersRef.setValue(users);

//        Question question = new Question("2", "What is it your favorite color?");
//        ArrayList<Answer> answers = new ArrayList<>();
//        answers.add(new Answer("1", "Red"));
//        answers.add(new Answer("2", "Blue"));
//        answers.add(new Answer("3", "Green"));
//        answers.add(new Answer("4", "Yellow"));
//        postQuestion(new QuestionPostData("22", question,  answers, Calendar.getInstance().getTime()));

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        manager = getSupportFragmentManager();
        manager.beginTransaction().add(R.id.nav_host_fragment, new HomeFragment()).commit();

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        navigationView.getMenu().findItem(R.id.nav_settings).setOnMenuItemClickListener(menuItem -> {
            drawer.closeDrawer(navigationView);
            replaceFragment(new SettingsFragment());
            return false;
        });

        navigationView.getMenu().findItem(R.id.nav_logout).setOnMenuItemClickListener(menuItem -> {
            logout();
            return false;
        });

        BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
                = new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_home:
//                        switchToHomeFragment();
                        replaceFragment(new HomeFragment());
                        break;

                    case R.id.action_search:
//                        switchToHomeFragment();

                        break;

                    case R.id.action_add:
//                        switchToAddPostFragment();
                        replaceFragment(new AddPostFragment());
                        break;

                    case R.id.action_statistics:

                        break;

                    case R.id.action_profile:
//                        switchToProfileFragment();
                        replaceFragment(new MyProfileFragment());
                        break;
                }
                return true;
            }

        };

        BottomNavigationView bottomNavigation = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        authStateListener = firebaseAuth -> { // onAuthChanged..
            // if signed-out
            if(firebaseAuth.getCurrentUser() == null) {
                //startActivity(new Intent(MainActivity.this, SignInUpActivity.class));
                startActivity(new Intent(MainActivity.this, SignInActivity.class));
                finish();
            }
        };

        // Show welcome
        View headerView = navigationView.getHeaderView(0);
        String welcome = getResources().getString(R.string.welcome);
        TextView welcomeTV = headerView.findViewById(R.id.welcome_nav_header);
        FirebaseUser currentUser = auth.getCurrentUser();
        userData = new UserData();
        Consumer<UserData> userConsumer = new Consumer<UserData>() {
            @Override
            public void apply(UserData currentUser) {
                userData = currentUser;
                welcomeTV.setText(welcome + userData.getNickName());
                welcomeTV.setMovementMethod(LinkMovementMethod.getInstance());
            }
        };
        viewModel.getCurrentUserData(currentUser.getUid(), userConsumer);

        if(userData != null) {
            welcomeTV.setText(welcome + userData.getNickName());
            welcomeTV.setMovementMethod(LinkMovementMethod.getInstance());
        }

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

    private void replaceFragment (Fragment fragment){
        String backStateName = fragment.getClass().getName();

        FragmentManager manager = getSupportFragmentManager();
        boolean fragmentPopped = manager.popBackStackImmediate (backStateName, 0);
        
        if (!fragmentPopped){ //fragment not in back stack, create it.
            FragmentTransaction ft = manager.beginTransaction();
            ft.replace(R.id.nav_host_fragment, fragment);
            ft.addToBackStack(backStateName);
            ft.commit();
        }
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
        isSignInNow = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        auth.addAuthStateListener(authStateListener);
        isSignInNow = false;
    }

    @Override
    protected void attachBaseContext(Context newBase) {

        LocalHelper localHelper = new LocalHelper(newBase);
        Locale newLocale = new Locale(localHelper.getLocaleLang());
        // .. create or get your new Locale object here.

        Context context = ContextWrapper.wrap(newBase, newLocale);
        super.attachBaseContext(context);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchItem.setVisible(false);

        return true;
    }

    @Override
    public void onSkip(Runnable onFinish) {

    }

    @Override
    public void onImageAndNickname(String nickName, String profileImage, Runnable onFinish) {
        String id = auth.getCurrentUser().getUid();
        Map<String, Object> data = new HashMap<>();
        data.put("nickName", nickName);
        data.put("profileImageName", profileImage);
        db.collection(UserData.TABLE_NAME).document(id).set(data, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        if(onFinish != null)
                            onFinish.run();
                        manager.popBackStack();
                        manager.beginTransaction().replace(R.id.nav_host_fragment, new MyProfileFragment()).commit();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "ERROR", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onVote(String questionPosdId, String currentUserId, List<AnswerInPost> answersList, int answerVoted) {
        viewModel.voteOnPost(questionPosdId, currentUserId, answersList, userData.getVotedQuestionPostsIdList(), answerVoted);
    }

    @Override
    public void onDecrementAmountOfChosenQuestionInQuestionPost(String questionID) {
        viewModel.decrementAmountOfChosenQuestionInQuestionPost(questionID);
        //TODO
        //viewModel.updateAmountOfChosenQuestionInUser(questionID, userData);
    }

    @Override
    public void onDeleteQuestionPostIdFromUser(String questionPostID) {
        viewModel.deleteQuestionPostIdFromUser(questionPostID, userData.getId(), userData.getPostedQuestionPostsIdList());
    }
}