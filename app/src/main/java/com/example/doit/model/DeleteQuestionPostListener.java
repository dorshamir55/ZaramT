package com.example.doit.model;

import com.example.doit.adapter.PostsRecyclerAdapter;

public interface DeleteQuestionPostListener {
    void onDeleteQuestionPost(PostsRecyclerAdapter adapter, QuestionPostData questionPostData, int position);
    void onDecrementAmountOfChosenQuestionInQuestionPost(String questionID);
    void onDeleteQuestionPostIdFromUser(String questionPostID);
}
