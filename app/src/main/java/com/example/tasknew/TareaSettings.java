package com.example.tasknew;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.ColorMatrix;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.AnyRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.UUID;

public class TareaSettings extends AppCompatActivity {

    String tarea;
    String usuario;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private Uri filePath;
    boolean conec = true;

    String server_url = "http://ec2-52-56-170-196.eu-west-2.compute.amazonaws.com/everhorst001/WEB/";
    AlertDialog.Builder builder;
    private static final String UPLOAD_URL= "http://ec2-52-56-170-196.eu-west-2.compute.amazonaws.com/everhorst001/WEB/";
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


        //SUBIR LA FOTO AL SERVIDOR
        Button botonimagen= findViewById(R.id.id_boton_imagen);
        botonimagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("img","ha entrado en el onclick");
                    //se ha hecho bien la conexión, por lo tanto podemos subir la imagen.
                    Log.i("img","ha entrado en el if de verificacion");

                    Intent intent= new Intent(TareaSettings.this, SubirImagen.class);
                intent.putExtra("tarea",tarea);
                    startActivity(intent);

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


    //CONSEGUIR LA FOTO SACADA CON LA CÁMARA DEL TELÉFONO
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        ImageView img= findViewById(R.id.id_imagen);
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==100){
            Bitmap bm=(Bitmap) data.getExtras().get("data");
            img.setImageBitmap(bm);
            filePath=data.getData();



            //SUBIR IMAGENES AL SERVIDOR
            mStorageRef= FirebaseStorage.getInstance().getReference("uploads");
            mDatabaseRef= FirebaseDatabase.getInstance().getReference("uploads");

            Button botonimagen= findViewById(R.id.id_boton_imagen);
            botonimagen.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.i("img","ha entrado en el onclick");
                    if(verificarConexion()){
                     //se ha hecho bien la conexión, por lo tanto podemos subir la imagen.
                        Log.i("img","ha entrado en el if de verificacion");
                        DBRemota dbr= new DBRemota();
                        dbr.subirImagen(bm);
                    }
                }
            });
        }

    }

    private String getPath(Uri uri){
        Cursor cursor= getContentResolver().query(uri,null,null,null,null);
        cursor.moveToFirst();
        String document_id= cursor.getString(0);

        document_id= document_id.substring(document_id.lastIndexOf(":")+1);
        cursor.close();

        cursor=getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null,
                MediaStore.Images.Media._ID+" = ? ",
                new String[]{document_id},
                null
        );
        cursor.moveToFirst();
        @SuppressLint("Range") String path= cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();
        return path;
    }




        public boolean verificarConexion(){
        //Log.i("algo","ha entrado en dbremota");

        server_url= server_url+ "pruebaConexion.php";
        builder = new AlertDialog.Builder(TareaSettings.this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, server_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
               conec=true;
                Log.i("img","holiii");
                Log.i("img1", String.valueOf(conec));
            }
        }               , new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(TareaSettings.this,"some error found .....",Toast.LENGTH_SHORT).show();
                error.printStackTrace();
                conec=false;

            }
        });

        DBRemotaHelper.getInstance(TareaSettings.this).addTorequestque(stringRequest);
            Log.i("img", String.valueOf(conec));
        return conec;
    }


}



