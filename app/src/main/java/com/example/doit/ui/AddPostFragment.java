package com.example.doit.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.doit.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AddPostFragment extends Fragment {

    private Spinner categoryS, questionsS;
    private ArrayAdapter categorySpinnerAdapter, questionsSpinnerAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_post, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        categoryS = view.findViewById(R.id.choose_category_spinner);
        //questionsS = view.findViewById(R.id.choose_question_spinner);

        String[] categoryArray = getResources().getStringArray(R.array.category_list);
        List<String> categoryList = new ArrayList<>(Arrays.asList(categoryArray));
        Collections.sort(categoryList); //planetsArray will be sorted
        String chooseCategory = getResources().getString(R.string.choose_category);
        categoryList.add(0, chooseCategory);

        categorySpinnerAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, categoryList);
        categorySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categoryS.setAdapter(categorySpinnerAdapter);
    }
}