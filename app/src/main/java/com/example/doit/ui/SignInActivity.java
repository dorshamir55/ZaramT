package com.example.doit.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.doit.R;
import com.example.doit.model.Consumer;
import com.example.doit.model.UserData;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class SignInActivity extends AppCompatActivity
        implements GoogleFacebookLoginFragment.GoogleFacebookLoginFragmentClickListener,
                EditImageNicknameFragment.EditImageNicknameFragmentClickListener{

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private GoogleSignInClient mGoogleSignInClient;
    private final int RC_SIGN_IN = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.container2, new GoogleFacebookLoginFragment(), GoogleFacebookLoginFragment.TAG)
                .commit();

//        getSupportFragmentManager()
//                .beginTransaction()
//                .add(R.id.container2, new EditImageNicknameFragment(), EditImageNicknameFragment.TAG)
//                .commit();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
            // Signed in successfully, show authenticated UI.
            firebaseAuthWithGoogle(account.getIdToken());
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            loginError();
        }
    }

    private void loginError() {
        Toast.makeText(this, getResources().getString(R.string.signup_failed), Toast.LENGTH_SHORT).show();
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");

                            String email = mAuth.getCurrentUser().getEmail();
                            String nickname = mAuth.getCurrentUser().getDisplayName();
                            UserData userData = new UserData(nickname, email).withId(mAuth.getCurrentUser().getUid());
                            addUserToDB(userData);
//                            moveToFirstSignInFragment();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            loginError();
                        }
                    }
                });
    }

    private void isExistInDB(String email, Consumer<Boolean> consumer) {
        db.collection(UserData.TABLE_NAME).get()
                .addOnCompleteListener(task -> {
                    boolean isExist = false;
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult().getDocuments()) {
                            if (document.toObject(UserData.class).getEmail().equals(email)) {
                                isExist = true;
                                break;
                            }
                        }
                    } else {
                        task.getException().printStackTrace();
                    }
                    if (consumer != null)
                        consumer.apply(isExist);
                });
    }

    private void addUserToDB(UserData userData) {

        assert userData.getId() != null;

        Consumer<Boolean> isExist = new Consumer<Boolean>() {
            @Override
            public void apply(Boolean isUserExist) {
                if(!isUserExist){
                    db.collection(UserData.TABLE_NAME)
                            .document(userData.getId())
                            .set(userData)
                            .addOnSuccessListener(documentReference -> {
                                moveToFirstSignInFragment();
                            })
                            .addOnFailureListener(exception -> {
                                Toast.makeText(getBaseContext(), "ERROR", Toast.LENGTH_SHORT).show();
                            });
                }
                else{
                    moveToMainActivity();
                }
            }
        };
        isExistInDB(userData.getEmail(), isExist);
    }

    @Override
    public void onSignInGoogle(GoogleSignInOptions googleSignInOptions, Runnable onFinish) {
        mGoogleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void moveToFirstSignInFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container2, new EditImageNicknameFragment(), EditImageNicknameFragment.TAG)
                .commit();
    }

    private void moveToMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @Override
    public void onSkip(Runnable onFinish) {
//        String email = mAuth.getCurrentUser().getEmail();
//        String nickName = mAuth.getCurrentUser().getDisplayName();
//        UserData userData = new UserData(nickName, email).withId(mAuth.getCurrentUser().getUid());
//        addUserToDB(userData);
        moveToMainActivity();
    }

    @Override
    public void onImageAndNickname(String nickName, String profileImage, Runnable onFinish) {
        String id = mAuth.getCurrentUser().getUid();
        Map<String, Object> data = new HashMap<>();
        data.put("nickName", nickName);
        data.put("profileImageName", profileImage);
        db.collection(UserData.TABLE_NAME).document(id).set(data, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        if(onFinish != null)
                            onFinish.run();
                        moveToMainActivity();
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "ERROR", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
