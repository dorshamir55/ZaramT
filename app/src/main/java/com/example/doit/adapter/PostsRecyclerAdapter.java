package com.example.doit.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.renderscript.Script;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.PopupMenu;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doit.R;
import com.example.doit.model.AnswerInPost;
import com.example.doit.model.PercentFormatter;
import com.example.doit.model.PieChartHelper;
import com.example.doit.model.LocalHelper;
import com.example.doit.model.QuestionPostData;
import com.example.doit.viewmodel.IMainViewModel;
import com.example.doit.viewmodel.MainViewModel;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class PostsRecyclerAdapter extends RecyclerView.Adapter<PostsRecyclerAdapter.RecyclerViewHolder> {
    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference("server/saving-data/fireblog");
    DatabaseReference timeRef = ref.child("time_left_posts");
    private double NO_VOTES = 0.0;

    @Nullable
    private List<QuestionPostData> listData;
    private PostsRecyclerListener listener;
    private LocalHelper localHelper;
    private PieChartHelper pieChartHelper;
    private Activity activity;
    private String currentLanguage;

    public PostsRecyclerAdapter(Activity activity) {
        this.activity = activity;
        this.localHelper = new LocalHelper(activity);
        this.currentLanguage = localHelper.getLocale();
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

    @SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface"})
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    //Will be call for every item..
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
        assert listData != null;

        pieChartHelper = new PieChartHelper(listData.get(position).getAnswers());

//        String hours = String.valueOf(listData.get(position).getUpdateDate().getHours());
//        String minutes = String.valueOf(listData.get(position).getUpdateDate().getMinutes());
//        String seconds = String.valueOf(listData.get(position).getUpdateDate().getSeconds());
//        String date = hours+":"+minutes+":"+seconds;
//        holder.nickname.setText(date);
//        holder.nickname.setText(listData.get(position).getEndingPostDate().toString());

        holder.webView.requestFocus();
        holder.webView.getSettings().setJavaScriptEnabled(true);
        holder.webView.getSettings().setGeolocationEnabled(true);
        WebAppInterface webAppInterface = new WebAppInterface(activity);
        webAppInterface.storeText("working", "123");
        holder.webView.addJavascriptInterface(webAppInterface, "Android");

        String postEnding = listData.get(position).getEndingPostDate().toString();
        String hoursText, minutesText, secondsText, closed;
        hoursText = activity.getResources().getString(R.string.hours);
        minutesText = activity.getResources().getString(R.string.minutes);
        secondsText = activity.getResources().getString(R.string.seconds);
        closed = activity.getResources().getString(R.string.closed);
        if(listData.get(position).isPostTimeOver()){
            String script = "<!DOCTYPE HTML><html><head><meta name=\"viewport\" content=\"width=device-width, initial-scale=1\"<style></style></head><body><p id=\"demo\" style=\"margin:0px; font-size:50%;\"></p><script>document.getElementById(\"demo\").innerHTML = \""+closed+"\" </script></body></html>";
            holder.webView.loadData(script, "text/html", "UTF-8");
        }
        else {
            String script = "<!DOCTYPE HTML><html><head><meta name=\"viewport\" content=\"width=device-width, initial-scale=1\"<style></style></head><body><p id=\"demo\" style=\"margin:0px; font-size:50%;\"></p><script>function saveFunction(text) {   Android.storeText(text,\"" + listData.get(position).getId() + "\");   }</script><script> var countDownDate = new Date(\"" + postEnding + "\").getTime();var x = setInterval(function() {  var now = new Date().getTime();  var distance = countDownDate - now;  var hours = Math.floor((distance % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));  var minutes = Math.floor((distance % (1000 * 60 * 60)) / (1000 * 60));  var seconds = Math.floor((distance % (1000 * 60)) / 1000);  document.getElementById(\"demo\").innerHTML = hours + \" " + hoursText + " \"  + minutes + \" " + minutesText + " \" + seconds + \" " + secondsText + " \";  if (distance < 0) {    clearInterval(x);    document.getElementById(\"demo\").innerHTML = \"" + closed + "\";    saveFunction(\"" + closed + "\");  }}, 1000);</script></body></html>";
            holder.webView.loadData(script, "text/html", "UTF-8");
        }
//        timeRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                String time = dataSnapshot.getValue().toString();
//                holder.webView.loadData(time, "text/html", "UTF-8");
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });

        holder.nickname.setText(listData.get(position).getPostedUserId());
        holder.question.setText(listData.get(position).getQuestion().getTextByLanguage(currentLanguage));

        if(listData.get(position).isPostTimeOver()){
            if(pieChartHelper.getSumOfVotes() == 0) {
                holder.answersAndVote.setVisibility(View.GONE);
                holder.pieChart.setVisibility(View.GONE);
                holder.noVotes.setVisibility(View.VISIBLE);
            }
            else{
                showResults(holder, position);
            }
        }
        else if(pieChartHelper.getSumOfVotes() == 0 && isItMyPost(position)){
            holder.answersAndVote.setVisibility(View.GONE);
            holder.pieChart.setVisibility(View.GONE);
            holder.noVotes.setVisibility(View.VISIBLE);
        }
        else if(!alreadyVoted(position) && !isItMyPost(position)) {
            holder.answersAndVote.setVisibility(View.VISIBLE);
            holder.pieChart.setVisibility(View.GONE);
            holder.noVotes.setVisibility(View.GONE);
            holder.answer1.setText(listData.get(position).getAnswers().get(0).getTextByLanguage(currentLanguage));
            holder.answer2.setText(listData.get(position).getAnswers().get(1).getTextByLanguage(currentLanguage));
            holder.answer3.setText(listData.get(position).getAnswers().get(2).getTextByLanguage(currentLanguage));
            holder.answer4.setText(listData.get(position).getAnswers().get(3).getTextByLanguage(currentLanguage));
        }
        else{
            //Show statistics
            showResults(holder, position);
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
        return listData.get(position).getPostedUserId().equals(currentUser.getUid());
//        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void showResults(RecyclerViewHolder holder, int position) {
        int i=0;
        holder.answersAndVote.setVisibility(View.GONE);
        holder.noVotes.setVisibility(View.GONE);

        List<PieEntry> pieEntries = new ArrayList<>();

        for(AnswerInPost answerInPost : listData.get(position).getAnswers()){

            pieEntries.add(new PieEntry(pieChartHelper.getVotePercentages().get(i),
                    pieChartHelper.getVotePercentages().get(i) != (float) NO_VOTES ?
                            answerInPost.getTextByLanguage(currentLanguage) : ""));
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
        List<LegendEntry> entries = new ArrayList<>();
        ArrayList<PieEntry> yValues = new ArrayList<>();
        i=0;
        for(AnswerInPost answerInPost : listData.get(position).getAnswers()) {
            yValues.add(new PieEntry(pieChartHelper.getVotePercentages().get(i), answerInPost.getTextByLanguage(currentLanguage)));
            i++;
        }

        for (int j = 0; j < 4; j++) {
            LegendEntry entry = new LegendEntry();
            entry.formColor = ColorTemplate.MATERIAL_COLORS[j];
            entry.label = yValues.get(j).getLabel() ;
            entries.add(entry);
        }
        legend.setCustom(entries);

        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        if(currentLanguage.equals("en"))
            legend.setDirection(Legend.LegendDirection.LEFT_TO_RIGHT);
        else if(currentLanguage.equals("he"))
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
        private WebView webView;
        private ImageView imageNickName, imageOptions;
        private TextView nickname, question, noVotes;
        private RadioGroup answersGroup;
        private RadioButton answer1, answer2, answer3, answer4;
        private Button voteButton;
        private MenuItem deleteItem;

        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            pieChart = itemView.findViewById(R.id.barChar);
            webView = itemView.findViewById(R.id.time_left);
            answersAndVote = itemView.findViewById(R.id.answers_and_vote_layout);
            imageNickName = itemView.findViewById(R.id.image_nickname_cell_post);
            imageOptions = itemView.findViewById(R.id.options_image_cell_post);
            nickname = itemView.findViewById(R.id.nickname_cell_post);
            question = itemView.findViewById(R.id.question_cell_post);
            noVotes = itemView.findViewById(R.id.no_votes);
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
                        if(!isItMyPost(getAdapterPosition()))
                            deleteItem.setVisible(false);
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

    public class WebAppInterface
    {
        Context mContext;
        String data;

        /** Instantiate the interface and set the context */
        WebAppInterface(Context c)
        {
            mContext = c;
        }

        @JavascriptInterface
        public void storeText(String text, String id)
        {
            String closed = activity.getResources().getString(R.string.closed);
            this.data=text;
            if(data.equals(closed)){
                //TODO: Post time is over...
                IMainViewModel viewModel = new ViewModelProvider((ViewModelStoreOwner) activity).get(MainViewModel.class);
                viewModel.postTimeEnd(id);
//                Toast.makeText(activity, text, Toast.LENGTH_LONG).show();
            }
        }
    }
}
