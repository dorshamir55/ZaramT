package com.example.doit.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.doit.R;
import com.example.doit.adapter.AnswersRecyclerAdapter;
import com.example.doit.model.AnswerFireStore;
import com.example.doit.model.Consumer;
import com.example.doit.model.LocalHelper;
import com.example.doit.model.AnswerInQuestion;
import com.example.doit.model.QuestionFireStore;
import com.example.doit.service.UploadPostService;
import com.example.doit.viewmodel.IMainViewModel;
import com.example.doit.viewmodel.MainViewModel;

import java.util.ArrayList;
import java.util.List;

public class ChooseAnswersFragment extends Fragment {
    private IMainViewModel viewModel;
    private AnswersRecyclerAdapter adapter;
    private LocalHelper localHelper;

    private List<AnswerInQuestion> answersIDList;
    private List<AnswerFireStore> checkedAnswersList;
    private QuestionFireStore question;
    private TextView questionTextView;
    private CheckBox answerCheckBox;
    private Button uploadButton;
    private int amountOfAnswers = 0;
    private final int MAX_ANSWERS = 4;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);
        localHelper = new LocalHelper(getActivity());
        adapter = new AnswersRecyclerAdapter(getActivity());
        localHelper = new LocalHelper(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_choose_answers, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        questionTextView = view.findViewById(R.id.selected_question);
        uploadButton = view.findViewById(R.id.upload_button);
        checkedAnswersList = new ArrayList<>();

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            question = (QuestionFireStore) bundle.getSerializable("question");
            String questionText = null;
            
            if(localHelper.getLocale().equals("en"))
                questionText = question.getEn().getQuestionText();
            else if(localHelper.getLocale().equals("he"))
                questionText = question.getHe().getQuestionText();
            questionTextView.setText(questionText);
        }
        answersIDList = question.getAnswersInQuestion();

        RecyclerView recyclerView = view.findViewById(R.id.answersRecycler);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        recyclerView.setAdapter(adapter);

        Consumer<List<AnswerFireStore>> consumerList = new Consumer<List<AnswerFireStore>>() {
            @Override
            public void apply(List<AnswerFireStore> answerList) {
                adapter.setData(answerList);
                adapter.notifyDataSetChanged();
            }
        };
        viewModel.getListOfAnswers(consumerList, answersIDList);

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(amountOfAnswers !=4){
                    Toast.makeText(getActivity(), getResources().getString(R.string.valid_answers_message), Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent(getActivity(), UploadPostService.class);
//                Bundle bundleToService = new Bundle();
//                bundleToService.putSerializable("question", question);
//                bundleToService.putParcelable("answers", (Parcelable) checkedAnswersList);
                intent.putExtra("question", question);
                intent.putExtra("answers", (ArrayList<AnswerFireStore>)checkedAnswersList);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    getActivity().startForegroundService(intent);
                else
                    getActivity().startService(intent);

                getParentFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, new HomeFragment()).commit();
            }
        });

        adapter.setRecyclerListener(new AnswersRecyclerAdapter.AnswersRecyclerListener() {
            @Override
            public void onCheckChange(int position, boolean isChecked, CompoundButton buttonView, AnswerFireStore clickedAnswer) {
                View view = (View) buttonView.getParent();
                if(isChecked) {
                    if(amountOfAnswers>=MAX_ANSWERS) {
                        buttonView.setChecked(false);
                        Toast.makeText(getActivity(), getResources().getString(R.string.max_answers_message), Toast.LENGTH_SHORT).show();
                    }
                    else {
                        view.setBackgroundColor(getResources().getColor(R.color.toolbarColorVeryLight));
                        checkedAnswersList.add(clickedAnswer);
                        amountOfAnswers++;
                    }
                }
                else{
                    view.setBackgroundColor(Color.WHITE);
                    checkedAnswersList.remove(clickedAnswer);
                    amountOfAnswers--;
                }
            }
        });
    }
}