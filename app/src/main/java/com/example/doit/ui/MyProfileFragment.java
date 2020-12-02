package com.example.doit.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.doit.R;
import com.example.doit.adapter.PostsRecyclerAdapter;
import com.example.doit.model.AnswerInPost;
import com.example.doit.model.BackButtonListener;
import com.example.doit.model.ChangeLabelListener;
import com.example.doit.model.Consumer;
import com.example.doit.model.DeleteQuestionPostListener;
import com.example.doit.model.QuestionPostData;
import com.example.doit.model.UserData;
import com.example.doit.model.UserDataListener;
import com.example.doit.model.VotersClickListener;
import com.example.doit.model.VotesClickListener;
import com.example.doit.viewmodel.IMainViewModel;
import com.example.doit.viewmodel.MainViewModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyProfileFragment extends Fragment {
    private IMainViewModel viewModel = null;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    private BroadcastReceiver reloadUserReceiver;
    private PostsRecyclerAdapter adapter;
    private DeleteQuestionPostListener deleteQuestionPostListener;
    private UserDataListener userDataListener;
    private VotesClickListener votesClickListener;
    private BackButtonListener backButtonListener;
    private VotersClickListener votersListener;
    private ChangeLabelListener changeLabelListener;
    private SwipeRefreshLayout swipeContainer;
    private UserData userData;
    private Uri imageUri;
    private boolean isDownloaded = false;
    private CircleImageView profileImage;
    private TextView nickname, votes, posts;
    private ImageView editIV;
    private String userID;

    public MyProfileFragment(String userID){
        this.userID = userID;
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_profile, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        adapter = new PostsRecyclerAdapter(getActivity());

        reloadUserReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                viewModel.loadAds(null);
//                userData = userDataListener.onUserDataChanged(userID);
                Consumer<UserData> userConsumer = new Consumer<UserData>() {
                    @Override
                    public void apply(UserData currentUser) {
                        userData = currentUser;
                        votes.setText(String.valueOf(userData.getVotedQuestionPostsIdList().size()));
                        posts.setText(String.valueOf(userData.getPostedQuestionPostsIdList().size()));
                    }
                };
                viewModel.getCurrentUserData(userID, userConsumer);
            }
        };
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        profileImage = view.findViewById(R.id.profile_image_cell_my_profile);
        nickname = view.findViewById(R.id.nickname_cell_my_profile);
        votes = view.findViewById(R.id.votes_value_text_view);
        posts = view.findViewById(R.id.posts_value_text_view);
        editIV = view.findViewById(R.id.edit_image_nickname_button);

