package com.example.doit.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.doit.R;
import com.example.doit.adapter.AnswersRecyclerAdapter;
import com.example.doit.model.AnswerFireStore;
import com.example.doit.model.BackButtonListener;
import com.example.doit.model.Consumer;
import com.example.doit.model.LocalHelper;
import com.example.doit.model.AnswerInQuestion;
import com.example.doit.model.QuestionFireStore;
import com.example.doit.service.UploadPostService;
import com.example.doit.viewmodel.IMainViewModel;
import com.example.doit.viewmodel.MainViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ChooseAnswersFragment extends Fragment {
    private IMainViewModel viewModel;
    private AnswersRecyclerAdapter adapter;
    private LocalHelper localHelper;
    private FragmentManager manager;
    private BackButtonListener backButtonListener;

    private List<AnswerInQuestion> answersIDList;
    private List<AnswerFireStore> checkedAnswersList;
    private QuestionFireStore question;
    private Spinner hoursS, minutesS;
    private TextView questionTextView;
    private CheckBox answerCheckBox;
    private Button uploadButton;
    private String currentLanguage;
    private int amountOfAnswers = 0;
    private final int MAX_ANSWERS = 4;
    private final int HOURS_LIMIT = 25;
    private final int MINUTES_LIMIT = 60;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);
        localHelper = new LocalHelper(getActivity());
        adapter = new AnswersRecyclerAdapter(getActivity());
        localHelper = new LocalHelper(getActivity());
        this.currentLanguage = localHelper.getLocale();
        manager = getParentFragmentManager();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_choose_answers, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        questionTextView = view.findViewById(R.id.selected_question);
        hoursS = view.findViewById(R.id.choose_hours_spinner);
        minutesS = view.findViewById(R.id.choose_minutes_spinner);
        uploadButton = view.findViewById(R.id.upload_button);

        @RequiresApi(api = Build.VERSION_CODES.N)
        class HoursNumbers
                implements Supplier<Integer> {
            private int i = 0;
            public Integer get() { return i++; }
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        class MinutesNumbers
                implements Supplier<Integer> {
            private int i = 0;
            public Integer get() { return i++; }
        }

        List<Integer> hoursList = Stream.generate(new HoursNumbers()).limit(HOURS_LIMIT).collect(Collectors.toList());
        List<Integer> minutesList = Stream.generate(new MinutesNumbers()).limit(MINUTES_LIMIT).collect(Collectors.toList());
        List<Integer> zeroArray = new ArrayList<>(1);
        zeroArray.add(0);

        ArrayAdapter hoursSpinnerAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, hoursList);
        hoursSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        hoursS.setAdapter(hoursSpinnerAdapter);

        ArrayAdapter minutesOnlyZeroAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, zeroArray);
        ArrayAdapter minutesSpinnerAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, minutesList);
        minutesSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        minutesS.setAdapter(minutesSpinnerAdapter);

        hoursS.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 24){
                    minutesS.setAdapter(minutesOnlyZeroAdapter);
                    minutesS.setEnabled(false);
                }
                else{
                    minutesS.setEnabled(true);
                    minutesS.setAdapter(minutesSpinnerAdapter);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        checkedAnswersList = new ArrayList<>();

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            question = (QuestionFireStore) bundle.getSerializable("question");
            String questionText = question.getTextByLanguage(currentLanguage);
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

                int hours, minutes;
                hours = Integer.parseInt(hoursS.getSelectedItem().toString());
                minutes = Integer.parseInt(minutesS.getSelectedItem().toString());

                if(hours == 0 && minutes == 0){
                    Toast.makeText(getActivity(), getResources().getString(R.string.not_enough_time_message), Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent(getActivity(), UploadPostService.class);
//                Bundle bundleToService = new Bundle();
//                bundleToService.putSerializable("question", question);
//                bundleToService.putParcelable("answers", (Parcelable) checkedAnswersList);
                intent.putExtra("question", question);
                intent.putExtra("answers", (ArrayList<AnswerFireStore>)checkedAnswersList);
                intent.putExtra("hours", hours);
                intent.putExtra("minutes", minutes);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    getActivity().startForegroundService(intent);

                else
                    getActivity().startService(intent);

                for(int i = 0; i < manager.getBackStackEntryCount(); ++i) {
                    manager.popBackStack();
                }
                manager.beginTransaction().replace(R.id.nav_host_fragment, new HomeFragment()).commit();
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

    @Override
    public void onResume() {
        super.onResume();

        backButtonListener.onBackButtonClickListener(true);
    }

    @Override
    public void onPause() {
        super.onPause();

        backButtonListener.onBackButtonClickListener(false);
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