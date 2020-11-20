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
import com.example.doit.model.QuestionFireStore;
import com.example.doit.model.QuestionInPost;
import com.example.doit.model.QuestionPostData;
import com.example.doit.model.UserData;
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

    @Override
    public void onCreate() {
        super.onCreate();
        db = FirebaseFirestore.getInstance();
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

        final QuestionPostData data = new QuestionPostData(currentUser.getUid(), question, answersList, endDate);
        postQuestion(data);
        return Service.START_NOT_STICKY;
    }

    private void postQuestion(QuestionPostData questionPostData) {

        FirebaseFirestore.getInstance().collection("posts")
                .add(questionPostData)
                .addOnSuccessListener(docRef -> {
                    LocalBroadcastManager.getInstance(getApplicationContext())
                            .sendBroadcast(new Intent("com.project.ACTION_RELOAD"));

                    incrementQuestionChoice(questionPostData.getQuestion().getQuestionID());

//                    final FirebaseDatabase database = FirebaseDatabase.getInstance();
//                    DatabaseReference ref = database.getReference("server/saving-data/fireblog");
//                    DatabaseReference timeRef = ref.child("time_left_posts");
//
//                    Map<String, String> timeLeft = new HashMap<>();
//                    timeLeft.put(docRef.getId(), "<!DOCTYPE HTML><html><head><meta name=\"viewport\" content=\"width=device-width, initial-scale=1\"<style></style></head><body><p id=\"demo\"></p><script>var countDownDate = new Date(\"Jan 5, 2021 15:37:25\").getTime();var x = setInterval(function() {  var now = new Date().getTime();  var distance = countDownDate - now;  var days = Math.floor(distance / (1000 * 60 * 60 * 24));  var hours = Math.floor((distance % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));  var minutes = Math.floor((distance % (1000 * 60 * 60)) / (1000 * 60));  var seconds = Math.floor((distance % (1000 * 60)) / 1000);  document.getElementById(\"demo\").innerHTML = days + \"d \" + hours + \"h \"  + minutes + \"m \" + seconds + \"s \";  if (distance < 0) {    clearInterval(x);    document.getElementById(\"demo\").innerHTML = \"EXPIRED\";  }}, 1000);</script></body></html>");
//
//                    timeRef.setValue(timeLeft);
                    stopSelf();
                })
                .addOnFailureListener(ex -> {
                    ex.printStackTrace();
                    showMessageAndFinish(getResources().getString(R.string.post_question_fail));
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
                .setSmallIcon(R.drawable.doit_icon)
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
