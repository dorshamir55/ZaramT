package com.example.doit.model;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class PieChartHelper {
    private int sumOfVotes = 0;
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
        votePercentages = new ArrayList<>();
        for(AnswerInPost answerInPost : answersInPost) {
            if(sumOfVotes == 0)
                votePercentages.add(Float.valueOf(0));
            else
                votePercentages.add(Float.valueOf (answerInPost.getVotedUserIdList().size()*100/sumOfVotes));
//            Log.d("TAG", votePercentages.get(votePercentages.size()-1).toString());
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
