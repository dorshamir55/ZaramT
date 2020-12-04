package com.example.doit.ui;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.doit.R;
import com.example.doit.adapter.ChooseStatisticsAdapter;
import com.example.doit.model.BackButtonListener;
import com.example.doit.model.ChangeLabelListener;
import com.example.doit.model.DeleteQuestionPostListener;
import com.example.doit.model.UserProfileListener;
import com.example.doit.model.VotersClickListener;
import com.example.doit.model.VotesClickListener;

import java.util.ArrayList;
import java.util.List;

public class StatisticsMenuFragment extends Fragment {
    private ChooseStatisticsAdapter adapter;
    private List<String> statistics;
    private ChangeLabelListener changeLabelListener;
    private BackButtonListener backButtonListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        adapter = new ChooseStatisticsAdapter(getActivity());
        statistics = new ArrayList<>();
        statistics.add(getResources().getString(R.string.top_question));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_statistics, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.statisticsRecycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.setAdapter(adapter);
        adapter.setData(statistics);
        adapter.notifyDataSetChanged();

        adapter.setListener(new ChooseStatisticsAdapter.MyStatisticsListener() {
            @Override
            public void onStatisticsClicked(int position, View view) {

            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            changeLabelListener = (ChangeLabelListener)context;
            backButtonListener = (BackButtonListener)context;
        } catch(ClassCastException ex) {
            throw new ClassCastException("NOTE! The activity must implement the fragment's listener" +
                    " interface!");
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        changeLabelListener.onChangeLabelVisibleListener();
        changeLabelListener.onChangeLabelTextListener(getResources().getString(R.string.statistics));
        backButtonListener.onBackButtonClickListener(true);
    }

    @Override
    public void onPause() {
        super.onPause();

        changeLabelListener.onChangeLabelGoneListener();
        backButtonListener.onBackButtonClickListener(false);
    }
}