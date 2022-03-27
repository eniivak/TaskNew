package com.example.tasknew;

import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.ColorMatrix;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

public class TareaSettings extends AppCompatActivity {

    String tarea;
    String usuario;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //INICIALIZAR
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tarea_settings);
        miDB gestorDB = new miDB(TareaSettings.this);
        Bundle extras= getIntent().getExtras();
        tarea=extras.getString("tarea");
        usuario=extras.getString("usuario");
        TareaModel tm= new TareaModel(tarea,usuario);

        Log.i("ha entrado" , " en la clase TareaSettings");
        //Log.i("el nombre de la tarea", extras.getString("tarea"));


        TextView nomtarea= findViewById(R.id.id_textview_nomtarea);
        //para poder editar el texto del ViewText es encesario poner TextView.BufferType.EDITABLE
        nomtarea.setText(extras.getString("tarea"),TextView.BufferType.EDITABLE); //mostrar el nombre de la tarea

        EditText descrip= findViewById(R.id.id_descrip);
        try {
            String des=gestorDB.tieneDes(tm) ;
            if (!des.equals("")) { //comprobar si la tarea tiene descripcion
                //para poder editar el texto del EditText es encesario poner TextView.BufferType.EDITABLE
                descrip.setText(des +"\n " +"\n\n\n"+
                        "(pulsa para editar y el botón guardar al finalizar)", TextView.BufferType.EDITABLE); //si es así, se verá en el panel, sino se pondrá el hint
            }
        }
        catch(Exception e){
            Log.i("info","no hay descripción en la tarea");
        }


        TextView fecha= findViewById(R.id.text_fecha);
        try {
            String f= gestorDB.tieneFecha(tarea);
            if(!f.equals("")){
                fecha.setText(f,TextView.BufferType.EDITABLE);
                Log.i("fecha","ha entrado"+f);
            }
        } catch(Exception e){
            Log.i("info","no hay fecha en la tarea");
        }



        //BORRAR TAREA
        ImageButton botonborrar= findViewById(R.id.imageButton);
        botonborrar.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                TareaModel tm= new TareaModel(extras.getString("tarea"), extras.getString("usuario"));
                gestorDB.borrarTarea(tm);
                Intent intent= new Intent(TareaSettings.this, MainActivity.class); //para que cuando borres la tarea directamente te lleve al panel de las tareas
                startActivity(intent);

                NotificationUtils mNotificationUtils = new NotificationUtils(TareaSettings.this);
                Notification.Builder nb = mNotificationUtils.
                        getAndroidChannelNotification("Has borrado la tarea ", tarea);

                mNotificationUtils.getManager().notify(101, nb.build());
            }
        });


        //GUARDAR DESCRIPCIÓN DE LA TAREA
        Button guardardesc= findViewById(R.id.id_botonguardar_desc);
        guardardesc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //coger el texto de la descripción y meterlo en la base de datos
                EditText descrip= findViewById(R.id.id_descrip);

                tm.setDescrip(descrip.getText().toString());
                gestorDB.guardarDesc(tm);
            }
        });


        //VOLVER AL PANEL DE LAS TAREAS
        Button botonvolver= findViewById(R.id.id_boton_volver);
        botonvolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(TareaSettings.this, MainActivity.class);
                startActivity(intent);

            }
        });


        //GESTIONAR EL DIÁLOGO DE LA FECHA
        Button botonfecha= findViewById(R.id.boton_fecha);
        botonfecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Please note that use your package name here
                showDatePickerDialog(gestorDB);
            }
        });



    }

    private void showDatePickerDialog(miDB db) {
        ClaseDialogoFecha newFragment = ClaseDialogoFecha.newInstance(new DatePickerDialog.OnDateSetListener(){
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                //añadir fecha a BD
                db.guardarFecha(tarea,year,month,day);
                TextView tfecha= findViewById(R.id.text_fecha);
                // month+1 porque enero es 0
                final String selectedDate = day + " / " + (month+1) + " / " + year;
                tfecha.setText(selectedDate,TextView.BufferType.EDITABLE);
                Log.i("la fecha",selectedDate);
            }
        });

        newFragment.show(getSupportFragmentManager(), "datePicker");
    }



}
