package com.example.doit.remote;

import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.doit.model.AnswerFireStore;
import com.example.doit.model.AnswerInPost;
import com.example.doit.model.AnswerInQuestion;
import com.example.doit.model.Consumer;
import com.example.doit.model.QuestionFireStore;
import com.example.doit.model.QuestionPostData;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainRemoteDataSource implements IMainRemoteDataSource {

    private FirebaseFirestore db;

    public MainRemoteDataSource() {
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public void fetchQuestionsPosts(Date fromDate, Consumer<List<QuestionPostData>> consumer) {
        Query query = db.collection(QuestionPostData.TABLE_NAME).orderBy("updateDate", Query.Direction.DESCENDING);

        if(fromDate != null)
            query = query.whereGreaterThan("updateDate", fromDate);

        query.get()
                .addOnCompleteListener(task -> {
                    List<QuestionPostData> data = null;
                    if (task.isSuccessful()) {
                        data = new ArrayList<>();
                        for (DocumentSnapshot document : task.getResult().getDocuments()) {
                            //ArrayList<Answer> answers = new ArrayList<>();
                            //ArrayList<Answer> answers = (ArrayList<Answer>) document.get("answers");
                            //Log.d(TAG, list.toString());
                            data.add(document.toObject(QuestionPostData.class).withId(document.getId()));
                            //data.get(data.size()-1).setAnswers(answers);
                        }
                    } else {
                        task.getException().printStackTrace();
                    }

                    if(consumer != null)
                        consumer.apply(data);
                });
    }

    @Override
    public void removePost(String id, Runnable onFinish) {
        Map<String, Object> data = new HashMap<>();
        data.put("updateDate", FieldValue.serverTimestamp());
        data.put("removed", true);
        db.collection(QuestionPostData.TABLE_NAME).document(id).set(data, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        if(onFinish != null)
                            onFinish.run();
                    }
                });
    }

    @Override
    public void fetchAllQuestions(Consumer<List<QuestionFireStore>> consumerList) {
        Query query = db.collection(QuestionFireStore.TABLE_NAME);
        query.get()
                .addOnCompleteListener(task -> {
                    List<QuestionFireStore> data = null;
                    if (task.isSuccessful()) {
                        data = new ArrayList<>();
                        for (DocumentSnapshot document : task.getResult().getDocuments()) {
                            data.add(document.toObject(QuestionFireStore.class).withId(document.getId()));
                        }
                    } else {
                        task.getException().printStackTrace();
                    }

                    if(consumerList != null)
                        consumerList.apply(data);
                });
    }

    @Override
    public void fetchAnswers(Consumer<List<AnswerFireStore>> consumerList, List<AnswerInQuestion> answerInQuestions) {
        Query query = db.collection(AnswerFireStore.TABLE_NAME);
        query.get()
                .addOnCompleteListener(task -> {
                    List<AnswerFireStore> data = null;
                    if (task.isSuccessful()) {
                        data = new ArrayList<>();
                        List<DocumentSnapshot> documentSnapshots = task.getResult().getDocuments();
                        if(answerInQuestions != null) {
                            for (AnswerInQuestion answer : answerInQuestions) {
                                for (DocumentSnapshot document : task.getResult().getDocuments())
                                    if (document.getId().equals(answer.getAnswerID()))
                                        data.add(document.toObject(AnswerFireStore.class).withId(document.getId()));
                            }
                        }
                    } else {
                        task.getException().printStackTrace();
                    }

                    if(consumerList != null)
                        consumerList.apply(data);
                });
    }

    @Override
    public void updateVotes(String id, String currentUserId, List<AnswerInPost> answersInPost, int votedPosition, Runnable onFinish) {
        Map<String, Object> data = new HashMap<>();
        List<String> votedList = answersInPost.get(votedPosition).getVotedUserIdList();
        votedList.add(currentUserId);
        answersInPost.get(votedPosition).setVotedUserIdList(votedList);
        data.put("updateDate", FieldValue.serverTimestamp());
        data.put("voted", true);
        data.put("answers", answersInPost);
        db.collection(QuestionPostData.TABLE_NAME).document(id).set(data, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        if(onFinish != null)
                            onFinish.run();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    @Override
    public void changeUpdatedToFalse(String id) {
        Map<String, Object> data = new HashMap<>();
        data.put("voted", false);
        db.collection(QuestionPostData.TABLE_NAME).document(id).set(data, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }
}
