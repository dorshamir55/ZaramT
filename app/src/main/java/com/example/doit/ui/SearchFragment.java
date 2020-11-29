package com.example.doit.ui;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.example.doit.R;
import com.example.doit.adapter.UserSearchRecyclerAdapter;
import com.example.doit.model.BackButtonListener;
import com.example.doit.model.Consumer;
import com.example.doit.model.QuestionFireStore;
import com.example.doit.model.UserData;
import com.example.doit.viewmodel.IMainViewModel;
import com.example.doit.viewmodel.MainViewModel;

import java.util.List;

public class SearchFragment extends Fragment {
    private IMainViewModel viewModel = null;
    private UserSearchRecyclerAdapter adapter;
    private BackButtonListener backButtonListener;
    private List<UserData> allUsersList;
    private SearchView searchView;
    private MenuItem searchItem;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        viewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);
        adapter = new UserSearchRecyclerAdapter(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.searchRecycler);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onPause() {
        super.onPause();

        searchItem.setVisible(false);
        backButtonListener.onBackButtonClickListener(false);
    }

    @Override
    public void onResume() {
        super.onResume();

        backButtonListener.onBackButtonClickListener(true);

        Consumer<List<UserData>> consumerList = new Consumer<List<UserData>>() {
            @Override
            public void apply(List<UserData> usersList) {
                allUsersList = usersList;
                adapter.setData(allUsersList);
                adapter.notifyDataSetChanged();
            }
        };
        viewModel.getAllUsers(consumerList);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {

        searchItem = menu.findItem(R.id.action_search);
        searchItem.setVisible(true);
        searchView = (SearchView) searchItem.getActionView();
        searchView.onActionViewExpanded();

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

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try{
            backButtonListener = (BackButtonListener)context;
        } catch(ClassCastException ex) {
            throw new ClassCastException("NOTE! The activity must implement the fragment's listener" +
                    " interface!");
        }
    }
}