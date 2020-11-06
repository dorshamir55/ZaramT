package com.example.doit.ui;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SearchView;
import android.widget.Spinner;

import com.bumptech.glide.request.transition.Transition;
import com.example.doit.R;
import com.example.doit.adapter.PostsRecyclerAdapter;
import com.example.doit.adapter.QuestionsRecyclerAdapter;
import com.example.doit.model.Consumer;
import com.example.doit.model.LocalHelper;
import com.example.doit.model.NewQuestion;
import com.example.doit.model.Question;
import com.example.doit.viewmodel.IMainViewModel;
import com.example.doit.viewmodel.MainViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class AddPostFragment extends Fragment {
    private IMainViewModel viewModel = null;
    private QuestionsRecyclerAdapter adapter;
    private LocalHelper localHelper;

    private Spinner categoryS;
    private ArrayAdapter categorySpinnerAdapter;
    private List<NewQuestion> currentQuestionsList, allQuestionsList;
    private SearchView searchView;
    private MenuItem searchItem;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        adapter = new QuestionsRecyclerAdapter(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_post, container, false);
    }

    @Override
    public void onPause() {
        super.onPause();
        searchItem.setVisible(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        localHelper = new LocalHelper(getActivity());
        Consumer<List<NewQuestion>> consumerList = new Consumer<List<NewQuestion>>() {
            @Override
            public void apply(List<NewQuestion> questionsList) {
                allQuestionsList = questionsList;
                adapter.setData(allQuestionsList);
                adapter.notifyDataSetChanged();
            }
        };
        viewModel.getListOfQuestions(consumerList);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);

        categoryS = view.findViewById(R.id.choose_category_spinner);

        String[] categoryArray = getResources().getStringArray(R.array.category_list);
        List<String> categoryList = new ArrayList<>(Arrays.asList(categoryArray));
        Collections.sort(categoryList); //planetsArray will be sorted
        String chooseCategory = getResources().getString(R.string.choose_category);
        categoryList.add(0, chooseCategory);

        categorySpinnerAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, categoryList);
        categorySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categoryS.setAdapter(categorySpinnerAdapter);

        RecyclerView recyclerView = view.findViewById(R.id.questionsRecycler);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        recyclerView.setAdapter(adapter);

        adapter.setRecyclerListener(new QuestionsRecyclerAdapter.QuestionsRecyclerListener() {
            @Override
            public void onItemClick(int position, View clickedView, NewQuestion clickedQuestion) {
                ChooseAnswersFragment chooseAnswerFragment = new ChooseAnswersFragment();
                Bundle bundle = new Bundle();
                //bundle.putString("questionID", clickedQuestion.getId());
                bundle.putSerializable("question", clickedQuestion);
                chooseAnswerFragment.setArguments(bundle);

                FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.nav_host_fragment, chooseAnswerFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        categoryS.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                if(allQuestionsList != null) {
                    if (position != 0)
                        if (localHelper.getLocale().equals("en"))
                            currentQuestionsList = allQuestionsList.stream().filter(question -> question.getEn().getCategory().equals(selectedItem)).collect(Collectors.toList());
                        else
                            currentQuestionsList = allQuestionsList.stream().filter(question -> question.getHe().getCategory().equals(selectedItem)).collect(Collectors.toList());
                    else
                        currentQuestionsList = new ArrayList<>(allQuestionsList);

                    adapter.setData(currentQuestionsList);
                    adapter.notifyDataSetChanged();
                    searchView.setQuery("", false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {

        searchItem = menu.findItem(R.id.action_search);
        searchItem.setVisible(true);
        searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return  false;
            }
        });
    }
}