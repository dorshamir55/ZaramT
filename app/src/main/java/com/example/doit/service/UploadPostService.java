package com.example.doit.service;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.doit.R;
import com.example.doit.model.AnswerFireStore;
import com.example.doit.model.AnswerInPost;
import com.example.doit.model.EndPostNotification;
import com.example.doit.model.QuestionFireStore;
import com.example.doit.model.QuestionInPost;
import com.example.doit.model.QuestionPostData;
import com.example.doit.model.UserData;
import com.example.doit.ui.HomeFragment;
import com.example.doit.ui.MainActivity;
import com.example.doit.ui.OpeningScreenActivity;
import com.example.doit.viewmodel.IMainViewModel;
import com.example.doit.viewmodel.MainViewModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UploadPostService extends Service
{
    private static final int ID = 1;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    private QuestionPostData data;
    private String fcmToken = null;
    private List<String> postedQuestionPostsIdList;

    @Override
    public void onCreate() {
        super.onCreate();
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();

        MyFirebaseMessagingService.requestFCMToken()
                .addOnSuccessListener(instanceIdResult -> {
                    fcmToken = instanceIdResult.getToken();
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground(ID, createNotification());

        QuestionFireStore oldQuestion = (QuestionFireStore) intent.getSerializableExtra("question");
        QuestionInPost question = new QuestionInPost(oldQuestion.getId(), oldQuestion.getEn(), oldQuestion.getHe());
        List<AnswerFireStore> oldAnswersList = (List<AnswerFireStore>) intent.getSerializableExtra("answers");
        List<AnswerInPost> answersList = new ArrayList<>();
        for(AnswerFireStore answer : oldAnswersList){
            AnswerInPost answerInPost = new AnswerInPost(answer.getId(), answer.getEn(), answer.getHe(), new ArrayList<>());
            answersList.add(answerInPost);
        }

        int hours = intent.getIntExtra("hours", 1);
        int minutes = intent.getIntExtra("minutes", 0);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(Calendar.getInstance().getTime());
        calendar.add(Calendar.HOUR, hours);
        calendar.add(Calendar.MINUTE, minutes);
        Date endDate = calendar.getTime();

        Log.d("TAG", endDate.toString());

        data = new QuestionPostData(currentUser.getUid(), question, answersList, endDate);
        postQuestion(data);
        return Service.START_NOT_STICKY;
    }

    private void postQuestion(QuestionPostData questionPostData) {

        db.collection("posts").add(questionPostData)
                .addOnSuccessListener(docRef -> {
                    LocalBroadcastManager.getInstance(getApplicationContext())
                            .sendBroadcast(new Intent("com.project.ACTION_RELOAD"));
                    docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {

                            if(currentUser == null || fcmToken == null) {
                                return;
                            }

                            String questionText = questionPostData.getQuestion().getTextByLanguage("he");
                            String postedUserId = questionPostData.getPostedUserId();
                            String questionPostId = docRef.getId();
                            Date endingPostDate = questionPostData.getEndingPostDate();

                            Map<String, Object> data = new HashMap<>();
                            data.put("fcmToken", fcmToken);
                            data.put("postedUserId", postedUserId);
                            data.put("questionText", questionText);
                            data.put("questionPostId", questionPostId);
                            data.put("endingPostDate", endingPostDate);

//                            EndPostNotification endPostNotification = new EndPostNotification(postedUserId, questionText, questionPostData.getEndingPostDate());
//                            db.collection("notifications").document(docRef.getId()).set(endPostNotification)
                            db.collection("notifications").document(docRef.getId()).set(data)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                        }
                                    });
                            incrementQuestionChoice(questionPostData.getQuestion().getQuestionID());
                            updateUserQuestionPostsIdList(documentSnapshot.getId(), questionPostData.getPostedUserId());
                        }
                    });

                    stopSelf();
                })
                .addOnFailureListener(ex -> {
                    ex.printStackTrace();
                    showMessageAndFinish(getResources().getString(R.string.post_question_fail));
                });
    }

    private void updateUserQuestionPostsIdList(String questionPostID, String userID) {
        db.collection(UserData.TABLE_NAME).document(userID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                List<String> postedQuestionPostsIdList = documentSnapshot.toObject(UserData.class).getPostedQuestionPostsIdList();

                Map<String, Object> data = new HashMap<>();
                postedQuestionPostsIdList.add(questionPostID);
                data.put("postedQuestionPostsIdList", postedQuestionPostsIdList);
                db.collection(UserData.TABLE_NAME).document(userID).set(data, SetOptions.merge())
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
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    private void incrementQuestionChoice(String questionID) {
        FirebaseFirestore.getInstance().collection(QuestionFireStore.TABLE_NAME).document(questionID)
                .update("amountOfChoices", FieldValue.increment(1))
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

    private void showMessageAndFinish(String msg) {
        try {
            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            stopSelf();
        } catch(Throwable t) {
            t.printStackTrace();
        }
    }

    private Notification createNotification() {
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "com.project.service.UploadNotifID";
        if(Build.VERSION.SDK_INT >= 26) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                    getResources().getString(R.string.channel_name), NotificationManager.IMPORTANCE_HIGH);

            // Configure the notification channel.
            notificationChannel.setDescription(getResources().getString(R.string.channel_name));
            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this,
                NOTIFICATION_CHANNEL_ID);

        // Create notification action intent..
        Intent notificationIntent = new Intent(getApplicationContext(), OpeningScreenActivity.class);
        PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, 0);

        notificationBuilder.setAutoCancel(false)
                .setDefaults(Notification.DEFAULT_ALL)
                .setSmallIcon(R.drawable.ic_app)
                .setContentTitle(getResources().getString(R.string.posting_title))
                .setContentText(getResources().getString(R.string.posting_message))
                .setContentIntent(pi);

        return notificationBuilder.build();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
