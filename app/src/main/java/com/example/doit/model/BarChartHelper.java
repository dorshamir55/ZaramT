package com.example.doit.model;

import java.util.ArrayList;
import java.util.List;

public class BarChartHelper {
    private int sumOfVotes;
    private List<Double> votePercentages;
    private List<AnswerInPost> answersInPost;

    public BarChartHelper(List<AnswerInPost> answersInPost) {
        this.answersInPost = answersInPost;
        calculateSumOfVotesOfAnswers();
        calculateVoteAveragesOfAnswers();
    }

    private void calculateSumOfVotesOfAnswers() {
        for(AnswerInPost answerInPost : answersInPost)
            sumOfVotes+=answerInPost.getVotedUserIdList().size();
    }

    private void calculateVoteAveragesOfAnswers() {
        int i=0;
        votePercentages = new ArrayList<>();
        for(AnswerInPost answerInPost : answersInPost) {
            votePercentages.add((double) (answerInPost.getVotedUserIdList().size()/sumOfVotes*100));
            i++;
        }
    }

    public List<Double> getVotePercentages() {
        return votePercentages;
    }

    public void setVotePercentages(List<Double> voteAverages) {
        this.votePercentages = voteAverages;
    }

    public List<AnswerInPost> getAnswersInPost() {
        return answersInPost;
    }

    public void setAnswersInPost(List<AnswerInPost> answersInPost) {
        this.answersInPost = answersInPost;
    }
}
