package com.example.doit.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.doit.R;
import com.example.doit.model.QuestionPostData;

import java.util.List;

public class PostsRecyclerAdapter extends RecyclerView.Adapter<PostsRecyclerAdapter.RecyclerViewHolder> {

    @Nullable
    private List<QuestionPostData> listData;
    private PostsRecyclerListener listener;

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

    @Override
    //Will be call for every item..
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
        assert listData != null;
        holder.nickname.setText("Cristiano Ronaldo");
        holder.question.setText(listData.get(position).getQuestion().getQuestionText());
//        holder.answers.setText(listData.get(position).getAnswers().get(0).getAnswerText());

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
        private ImageView imageView;
        private TextView nickname;
        private TextView question;
        private TextView answers;

        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_cell);
            nickname = itemView.findViewById(R.id.nickname_cell);
            question = itemView.findViewById(R.id.question_cell);
            answers = itemView.findViewById(R.id.answers_cell);

            itemView.setOnClickListener(view -> {
                if(listener != null) {
                    assert listData != null;
                    listener.onItemClick(getAdapterPosition(), view, listData.get(getAdapterPosition()));
                }
            });
        }
    }

    public static interface PostsRecyclerListener {
        void onItemClick(int position, View clickedView, QuestionPostData clickedPost);
    }

}
