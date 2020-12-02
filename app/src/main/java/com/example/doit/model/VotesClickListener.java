package com.example.doit.model;

import java.util.List;

public interface VotesClickListener {
    void onVote(String questionPostId, String currentUserId, List<AnswerInPost> answersList, int answerVoted);
}
