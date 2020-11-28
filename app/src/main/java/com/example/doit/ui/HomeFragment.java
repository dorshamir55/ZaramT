package com.example.doit.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.doit.R;
import com.example.doit.adapter.PostsRecyclerAdapter;
import com.example.doit.model.DeleteQuestionPostListener;
import com.example.doit.model.QuestionPostData;
import com.example.doit.model.UserData;
import com.example.doit.model.UserDataListener;
import com.example.doit.model.VotesClickListener;
import com.example.doit.viewmodel.IMainViewModel;
import com.example.doit.viewmodel.MainViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeFragment extends Fragment {
    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    private IMainViewModel viewModel = null;
    private SwipeRefreshLayout swipeContainer;
    private PostsRecyclerAdapter adapter;// = new PostsRecyclerAdapter(getActivity());
    private BroadcastReceiver reloadAdsReceiver;
    private VotesClickListener votesClickListener;
    private DeleteQuestionPostListener deleteQuestionPostListener;
    private UserDataListener userDataListener;
    private UserData userData;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        adapter = new PostsRecyclerAdapter(getActivity());

        reloadAdsReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(viewModel != null) {
                    viewModel.loadAds(null);
                    userDataListener.onUserDataChanged();
                }
            }
        };
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);

        RecyclerView recyclerView = view.findViewById(R.id.mainRecycler);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        recyclerView.setAdapter(adapter);

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
//                deletePost(position, clickedPost);
                deleteQuestionPostListener.onDeleteQuestionPost(adapter, clickedPost, position);
            }
        });

/*      FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(view1 -> {
            startActivity(new Intent(getContext(), PostAdActivity.class));
        });*/

        swipeContainer = view.findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(() -> {
            // onRefresh()..
            viewModel.loadAds(() -> swipeContainer.setRefreshing(false));
        });
        swipeContainer.setColorSchemeResources(R.color.toolbarColor, R.color.toolbarColorLight, R.color.toolbarColorVeryLight);

        viewModel.getPostsLiveData().observe(getViewLifecycleOwner(), postsList -> {
            adapter.setData(postsList);
            adapter.notifyDataSetChanged();
        });

        viewModel.loadAds(null);
    }

    private void vote(QuestionPostData clickedPost, int answerVoted){
        //clickedPost.getAnswers().get(answerPosition).setVotes(clickedPost.getAnswers().get(answerPosition).getVotes() + 1);
//        viewModel.voteOnPost(clickedPost.getId(), currentUser.getUid(), clickedPost.getAnswers(), answerVoted);
        votesClickListener.onVote(clickedPost.getId(), currentUser.getUid(), clickedPost.getAnswers(), answerVoted);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(getContext() != null) {
            LocalBroadcastManager.getInstance(getContext()).registerReceiver(reloadAdsReceiver,
                    new IntentFilter("com.project.ACTION_RELOAD"));
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(getContext() != null) {
            LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(reloadAdsReceiver);
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            votesClickListener = (VotesClickListener)context;
            deleteQuestionPostListener = (DeleteQuestionPostListener)context;
            userDataListener = (UserDataListener)context;
        } catch(ClassCastException ex) {
            throw new ClassCastException("NOTE! The activity must implement the fragment's listener" +
                    " interface!");
        }
    }
}