package com.example.doit.remote;

import com.example.doit.model.Consumer;
import com.example.doit.model.QuestionPostData;

import java.util.Date;
import java.util.List;

public interface IMainRemoteDataSource {
    public void fetchQuestionsPosts(Date fromDate, Consumer<List<QuestionPostData>> consumer);
    public void removePost(String id, Runnable onFinish);
}
