package com.example.doit.ui;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.doit.R;
import com.example.doit.adapter.ChoosePictureAccountAdapter;
import com.example.doit.model.BackButtonListener;
import com.example.doit.model.ChangeLabelListener;
import com.example.doit.model.Consumer;
import com.example.doit.model.EditImageNicknameListener;
import com.example.doit.model.QuestionFireStore;
import com.example.doit.model.UserData;
import com.example.doit.viewmodel.IMainViewModel;
import com.example.doit.viewmodel.MainViewModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class EditImageNicknameFragment extends Fragment {
    private IMainViewModel viewModel = null;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    public static final String TAG = "FIRST_SIGN_IN_FRG";
    public static final String PROFILE_IMAGES_FOLDER = "profile_pictures/";
    private EditImageNicknameListener editImageNicknameListener;
    private ChangeLabelListener changeLabelListener;
    private ChoosePictureAccountAdapter adapter;
    private ChoosePictureAccountAdapter.MyPictureListener pictureListener;
    private UserData userData;

    private List<StorageReference> items;
    private Button saveButton, skipButton;
    private EditText nicknameEditText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        items = new ArrayList<>();
        userData = new UserData();

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            userData = (UserData) bundle.getSerializable("user_data");
        }

        if(userData != null) {
            adapter = new ChoosePictureAccountAdapter(getActivity(), userData.getProfileImageName());
        }

        StorageReference reference = FirebaseStorage.getInstance().getReference(PROFILE_IMAGES_FOLDER);
        reference.listAll()
                .addOnSuccessListener( listResult -> {
                    for (StorageReference item : listResult.getItems()) {
//                        item.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                            @Override
//                            public void onSuccess(Uri uri) {
//                                uriList.add(uri);
//                            }
//                        });
                        items.add(item);
                    }
                    adapter.setData(items);
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                    }
                });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_image_nickname, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ProgressBar loadingBar = view.findViewById(R.id.firstSignInLoadingBar);
        nicknameEditText = view.findViewById(R.id.nickname_edit_text);

//        Consumer<UserData> userConsumer = new Consumer<UserData>() {
//            @Override
//            public void apply(UserData currentUser) {
//                userData = currentUser;
//                nicknameEditText.setText(currentUser.getNickName());
////                adapter.setSavedImage(userData.getProfileImageName());
////                adapter.notifyDataSetChanged();
//            }
//        };
//        viewModel.getCurrentUserData(currentUser.getUid(), userConsumer);

        nicknameEditText.setText(userData.getNickName());

        skipButton = view.findViewById(R.id.skip_button);
        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingBar.setVisibility(View.VISIBLE);
                editImageNicknameListener.onSkip(() -> loadingBar.setVisibility(View.INVISIBLE));
            }
        });

        if(!MainActivity.isSignInNow)
            skipButton.setVisibility(View.GONE);

        nicknameEditText.setText(userData.getNickName());

        saveButton = view.findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nickname = nicknameEditText.getText().toString();
                String message = getResources().getString(R.string.nickname_validation);

                if(nickname.equals("")){
                    Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                    return;
                }
                loadingBar.setVisibility(View.VISIBLE);
                editImageNicknameListener.onImageAndNickname(nickname, ChoosePictureAccountAdapter.currentImageSelectedName,
                        () -> loadingBar.setVisibility(View.INVISIBLE));
            }
        });

//        Consumer<List<Uri>> uriConsumer = new Consumer<List<Uri>>() {
//            @Override
//            public void apply(List<Uri> uriList) {
//                adapter.setData(uriList);
//                adapter.notifyDataSetChanged();
//            }
//        };
//        viewModel.getAllAccountImages(uriConsumer);

        RecyclerView recyclerView = view.findViewById(R.id.choosePicturesRecycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        recyclerView.setAdapter(adapter);

        loadingBar.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loadingBar.setVisibility(View.INVISIBLE);
                adapter.notifyDataSetChanged();
            }
        }, 1500);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            editImageNicknameListener = (EditImageNicknameListener)context;
            changeLabelListener = (ChangeLabelListener)context;
//            backButtonListener = (BackButtonListener)context;
        } catch(ClassCastException ex) {
            throw new ClassCastException("NOTE! The activity must implement the fragment's listener" +
                    " interface!");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        changeLabelListener.onChangeLabelVisibleListener();
        changeLabelListener.onChangeLabelTextListener(getResources().getString(R.string.edit));
//        backButtonListener.onBackButtonClickListener(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        changeLabelListener.onChangeLabelGoneListener();
//        backButtonListener.onBackButtonClickListener(false);
    }
}