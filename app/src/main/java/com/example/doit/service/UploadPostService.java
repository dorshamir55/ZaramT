package com.example.doit.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.doit.R;
import com.example.doit.model.AnswerFireStore;
import com.example.doit.model.AnswerInPost;
import com.example.doit.model.QuestionFireStore;
import com.example.doit.model.QuestionInPost;
import com.example.doit.model.QuestionPostData;
import com.example.doit.ui.OpeningScreenActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class UploadPostService extends Service
{
    private static final int ID = 1;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;

    @Override
    public void onCreate() {
        super.onCreate();
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground(ID, createNotification());

        QuestionFireStore oldQuestion = (QuestionFireStore) intent.getSerializableExtra("question");
//        Toast.makeText(getApplicationContext(), oldQuestion.getId(), Toast.LENGTH_SHORT).show();
        QuestionInPost question = new QuestionInPost(oldQuestion.getId(), oldQuestion.getEn(), oldQuestion.getHe());
        List<AnswerFireStore> oldAnswersList = (List<AnswerFireStore>) intent.getSerializableExtra("answers");
        List<AnswerInPost> answersList = new ArrayList<>();
        for(AnswerFireStore answer : oldAnswersList){
            answersList.add(new AnswerInPost(answer.getAnswerID(), answer.getEn(), answer.getHe(), new ArrayList<>()));
        }

        final QuestionPostData data = new QuestionPostData(currentUser.getUid(), question, answersList);
        postQuestion(data);
        return Service.START_NOT_STICKY;
    }

    private void postQuestion(QuestionPostData questionPostData) {
        FirebaseFirestore.getInstance().collection("posts")
                .add(questionPostData)
                .addOnSuccessListener(docRef -> {
                    LocalBroadcastManager.getInstance(getApplicationContext())
                            .sendBroadcast(new Intent("com.project.ACTION_RELOAD"));
                    stopSelf();
                })
                .addOnFailureListener(ex -> {
                    ex.printStackTrace();
                    showMessageAndFinish(getResources().getString(R.string.post_question_fail));
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
                .setSmallIcon(R.drawable.doit_icon)
                .setContentTitle(getString(R.string.posting_title))
                .setContentText(getString(R.string.posting_message))
                .setContentIntent(pi);

        return notificationBuilder.build();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
