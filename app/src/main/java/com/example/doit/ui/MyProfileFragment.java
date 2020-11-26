package com.example.doit.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.net.Uri;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.doit.R;
import com.example.doit.model.Consumer;
import com.example.doit.model.UserData;
import com.example.doit.viewmodel.IMainViewModel;
import com.example.doit.viewmodel.MainViewModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyProfileFragment extends Fragment {
    private IMainViewModel viewModel = null;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    private UserData userData;
    private Uri imageUri;
    private boolean isDownloaded = false;
    private CircleImageView profileImage;
    private TextView nickname;
    private ImageView editIV;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_profile, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        profileImage = view.findViewById(R.id.profile_image_cell_my_profile);
        nickname = view.findViewById(R.id.nickname_cell_my_profile);
        editIV = view.findViewById(R.id.edit_image_nickname_button);

        editIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditImageNicknameFragment editImageNicknameFragment = new EditImageNicknameFragment();
                FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.nav_host_fragment, editImageNicknameFragment)
                        .addToBackStack(editImageNicknameFragment.getClass().getName());
                fragmentTransaction.commit();
            }
        });

        userData = new UserData();
        Consumer<UserData> userConsumer = new Consumer<UserData>() {
            @Override
            public void apply(UserData currentUser) {
                userData = currentUser;
                nickname.setText(userData.getNickName());

                if(isDownloaded){
                    Glide
                            .with(getActivity())
                            .load(imageUri)
                            .apply(new RequestOptions())
                            .into(profileImage);
                }
                else {
                    FirebaseStorage.getInstance().getReference(EditImageNicknameFragment.PROFILE_IMAGES_FOLDER)
                            .child(userData.getProfileImageName()).getDownloadUrl()
                            .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    imageUri = uri;
                                    Glide
                                            .with(getContext())
                                            .load(uri)
                                            .apply(new RequestOptions())
                                            .into(profileImage);
                                    isDownloaded = true;
                                }
                            });
                }
            }
        };
        viewModel.getCurrentUserData(currentUser.getUid(), userConsumer);

    }
}