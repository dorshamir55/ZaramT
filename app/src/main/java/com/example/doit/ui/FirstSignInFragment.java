package com.example.doit.ui;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import com.example.doit.R;
import com.example.doit.model.UserData;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

public class FirstSignInFragment extends Fragment {
    public static final String TAG = "FIRST_SIGN_IN_FRG";
    private FirstSignInFragmentClickListener listener;

    private Button save;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first_sign_in, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ProgressBar loadingBar = view.findViewById(R.id.firstSignInLoadingBar);
        save = view.findViewById(R.id.save_button);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingBar.setVisibility(View.VISIBLE);
                listener.onSignIn(() -> loadingBar.setVisibility(View.INVISIBLE));
            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (FirstSignInFragment.FirstSignInFragmentClickListener)context;
        } catch(ClassCastException ex) {
            throw new ClassCastException("NOTE! The activity must implement the fragment's listener" +
                    " interface!");
        }
    }

    public static interface FirstSignInFragmentClickListener {
        public void onSignIn(Runnable onFinish);
    }
}