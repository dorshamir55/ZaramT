package com.example.doit.remote;

import com.example.doit.model.Consumer;
import com.example.doit.model.QuestionPostData;
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
                            data.add(document.toObject(QuestionPostData.class).withId(document.getId()));
                        }
                    } else {
                        task.getException().printStackTrace();
                    }

                    if(consumer != null)
                        consumer.apply(data);
                });
    }

    @Override
    public void removeAd(String id, Runnable onFinish) {
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
}
