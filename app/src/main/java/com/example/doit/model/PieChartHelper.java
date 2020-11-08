package com.example.doit.model;

import java.util.ArrayList;
import java.util.List;

public class PieChartHelper {
    private int sumOfVotes;
    private List<Float> votePercentages;
    private List<AnswerInPost> answersInPost;

    public PieChartHelper(List<AnswerInPost> answersInPost) {
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
            votePercentages.add(Float.valueOf (answerInPost.getVotedUserIdList().size()/sumOfVotes*100));
            i++;
        }
    }

    public List<Float> getVotePercentages() {
        return votePercentages;
    }

    public void setVotePercentages(List<Float> voteAverages) {
        this.votePercentages = voteAverages;
    }

    public List<AnswerInPost> getAnswersInPost() {
        return answersInPost;
    }

    public void setAnswersInPost(List<AnswerInPost> answersInPost) {
        this.answersInPost = answersInPost;
    }

    public int getSumOfVotes() {
        return sumOfVotes;
    }

    public void setSumOfVotes(int sumOfVotes) {
        this.sumOfVotes = sumOfVotes;
    }
}
