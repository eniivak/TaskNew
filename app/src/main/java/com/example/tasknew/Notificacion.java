package com.example.tasknew;

import android.app.Notification;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Notificacion extends AppCompatActivity {
    private NotificationUtils mNotificationUtils;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notificacion);
        miDB gestorDB = new miDB(Notificacion.this);
        Bundle extras= getIntent().getExtras();
       String tarea=extras.getString("tarea");

        mNotificationUtils = new NotificationUtils(this);

        final EditText editTextTitleAndroid = (EditText) findViewById(R.id.et_android_title);
        final EditText editTextAuthorAndroid = (EditText) findViewById(R.id.et_android_author);
        Button buttonAndroid = (Button) findViewById(R.id.btn_send_android);


        //LA FECHA DE HOY
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(" dd / M / yyyy");
        String currentDate = simpleDateFormat.format(calendar.getTime());


        //LA FECHA DE LA TAREA
        String fechaTarea=" "+gestorDB.tieneFecha(tarea);

        Log.i("1","la fecha de la tarea"+fechaTarea);
        Log.i("1","la fecha de hoy"+currentDate);
        buttonAndroid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = editTextTitleAndroid.getText().toString();
                String author = editTextAuthorAndroid.getText().toString();

                if(!TextUtils.isEmpty(title) && !TextUtils.isEmpty(author)) {
                    if(fechaTarea.equals(currentDate)){
                        Notification.Builder nb = mNotificationUtils.
                                getAndroidChannelNotification(title, "By " + author);

                        mNotificationUtils.getManager().notify(101, nb.build());
                    }

                }
            }
        });
    }
}
