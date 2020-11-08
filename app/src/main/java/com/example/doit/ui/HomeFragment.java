package com.example.doit.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
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
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.doit.R;
import com.example.doit.adapter.PostsRecyclerAdapter;
import com.example.doit.model.QuestionPostData;
import com.example.doit.viewmodel.IMainViewModel;
import com.example.doit.viewmodel.MainViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    private IMainViewModel viewModel = null;
    private SwipeRefreshLayout swipeContainer;
    private PostsRecyclerAdapter adapter;// = new PostsRecyclerAdapter(getActivity());
    private BroadcastReceiver reloadAdsReceiver;

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
                if(viewModel != null)
                    viewModel.loadAds(null);
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
//                deletePost(position, clickedPost);
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
                deletePost(position, clickedPost);
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

    /*@Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.search_bar){
            cardView = getActivity().findViewById(R.id.card_view_search);
            if(!cardIsVisible){
                item.setIcon(R.drawable.ic_opened_details);
                cardIsVisible=true;
                cardView.setVisibility(View.VISIBLE);
            }
            else {
                item.setIcon(R.mipmap.ic_search);
                cardIsVisible=false;
                cardView.setVisibility(View.GONE);
            }
        }

        return super.onOptionsItemSelected(item);*/
    }

    private void vote(QuestionPostData clickedPost, int answerVoted){
        //clickedPost.getAnswers().get(answerPosition).setVotes(clickedPost.getAnswers().get(answerPosition).getVotes() + 1);
        viewModel.voteOnPost(clickedPost.getId(), currentUser.getUid(), clickedPost.getAnswers(), answerVoted);
    }

    private void deletePost(int position, QuestionPostData clickedPost){
        new AlertDialog.Builder(getContext())
                .setTitle(R.string.delete_title)
                .setMessage(R.string.delete_message)
                .setIcon(R.drawable.doit_icon)
                .setPositiveButton(getString(R.string.yes), (dialog, which) -> {
                    viewModel.deletePost(clickedPost);
                    adapter.getData().remove(position);
                    adapter.notifyItemRemoved(position);
                })
                .setNegativeButton(getString(R.string.no), null)
                .create()
                .show();
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
}