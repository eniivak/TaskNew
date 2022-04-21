package com.example.tasknew;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.Context;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.UUID;

public class TareaSettings extends AppCompatActivity {

    String tarea;
    String usuario;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private Uri filePath;

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





        //Request for camera runtime permission
        if(ContextCompat.checkSelfPermission(TareaSettings.this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(TareaSettings.this,new String[]{
                    Manifest.permission.CAMERA},100);

        }

        //ABRIR LA CÁMARA PARA SACAR FOTOS
        Button camarabot= findViewById(R.id.id_boton_camara);
        camarabot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,100);
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

/*
    private String getFileExtension(Uri uri){
        ContentResolver cr= getContentResolver();
        MimeTypeMap mime= MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }
    private void subirArchivo() throws FileNotFoundException {
        ImageView img= findViewById(R.id.id_imagen);
        int id= img.getId();
        //img.setImageURI(Uri.fromFile(new File("/home/ena/AndroidStudioProjects/TaskNew/app/src/main/res/ena.jpg")));
       //Uri uri= getUriToDrawable(TareaSettings.this,id); //"@android:drawable/btn_star_big_on" ;
        Uri uri= Uri.fromFile(new File("/home/ena/AndroidStudioProjects/TaskNew/app/src/main/res/ena.jpg"));
        if(uri!=null){
            StorageReference fileRef= mStorageRef.child(System.currentTimeMillis() + "."+ getFileExtension(uri));
            fileRef.putFile(uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(TareaSettings.this, "Upload successful", Toast.LENGTH_LONG).show();

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(TareaSettings.this, e.getMessage()+"ERRORES VARIOS"+uri.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        else{
            Toast.makeText(this,"No file selected",Toast.LENGTH_SHORT).show();
        }
    }

    public static final Uri getUriToDrawable(@NonNull Context context, @AnyRes int drawableId) {
        Uri imageUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                + "://" + context.getResources().getResourcePackageName(drawableId)
                + '/' + context.getResources().getResourceTypeName(drawableId)
                + '/' + context.getResources().getResourceEntryName(drawableId) );
        return imageUri;
    }

*/

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
                    subirImagen(bm);
                    Log.d("algo","algo");
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

    private void subirImagen(Bitmap foto){
        String name= "cualquiera"; //mas tarde definir mejor
        String path= getPath(filePath);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        foto.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] fototransformada = stream.toByteArray();
        String fotoen64 = Base64.encodeToString(fototransformada,Base64.DEFAULT);


        Uri.Builder builder = new Uri.Builder()
                .appendQueryParameter("identificador", UUID.randomUUID().toString())
                .appendQueryParameter("imagen", fotoen64)
                .appendQueryParameter("titulo", "algo");
        String parametrosURL = builder.build().getEncodedQuery();
        
    }
}
