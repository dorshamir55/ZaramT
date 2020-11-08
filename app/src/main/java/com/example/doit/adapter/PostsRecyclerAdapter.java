package com.example.doit.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doit.R;
import com.example.doit.model.AnswerInPost;
import com.example.doit.model.PercentFormatter;
import com.example.doit.model.PieChartHelper;
import com.example.doit.model.LocalHelper;
import com.example.doit.model.QuestionPostData;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class PostsRecyclerAdapter extends RecyclerView.Adapter<PostsRecyclerAdapter.RecyclerViewHolder> {
    private FirebaseAuth auth;
    private FirebaseUser currentUser;

    @Nullable
    private List<QuestionPostData> listData;
    private PostsRecyclerListener listener;
    private LocalHelper localHelper;
    private Activity activity;

    public PostsRecyclerAdapter(Activity activity) {
        this.activity = activity;
        this.localHelper = new LocalHelper(activity);
        this.auth = FirebaseAuth.getInstance();
        this.currentUser = auth.getCurrentUser();
    }

    public void setData(List<QuestionPostData> data) {
        listData = data;
    }

    public List<QuestionPostData> getData() {
        return listData;
    }

    public void setRecyclerListener(PostsRecyclerListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_card_content, parent, false);
        return new RecyclerViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    //Will be call for every item..
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
        assert listData != null;
        holder.nickname.setText(currentUser.getDisplayName());
        holder.nickname.setText("Lionel Messi");
        if(localHelper.getLocale().equals("en")) {
            holder.question.setText(listData.get(position).getQuestion().getEn().getQuestionText());
            if(!alreadyVoted(position) && !isItMyPost(position)) {
                holder.answer1.setText(listData.get(position).getAnswers().get(0).getEn().getAnswerText());
                holder.answer2.setText(listData.get(position).getAnswers().get(1).getEn().getAnswerText());
                holder.answer3.setText(listData.get(position).getAnswers().get(2).getEn().getAnswerText());
                holder.answer4.setText(listData.get(position).getAnswers().get(3).getEn().getAnswerText());
            }
            else{
                //Show statistics
                showResults(holder, position);
            }
        }
        else if(localHelper.getLocale().equals("he")){
            holder.question.setText(listData.get(position).getQuestion().getHe().getQuestionText());
            if(!alreadyVoted(position) && !isItMyPost(position)) {
                holder.answer1.setText(listData.get(position).getAnswers().get(0).getHe().getAnswerText());
                holder.answer2.setText(listData.get(position).getAnswers().get(1).getHe().getAnswerText());
                holder.answer3.setText(listData.get(position).getAnswers().get(2).getHe().getAnswerText());
                holder.answer4.setText(listData.get(position).getAnswers().get(3).getHe().getAnswerText());
            }
            else{
                //Show statistics
                showResults(holder, position);
            }
        }

//        if(listData.get(position).getImagesURL() != null) {
//            Glide.with(holder.imageView.getContext())
//                    .load(listData.get(position).getImagesURL().get(0))
//                    .into(holder.imageView);
//        } else {
//            holder.imageView.setImageResource(R.mipmap.dog_profile);
//        }
    }

    private boolean isItMyPost(int position) {
//        return listData.get(position).getPostedUserId().equals(currentUser.getUid());
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void showResults(RecyclerViewHolder holder, int position) {
        int i=0;
        holder.answersAndVote.setVisibility(View.GONE);
        PieChartHelper pieChartHelper = new PieChartHelper(listData.get(position).getAnswers());
        List<PieEntry> pieEntries = new ArrayList<>();

        for(AnswerInPost answerInPost : listData.get(position).getAnswers()){
            if(localHelper.getLocale().equals("en"))
                pieEntries.add(new PieEntry(pieChartHelper.getVotePercentages().get(i), answerInPost.getEn().getAnswerText()));
            else if(localHelper.getLocale().equals("he"))
                pieEntries.add(new PieEntry(pieChartHelper.getVotePercentages().get(i), answerInPost.getHe().getAnswerText()));
            i++;
        }

        PieDataSet pieDataSet = new PieDataSet(pieEntries, "");
        int[] pieColors = new int[4];
        pieColors[0] = activity.getResources().getColor(R.color.toolbarColor2);
        pieColors[1] = activity.getResources().getColor(R.color.toolbarColor);
        pieColors[2] = activity.getResources().getColor(R.color.toolbarColorLight);
        pieColors[3] = activity.getResources().getColor(R.color.toolbarColorVeryLight);
//        pieDataSet.setColors(ColorTemplate.createColors(pieColors));
        pieDataSet.setColors(ColorTemplate.MATERIAL_COLORS);

        PieData pieData = new PieData(pieDataSet);
        pieData.setValueFormatter(new PercentFormatter());

        holder.pieChart.setData(pieData);
        holder.pieChart.setUsePercentValues(true);
        holder.pieChart.setVisibility(View.VISIBLE);
        holder.pieChart.animateXY(5000,5000);
        holder.pieChart.setEntryLabelColor(Color.BLACK);
        holder.pieChart.setEntryLabelTextSize(8.5f);
        holder.pieChart.setRotationAngle(45);

        Legend legend = holder.pieChart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        if(localHelper.getLocale().equals("en"))
            legend.setDirection(Legend.LegendDirection.LEFT_TO_RIGHT);
        else if(localHelper.getLocale().equals("he"))
            legend.setDirection(Legend.LegendDirection.RIGHT_TO_LEFT);
        legend.setWordWrapEnabled(true);

        Description description = new Description();
        String descriptionString = activity.getResources().getString(R.string.voted) + pieChartHelper.getSumOfVotes();
        description.setText(descriptionString);
        holder.pieChart.setDescription(description);
//        holder.pieChart.notifyDataSetChanged();
        holder.pieChart.invalidate();
    }

    private boolean alreadyVoted(int position) {
        for(AnswerInPost answerInPost : listData.get(position).getAnswers()){
            if(answerInPost.getVotedUserIdList().contains(currentUser.getUid()))
                return true;
        }
        return false;
    }

    @Override
    public int getItemCount() {
        return listData == null ? 0 : listData.size();
    }

    class RecyclerViewHolder extends RecyclerView.ViewHolder
    {
        private LinearLayout answersAndVote;
        private PieChart pieChart;
        private ImageView imageNickName, imageOptions;
        private TextView nickname;
        private TextView question;
        private RadioGroup answersGroup;
        private RadioButton answer1, answer2, answer3, answer4;
        private Button voteButton;
        private MenuItem deleteItem;

        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            pieChart = itemView.findViewById(R.id.barChar);
            answersAndVote = itemView.findViewById(R.id.answers_and_vote_layout);
            imageNickName = itemView.findViewById(R.id.image_nickname_cell_post);
            imageOptions = itemView.findViewById(R.id.options_image_cell_post);
            nickname = itemView.findViewById(R.id.nickname_cell_post);
            question = itemView.findViewById(R.id.question_cell_post);
            answersGroup = itemView.findViewById(R.id.options_cell_post);
            answer1 = itemView.findViewById(R.id.answer1_cell_post);
            answer2 = itemView.findViewById(R.id.answer2_cell_post);
            answer3 = itemView.findViewById(R.id.answer3_cell_post);
            answer4 = itemView.findViewById(R.id.answer4_cell_post);
            voteButton = itemView.findViewById(R.id.vote_cell_post);

            itemView.setOnClickListener(view -> {
                if(listener != null) {
                    assert listData != null;
                    listener.onItemClick(getAdapterPosition(), view, listData.get(getAdapterPosition()));
                }
            });

            voteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener != null) {
                        assert listData != null;
                        answersAndVote.setVisibility(View.GONE);
                        listener.onVoteClick(getAdapterPosition(), view,  listData.get(getAdapterPosition()), answersGroup.getCheckedRadioButtonId());
                        //showResults(holder, position);
                    }
                }
            });

            imageOptions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener != null) {
                        assert listData != null;
                        PopupMenu popupMenu = new PopupMenu(view.getContext(), imageOptions);
                        ((Activity)view.getContext()).getMenuInflater().inflate(R.menu.menu_post, popupMenu.getMenu());

                        popupMenu.show();

                        deleteItem = popupMenu.getMenu().findItem(R.id.action_delete);
                        deleteItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                listener.onDeleteClick(getAdapterPosition(), item, listData.get(getAdapterPosition()));
                                return false;
                            }
                        });
                    }
                }
            });
        }
    }

    public static interface PostsRecyclerListener {
        void onItemClick(int position, View clickedView, QuestionPostData clickedPost);
        void onVoteClick(int position, View clickedView, QuestionPostData clickedPost, int votedRadiobuttonID);
        void onDeleteClick(int position, MenuItem item, QuestionPostData clickedPost);
    }
}
