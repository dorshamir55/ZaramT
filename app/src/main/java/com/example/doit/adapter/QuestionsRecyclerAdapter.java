package com.example.doit.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doit.R;
import com.example.doit.model.LocalHelper;
import com.example.doit.model.QuestionFireStore;

import java.util.ArrayList;
import java.util.List;

public class QuestionsRecyclerAdapter extends RecyclerView.Adapter<QuestionsRecyclerAdapter.RecyclerViewHolder> implements Filterable {

    @Nullable
    private List<QuestionFireStore> listData, listDataFull;
    private QuestionsRecyclerListener listener;
    private LocalHelper localHelper;
    private Activity activity;
    private String currentLanguage;

    public QuestionsRecyclerAdapter(Activity activity) {
        this.activity = activity;
        this.localHelper = new LocalHelper(activity);
        this.currentLanguage = localHelper.getLocale();
    }

    public void setData(List<QuestionFireStore> data) {
        if(data!=null) {
            listData = new ArrayList<>(data);
            listDataFull = new ArrayList<QuestionFireStore>(data);
        }
    }

    public List<QuestionFireStore> getData() {
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


        holder.question.setText(listData.get(position).getTextByLanguage(currentLanguage));
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
        void onItemClick(int position, View clickedView, QuestionFireStore clickedQuestion);
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    private Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<QuestionFireStore> filterList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filterList.addAll(listDataFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (QuestionFireStore questionFireStore : listDataFull) {
                    if (questionFireStore.getTextByLanguage(currentLanguage).toLowerCase().contains(filterPattern)) {
                        filterList.add(questionFireStore);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filterList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if(listData != null){
                listData.clear();
                listData.addAll((List)results.values);
                notifyDataSetChanged();
            }
        }
    };
}
