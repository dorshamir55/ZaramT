package com.example.doit.ui;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.doit.R;
import com.example.doit.adapter.AnswersRecyclerAdapter;
import com.example.doit.adapter.QuestionsRecyclerAdapter;
import com.example.doit.model.LocalHelper;
import com.example.doit.model.NewAnswer;
import com.example.doit.model.NewQuestion;

import java.util.List;

public class ChooseAnswersFragment extends Fragment {
    private AnswersRecyclerAdapter adapter;
    private LocalHelper localHelper;

    private List<NewAnswer> answersList;
    private NewQuestion question;
    private TextView questionTextView;
    private CheckBox answerCheckBox;
    private int amountOfAnswers = 0;
    private final int MAX_ANSWERS = 4;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        localHelper = new LocalHelper(getActivity());
        adapter = new AnswersRecyclerAdapter(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_choose_answers, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        questionTextView = view.findViewById(R.id.selected_question);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            question = (NewQuestion) bundle.getSerializable("question");
            String questionText = null;
            if(localHelper.getLocale().equals("en"))
                questionText = question.getEn().getQuestionText();
            else if(localHelper.getLocale().equals("he"))
                questionText = question.getHe().getQuestionText();
            questionTextView.setText(questionText);
        }
        answersList = question.getAnswers();

        RecyclerView recyclerView = view.findViewById(R.id.answersRecycler);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        recyclerView.setAdapter(adapter);
        adapter.setData(answersList);
        adapter.notifyDataSetChanged();

        adapter.setRecyclerListener(new AnswersRecyclerAdapter.AnswersRecyclerListener() {
            @Override
            public void onCheckChange(int position, boolean isChecked, CompoundButton buttonView, NewAnswer clickedAnswer) {
                View view = (View) buttonView.getParent();
                if(isChecked) {
                    if(amountOfAnswers>=MAX_ANSWERS) {
                        buttonView.setChecked(false);
                        Toast.makeText(getActivity(), getResources().getString(R.string.max_answers_message), Toast.LENGTH_SHORT).show();
                    }
                    else {
                        view.setBackgroundColor(getResources().getColor(R.color.toolbarColorVeryLight));
                        amountOfAnswers++;
                    }
                }
                else{
                    view.setBackgroundColor(Color.WHITE);
                    amountOfAnswers--;
                }
            }
        });
    }
}