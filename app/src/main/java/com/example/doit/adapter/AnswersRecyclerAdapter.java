package com.example.doit.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doit.R;
import com.example.doit.model.AnswerFireStore;
import com.example.doit.model.LocalHelper;
import com.example.doit.model.AnswerInQuestion;

import java.util.ArrayList;
import java.util.List;

public class AnswersRecyclerAdapter extends RecyclerView.Adapter<AnswersRecyclerAdapter.RecyclerViewHolder> {


    @Nullable
    private List<AnswerFireStore> listData;
    private AnswersRecyclerListener listener;
    private LocalHelper localHelper;
    private Activity activity;

    public AnswersRecyclerAdapter(Activity activity) {
        this.activity = activity;
        this.localHelper = new LocalHelper(activity);
    }

    public void setData(List<AnswerFireStore> data) {
        if(data!=null)
            listData = new ArrayList<>(data);
    }

    public List<AnswerFireStore> getData() {
        return listData;
    }

    public void setRecyclerListener(AnswersRecyclerListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.answer_card_content, parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    //Will be call for every item..
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
        assert listData != null;

        if(localHelper.getLocale().equals("he"))
            holder.answer.setText(listData.get(position).getHe().getAnswerText());
        else if(localHelper.getLocale().equals("en"))
            holder.answer.setText(listData.get(position).getEn().getAnswerText());
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
        TextView answer;
        CheckBox checkBox;

        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            answer = itemView.findViewById(R.id.answer_cell_answer);
            checkBox = itemView.findViewById(R.id.answer_checkBox);

            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(listener != null) {
                        assert listData != null;
                        listener.onCheckChange(getAdapterPosition(), isChecked, buttonView, listData.get(getAdapterPosition()));
                    }
                }
            });
        }
    }

    public static interface AnswersRecyclerListener {
        //void onItemClick(int position, View clickedView, NewAnswer clickedAnswer);
        void onCheckChange(int position, boolean isChecked,CompoundButton buttonView, AnswerFireStore clickedAnswer);
    }

}
