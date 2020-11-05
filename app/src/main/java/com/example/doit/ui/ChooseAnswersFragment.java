package com.example.doit.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.doit.R;
import com.example.doit.model.LocalHelper;
import com.example.doit.model.NewQuestion;

public class ChooseAnswersFragment extends Fragment {
    LocalHelper localHelper;
    TextView textView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        localHelper = new LocalHelper(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_choose_answers, container, false);
        textView = view.findViewById(R.id.selected_question);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            NewQuestion question = (NewQuestion) bundle.getSerializable("question");
            String questionText = null;
            if(localHelper.getLocale().equals("en"))
                questionText = question.getEn().getQuestionText();
            else if(localHelper.getLocale().equals("he"))
                questionText = question.getHe().getQuestionText();
            textView.setText(questionText);
        }
        return view;
    }
}