//        Bundle bundle = this.getArguments();
//        if (bundle != null) {
//            userID = bundle.getString("userID");
//        }

        if(!currentUser.getUid().equals(userID)){
            editIV.setVisibility(View.GONE);
        }

        editIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditImageNicknameFragment editImageNicknameFragment = new EditImageNicknameFragment();

                Bundle bundle = new Bundle();
                bundle.putSerializable("user_data", userData);
                editImageNicknameFragment.setArguments(bundle);

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
                changeLabelListener.onChangeLabelTextListener(userData.getNickName());
                nickname.setText(userData.getNickName());
                votes.setText(String.valueOf(userData.getVotedQuestionPostsIdList().size()));
                posts.setText(String.valueOf(userData.getPostedQuestionPostsIdList().size()));

                if(isDownloaded){
                    if(isAdded()) {
                        Glide
                                .with(getActivity())
                                .load(imageUri)
                                .apply(new RequestOptions())
                                .into(profileImage);
                    }
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
        viewModel.getCurrentUserData(userID, userConsumer);

        swipeContainer = view.findViewById(R.id.swipeContainerMyProfile);
        swipeContainer.setOnRefreshListener(() -> {
            // onRefresh()..
            Consumer<UserData> userConsumerRefresh = new Consumer<UserData>() {
                @Override
                public void apply(UserData currentUser) {
                    userData = currentUser;
                    changeLabelListener.onChangeLabelTextListener(userData.getNickName());
                    votes.setText(String.valueOf(userData.getVotedQuestionPostsIdList().size()));
                    posts.setText(String.valueOf(userData.getPostedQuestionPostsIdList().size()));
                }
            };
            viewModel.getCurrentUserData(userID, userConsumerRefresh);

            viewModel.loadAds(() -> swipeContainer.setRefreshing(false));
        });
        swipeContainer.setColorSchemeResources(R.color.toolbarColor, R.color.toolbarColorLight, R.color.toolbarColorVeryLight);

        viewModel.getMyPostsLiveData(userID).observe(getViewLifecycleOwner(), postsList -> {
            adapter.setData(postsList);
            adapter.notifyDataSetChanged();
        });

        RecyclerView recyclerView = view.findViewById(R.id.profileRecycler);
        recyclerView.setHasFixedSize(false);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        recyclerView.setAdapter(adapter);
//        showMyPosts(userID);

        adapter.setRecyclerListener(new PostsRecyclerAdapter.PostsRecyclerListener() {
            @Override
            public void onItemClick(int position, View clickedView, QuestionPostData clickedPost) {

            }

            @Override
            public void onVoteClick(int position, View clickedView, QuestionPostData clickedPost, int votedRadiobuttonID) {
                switch (votedRadiobuttonID) {
                    case R.id.answer1_cell_post:
                        vote(clickedPost, 0);
                        break;
                    case R.id.answer2_cell_post:
                        vote(clickedPost, 1);
                        break;
                    case R.id.answer3_cell_post:
                        vote(clickedPost, 2);
                        break;
                    case R.id.answer4_cell_post:
                        vote(clickedPost, 3);
                        break;
                }
            }

            @Override
            public void onDeleteClick(int position, MenuItem item, QuestionPostData clickedPost) {
                deleteQuestionPostListener.onDeleteQuestionPost(adapter, clickedPost, position);
            }

            @Override
            public void onNicknameClick(String userID) {

            }

            @Override
            public void onVotersClick(List<AnswerInPost> answersInPost) {
                votersListener.onVoterClickListener(answersInPost);
            }
        });
    }

    public void showMyPosts(String userID){
        new Handler().postDelayed(()-> {
            Consumer<List<QuestionPostData>> consumerList = new Consumer<List<QuestionPostData>>() {
                @Override
                public void apply(List<QuestionPostData> result) {
                    adapter.setData(result);
                    adapter.notifyDataSetChanged();
                }
            };
            viewModel.searchMyPostsAndRun(consumerList, userID);
        }, 200);
    }

    private void vote(QuestionPostData clickedPost, int answerVoted){
        votesClickListener.onVote(clickedPost.getId(), currentUser.getUid(), clickedPost.getAnswers(), answerVoted);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            deleteQuestionPostListener = (DeleteQuestionPostListener)context;
//            userDataListener = (UserDataListener)context;
            backButtonListener = (BackButtonListener)context;
            votesClickListener = (VotesClickListener)context;
            votersListener = (VotersClickListener)context;
            changeLabelListener = (ChangeLabelListener)context;
        } catch(ClassCastException ex) {
            throw new ClassCastException("NOTE! The activity must implement the fragment's listener" +
                    " interface!");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        changeLabelListener.onChangeLabelVisibleListener();

        if(getContext() != null) {
            LocalBroadcastManager.getInstance(getContext()).registerReceiver(reloadUserReceiver,
                    new IntentFilter("com.project.ACTION_RELOAD_USER"));
        }

        if(!currentUser.getUid().equals(userID)){
            backButtonListener.onBackButtonClickListener(true);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        changeLabelListener.onChangeLabelGoneListener();

        if(getContext() != null) {
            LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(reloadUserReceiver);
        }

        if(!currentUser.getUid().equals(userID)){
            backButtonListener.onBackButtonClickListener(false);
        }
    }

    @Nullable
    @Override
    public View getView() {
        return super.getView();
    }
}