package com.example.doit.ui;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.doit.R;
import com.example.doit.adapter.ChooseStatisticsAdapter;
import com.example.doit.model.BackButtonListener;
import com.example.doit.model.ChangeLabelListener;
import com.example.doit.model.Consumer;
import com.example.doit.model.DeleteQuestionPostListener;
import com.example.doit.model.LocalHelper;
import com.example.doit.model.QuestionFireStore;
import com.example.doit.model.Statistic;
import com.example.doit.model.StatisticElement;
import com.example.doit.model.UserProfileListener;
import com.example.doit.model.VotersClickListener;
import com.example.doit.model.VotesClickListener;
import com.example.doit.viewmodel.IMainViewModel;
import com.example.doit.viewmodel.MainViewModel;

import java.util.ArrayList;
import java.util.List;

public class StatisticsMenuFragment extends Fragment {
    private IMainViewModel viewModel = null;
    private ChooseStatisticsAdapter adapter;
    private List<Statistic> statistics;
    private ChangeLabelListener changeLabelListener;
    private BackButtonListener backButtonListener;
    private LocalHelper localHelper;
    private String currentLanguage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        adapter = new ChooseStatisticsAdapter(getActivity());
        this.localHelper = new LocalHelper(getActivity());
        this.currentLanguage = localHelper.getLocale();
        statistics = new ArrayList<>();
        Statistic statistic1 = new Statistic(getResources().getString(R.string.top_question));

        Consumer<List<QuestionFireStore>> listConsumer = new Consumer<List<QuestionFireStore>>() {
            List<StatisticElement> competitors = null;
            @Override
            public void apply(List<QuestionFireStore> data) {
                competitors = new ArrayList<>();
                long currentMaxChoices = data.get(0).getAmountOfChoices();
                int degree = 1;
                for(QuestionFireStore questionFireStore : data) {
                    String imagePath = null;
                    if(questionFireStore.getAmountOfChoices() < currentMaxChoices){
                        degree++;
                    }
                    if(degree == 1){
                        imagePath = String.valueOf(R.drawable.gold_medal);
                    }
                    else if (degree == 2){
                        imagePath = String.valueOf(R.drawable.silver_medal);
                    }
                    else if(degree == 3) {
                        imagePath = String.valueOf(R.drawable.bronze_medal);
                    }
                    Uri imageUri = Uri.parse("android.resource://com.example.doit/" + imagePath);
                    StatisticElement statisticElement = new StatisticElement(questionFireStore.getTextByLanguage(currentLanguage), String.valueOf(questionFireStore.getAmountOfChoices()), imageUri);
                    competitors.add(statisticElement);
                }
                statistic1.setCompetitors(competitors);
            }
        };
        viewModel.getTopQuestions(listConsumer, 1);

        statistics.add(statistic1);
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
            public void onStatisticsClicked(int position, View view, Statistic statistic) {
                ResultFragment resultFragment = new ResultFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("statistic", statistic);
                resultFragment.setArguments(bundle);

                FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.nav_host_fragment, resultFragment)
                        .addToBackStack(resultFragment.getClass().getName());
                fragmentTransaction.commit();
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