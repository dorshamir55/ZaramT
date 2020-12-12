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
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.PreferenceManager;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.doit.R;
import com.example.doit.adapter.PostsRecyclerAdapter;
import com.example.doit.model.AnswerInPost;
import com.example.doit.model.BackButtonListener;
import com.example.doit.model.ChangeLabelListener;
import com.example.doit.model.Consumer;
import com.example.doit.model.ContextWrapper;
import com.example.doit.model.DeleteQuestionPostListener;
import com.example.doit.model.EditImageNicknameListener;
import com.example.doit.model.LocalHelper;
import com.example.doit.model.QuestionPostData;
import com.example.doit.model.UserData;
import com.example.doit.model.UserProfileListener;
import com.example.doit.model.VotersClickListener;
import com.example.doit.model.VotesClickListener;
import com.example.doit.service.MyFirebaseMessagingService;
import com.example.doit.viewmodel.IMainViewModel;
import com.example.doit.viewmodel.MainViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
//import com.example.doit.service.MyFirebaseMessagingService;

public class MainActivity extends AppCompatActivity implements EditImageNicknameListener, VotesClickListener,
        DeleteQuestionPostListener, /*UserDataListener, */BackButtonListener, UserProfileListener, VotersClickListener,
        ChangeLabelListener {
    public static boolean isSignInNow = true;
    public int i = 0;
    private IMainViewModel viewModel = null;
    private AppBarConfiguration mAppBarConfiguration;
    private NavigationView navigationView;
    private FragmentManager manager;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    public UserData userData;
    private DrawerLayout drawer;
    private Toolbar toolbar;
    private TextView toolbarTitleTV;

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

        toolbarTitleTV = findViewById(R.id.toolbar_title_text_view);
        toolbar = findViewById(R.id.toolbar);
//        toolbar.setTitleTextColor(Color.WHITE);
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
                        replaceFragment(new HomeFragment());
                        break;

                    case R.id.action_search:
                        replaceFragment(new SearchFragment());
                        break;

                    case R.id.action_add:
                        replaceFragment(new AddPostFragment());
                        break;

                    case R.id.action_statistics:
                        replaceFragment(new StatisticsMenuFragment());
                        break;

                    case R.id.action_profile:
                        moveToProfile(currentUser.getUid());
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
        currentUser = auth.getCurrentUser();
        userData = new UserData();
        Consumer<UserData> userConsumer = new Consumer<UserData>() {
            @Override
            public void apply(UserData currentUser) {
                userData = currentUser;
                welcomeTV.setText(userData.getNickName());
                welcomeTV.setMovementMethod(LinkMovementMethod.getInstance());
            }
        };
        viewModel.getCurrentUserData(currentUser.getUid(), userConsumer);

        // Check if FCM token was re-generated but the user auth uid couldn't be granted..
        String newToken = PreferenceManager.getDefaultSharedPreferences(this)
                .getString(MyFirebaseMessagingService.SP_TOKEN_KEY, null);

        if(newToken != null) {
            PreferenceManager.getDefaultSharedPreferences(this)
                    .edit()
                    .remove(MyFirebaseMessagingService.SP_TOKEN_KEY)
                    .commit();

            MyFirebaseMessagingService.updateTokenInFirestore(getApplicationContext(), newToken);
        }
    }

    private void replaceFragment (Fragment fragment){
        String backStateName = fragment.getClass().getName();

        FragmentManager manager = getSupportFragmentManager();
        boolean fragmentPopped = manager.popBackStackImmediate (backStateName, 0);
        
        if (!fragmentPopped || fragmentPopped && fragment instanceof MyProfileFragment){ //fragment not in back stack, create it.
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

//        Intent intent = new Intent(this, NotificationService.class);
//
//        startService(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        auth.addAuthStateListener(authStateListener);
        isSignInNow = false;

//        MyFirebaseMessagingService myFirebaseMessagingService = new MyFirebaseMessagingService();

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.d("TAG", "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();

                        // Log and toast
                        String msg = token;
                        Log.d("TAG", msg);
//                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });
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
//                        manager.popBackStack();
                        for(int i = 0; i < manager.getBackStackEntryCount(); ++i) {
                            manager.popBackStack();
                        }

                        FragmentTransaction ft = manager.beginTransaction();
                        ft.add(R.id.nav_host_fragment, new HomeFragment());
                        ft.addToBackStack(HomeFragment.class.getName());
                        ft.replace(R.id.nav_host_fragment, new MyProfileFragment(id));
                        ft.commit();

//                        replaceFragment(new MyProfileFragment(id));
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
    }

    @Override
    public void onDeleteQuestionPostIdFromUser(String questionPostID) {
        viewModel.deleteQuestionPostIdFromUser(questionPostID, userData.getId(), userData.getPostedQuestionPostsIdList());
    }

    @Override
    public void onDeleteQuestionPost(PostsRecyclerAdapter adapter, QuestionPostData questionPostData, int position) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.delete_title)
                .setMessage(R.string.delete_message)
                .setIcon(R.drawable.doit_icon)
                .setPositiveButton(getString(R.string.yes), (dialog, which) -> {
                    viewModel.deletePost(questionPostData);
                    viewModel.decrementAmountOfChosenQuestionInQuestionPost(questionPostData.getQuestion().getQuestionID());
                    viewModel.deleteQuestionPostIdFromUser(questionPostData.getId(), userData.getId(), userData.getPostedQuestionPostsIdList());
                    doAsynch(()-> viewModel.decrementVotesOfVoters(questionPostData.getId(), questionPostData.getAnswers()));
                    adapter.getData().remove(position);
                    adapter.notifyItemRemoved(position);
                })
                .setNegativeButton(getString(R.string.no), null)
                .create()
                .show();
    }

    private void doAsynch(Runnable task) {
        new Thread(task).start();
    }

//    @Override
//    public UserData onUserDataChanged(String userID) {
//        Consumer<UserData> userConsumer = new Consumer<UserData>() {
//            @Override
//            public void apply(UserData currentUser) {
//                userData = currentUser;
//            }
//        };
//        viewModel.getCurrentUserData(userID, userConsumer);
//
//        return userData;
//    }

    @Override
    public void onBackButtonClickListener(boolean flag) {
        if(flag) {
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }
        else{
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
            getSupportActionBar().setDisplayShowHomeEnabled(false);
            setSupportActionBar(toolbar);
        }
    }

    @Override
    public void onClickUserProfile(String userID) {
        moveToProfile(userID);
    }

    public void moveToProfile(String userID){
        MyProfileFragment myProfileFragment = new MyProfileFragment(userID);
//        Bundle bundle = new Bundle();
//        bundle.putString("userID", userID);
//        bundle.putString("userID", userID);
//        myProfileFragment.setArguments(bundle);
        replaceFragment(myProfileFragment);
    }

    @Override
    public void onVoterClickListener(List<AnswerInPost> answersInPost) {
        replaceFragment(new VotersFragment(answersInPost));
    }

    @Override
    public void onChangeLabelVisibleListener() {
        toolbar.setTitleTextColor(Color.BLACK);
        toolbarTitleTV.setVisibility(View.GONE);
    }

    @Override
    public void onChangeLabelGoneListener() {
        toolbar.setTitleTextColor(Color.WHITE);
        getSupportActionBar().setTitle("");
        toolbarTitleTV.setVisibility(View.VISIBLE);
    }

    @Override
    public void onChangeLabelTextListener(String title) {
        getSupportActionBar().setTitle(title);
    }
}