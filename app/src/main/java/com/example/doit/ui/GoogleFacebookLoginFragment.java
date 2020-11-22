package com.example.doit.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.doit.R;
import com.example.doit.model.Consumer;
import com.example.doit.model.QuestionFireStore;
import com.example.doit.model.UserData;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

import static android.content.ContentValues.TAG;

public class GoogleFacebookLoginFragment extends Fragment {
    private GoogleFacebookLoginFragmentClickListener listener;
    public static final String TAG = "GOOGLE_FACEBOOK_LOGIN_FRG";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_google_facebook_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SignInButton googleLogin = view.findViewById(R.id.sign_in_google);
        Button facebookLogin = view.findViewById(R.id.sign_in_facebook);
        ProgressBar loadingBar = view.findViewById(R.id.googleFacebookLoadingBar);

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        setGooglePlusButtonText(googleLogin, getResources().getString(R.string.continue_with_google));

        googleLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingBar.setVisibility(View.VISIBLE);
                doAsynch(new Runnable() {
                    @Override
                    public void run() {
                        listener.onSignInGoogle(googleSignInOptions, () -> loadingBar.setVisibility(View.INVISIBLE));
                    }
                });
            }
        });

        facebookLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                loadingBar.setVisibility(View.VISIBLE);
                //TODO
            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (GoogleFacebookLoginFragmentClickListener) context;
        } catch(ClassCastException ex) {
            throw new ClassCastException("NOTE! The activity must implement the fragment's listener" +
                    " interface!");
        }
    }

    public static interface GoogleFacebookLoginFragmentClickListener {
        public void onSignInGoogle(GoogleSignInOptions googleSignInOptions, Runnable onFinish);
    }

//    private void signIn() {
//        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
//        startActivityForResult(signInIntent, RC_SIGN_IN);
//    }
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
//        if (requestCode == RC_SIGN_IN) {
//            // The Task returned from this call is always completed, no need to attach
//            // a listener.
//            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
//            handleSignInResult(task);
//        }
//    }
//
//    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
//        try {
//            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
//            Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
//            // Signed in successfully, show authenticated UI.
//            firebaseAuthWithGoogle(account.getIdToken());
//        } catch (ApiException e) {
//            // The ApiException status code indicates the detailed failure reason.
//            // Please refer to the GoogleSignInStatusCodes class reference for more information.
//            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
//            loginError();
//        }
//    }
//
//    private void loginError() {
//        Toast.makeText(getContext(), getResources().getString(R.string.signup_failed), Toast.LENGTH_SHORT).show();
//    }
//
//    private void moveToMainActivity() {
//        startActivity(new Intent(getActivity(), MainActivity.class));
//        getActivity().finish();
//    }
//
//    private void firebaseAuthWithGoogle(String idToken) {
//        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
//
//        mAuth.signInWithCredential(credential)
//                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            // Sign in success, update UI with the signed-in user's information
//                            Log.d(TAG, "signInWithCredential:success");
//
//                            String email = mAuth.getCurrentUser().getEmail();
//                            String nickname = mAuth.getCurrentUser().getDisplayName();
//                            UserData userData = new UserData(nickname, email).withId(mAuth.getCurrentUser().getUid());
//                            addUserToDB(userData);
//                        } else {
//                            // If sign in fails, display a message to the user.
//                            Log.w(TAG, "signInWithCredential:failure", task.getException());
//                            loginError();
//                        }
//                    }
//                });
//    }
//
//    private void isExistInDB(String email, Consumer<Boolean> consumer) {
//        db.collection("users").get()
//                .addOnCompleteListener(task -> {
//                    boolean isExist = false;
//                    if (task.isSuccessful()) {
//                        for (DocumentSnapshot document : task.getResult().getDocuments()) {
//                            if (document.toObject(UserData.class).getEmail().equals(email)) {
//                                isExist = true;
//                                break;
//                            }
//                        }
//                    } else {
//                        task.getException().printStackTrace();
//                    }
//                    if (consumer != null)
//                        consumer.apply(isExist);
//                });
//    }

    protected void setGooglePlusButtonText(SignInButton signInButton, String buttonText) {
        // Find the TextView that is inside of the SignInButton and set its text
        for (int i = 0; i < signInButton.getChildCount(); i++) {
            View v = signInButton.getChildAt(i);

            if (v instanceof TextView) {
                TextView tv = (TextView) v;
                tv.setText(buttonText);
                return;
            }
        }
    }

    //    private void addUserToDB(UserData userData) {
//
//        assert userData.getId() != null;
//
//        Consumer<Boolean> isExist = new Consumer<Boolean>() {
//            @Override
//            public void apply(Boolean isUserExist) {
//                if(!isUserExist){
//                    db.collection("users")
//                            .document(userData.getId())
//                            .set(userData)
//                            .addOnSuccessListener(documentReference -> {
//                                moveToMainActivity();
//                            })
//                            .addOnFailureListener(exception -> {
//                                Toast.makeText(getContext(), "ERROR", Toast.LENGTH_SHORT).show();
//                            });
//                }
//                else{
//                    moveToMainActivity();
//                }
//            }
//        };
//        isExistInDB(userData.getEmail(), isExist);
//    }
    private void doAsynch(Runnable task) {
        new Thread(task).start();
    }
}
