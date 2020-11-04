package com.example.doit.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doit.R;
import com.example.doit.model.LocalHelper;
import com.example.doit.model.NewQuestion;
import com.example.doit.model.QuestionPostData;

import java.util.ArrayList;
import java.util.List;

public class QuestionsRecyclerAdapter extends RecyclerView.Adapter<QuestionsRecyclerAdapter.RecyclerViewHolder> implements Filterable {

    @Nullable
    private List<NewQuestion> listData, listDataFull;
    private QuestionsRecyclerListener listener;
    private LocalHelper localHelper;
    private Activity activity;

    public QuestionsRecyclerAdapter(Activity activity) {
        this.activity = activity;
        this.localHelper = new LocalHelper(activity);
    }

    public void setData(List<NewQuestion> data) {
        listData = data;
        listDataFull = new ArrayList<NewQuestion>(data);
    }

    public List<NewQuestion> getData() {
        return listData;
    }

    public void setRecyclerListener(QuestionsRecyclerListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.question_card_content, parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    //Will be call for every item..
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
        assert listData != null;

        if(localHelper.getLocale().equals("he"))
            holder.question.setText(listData.get(position).getHe().getQuestionText());
        else if(localHelper.getLocale().equals("en"))
            holder.question.setText(listData.get(position).getEn().getQuestionText());
//        if(listData.get(position).getImagesURL() != null) {
//            Glide.with(holder.imageView.getContext())
//                    .load(listData.get(position).getImagesURL().get(0))
//                    .into(holder.imageView);
//        } else {
//            holder.imageView.setImageResource(R.mipmap.dog_profile);
//        }
    }

    @Override
    public int getItemCount() {
        return listData == null ? 0 : listData.size();
    }


    class RecyclerViewHolder extends RecyclerView.ViewHolder
    {
        private TextView question;

        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            question = itemView.findViewById(R.id.question_cell_question);

            itemView.setOnClickListener(view -> {
                if(listener != null) {
                    assert listData != null;
                    listener.onItemClick(getAdapterPosition(), view, listData.get(getAdapterPosition()));
                }
            });
        }
    }

    public static interface QuestionsRecyclerListener {
        void onItemClick(int position, View clickedView, NewQuestion clickedQuestion);
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    private Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<NewQuestion> filterList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filterList.addAll(listData);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (NewQuestion newQuestion : listDataFull) {
                    if (localHelper.getLocale().equals("en")) {
                        if (newQuestion.getEn().getQuestionText().toLowerCase().contains(filterPattern)) {
                            filterList.add(newQuestion);
                        }
                    } else if (localHelper.getLocale().equals("he")) {
                        if (newQuestion.getHe().getQuestionText().toLowerCase().contains(filterPattern)) {
                            filterList.add(newQuestion);
                        }
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filterList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            listData.clear();
            listData.addAll((List)results.values);
            notifyDataSetChanged();
        }
    };
}
