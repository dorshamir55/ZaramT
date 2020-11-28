package com.example.doit.model;

import com.example.doit.adapter.PostsRecyclerAdapter;

public interface DeleteQuestionPostListener {
    public void onDeleteQuestionPost(PostsRecyclerAdapter adapter, QuestionPostData questionPostData, int position);
    public void onDecrementAmountOfChosenQuestionInQuestionPost(String questionID);
    public void onDeleteQuestionPostIdFromUser(String questionPostID);
}
