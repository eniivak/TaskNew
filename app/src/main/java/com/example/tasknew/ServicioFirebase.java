package com.example.tasknew;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class ServicioFirebase extends FirebaseMessagingService {

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onMessageReceived( RemoteMessage remoteMessage) {
        if (remoteMessage.getData().size() > 0) {
            if (remoteMessage.getNotification() != null) {
                final String CHANNEL_ID= "HEADS_UP_NOTIFICATION";
                Log.i("fcm ", "recived");
                //Toast.makeText(getApplicationContext(),"FCM message",Toast.LENGTH_SHORT).show();
                NotificationManager elManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                NotificationCompat.Builder elBuilder = new NotificationCompat.Builder(this, CHANNEL_ID);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel elCanal = new NotificationChannel(CHANNEL_ID, "mssg",
                            NotificationManager.IMPORTANCE_HIGH);
                    elManager.createNotificationChannel(elCanal);
                    elCanal.setDescription("Firebase messages");
                    elCanal.enableLights(true);
                    elCanal.setLightColor(Color.RED);
                    elCanal.setVibrationPattern(new long[]{0, 1000, 500, 1000});
                    elCanal.enableVibration(true);
                }
                elBuilder.setSmallIcon(android.R.drawable.stat_sys_warning)
                        .setContentTitle("Has subido una foto nueva al servidor!")
                        .setContentText("Foto de la tarea: "+remoteMessage.getNotification().getBody().toString())
                        .setSubText(remoteMessage.getNotification().getBody())
                        .setVibrate(new long[]{0, 1000, 500, 1000})
                        .setAutoCancel(true);
                elManager.notify(1, elBuilder.build());

            }
        }
    }


   /* public String generarToken(){
        token= FirebaseInstanceId.getInstance().getToken();
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>(){
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task){

                if (task.isSuccessful()){
                    token = task.getResult().getToken();
                    Log.i("Token", "ondo "+token);
                }
                else{
                    Log.i("Token", "error");
                }

            }
        });
        return  token;
    }*/
}
