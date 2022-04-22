package com.example.tasknew;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.HttpResponse;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.NameValuePair;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.HttpClient;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.entity.UrlEncodedFormEntity;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.methods.HttpPost;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.impl.client.DefaultHttpClient;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.message.BasicNameValuePair;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.util.EntityUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class SubirImagen extends Activity {

    Button camarabot, btnup;
    String picturePath;
    String ba1;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    ImageView img;
    String tarea;

    public static String URL = "http://ec2-52-56-170-196.eu-west-2.compute.amazonaws.com/everhorst001/WEB/subirimagen.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.subir_imagenes);
        Bundle extras= getIntent().getExtras();
        tarea = extras.getString("tarea");

        //Request for camera runtime permission
        if(ContextCompat.checkSelfPermission(SubirImagen.this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(SubirImagen.this,new String[]{
                    Manifest.permission.CAMERA},100);

        }

        //ABRIR LA CÁMARA PARA SACAR FOTOS
        camarabot= findViewById(R.id.id_boton_camara);
        camarabot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
                }
            }
        });

        //SUBIR FOTOS AL SERVIDOR
        btnup = (Button) findViewById(R.id.up);
        btnup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String titulo= tarea;
                String imagen= picturePath;
                SubirImagenesPHP subirImagenesPHP= new SubirImagenesPHP(SubirImagen.this);
                subirImagenesPHP.execute(titulo,imagen);

            }
        });

        Button volver= findViewById(R.id.boton_volversettings);
        volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(SubirImagen.this, TareaSettings.class);
                intent.putExtra("tarea",tarea);
                startActivity(intent);
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

       img = findViewById(R.id.Imageprev);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            img.setImageBitmap(imageBitmap);

            String imageFileName =tarea;
            imageFileName.concat(imageFileName);
            File f = (getExternalFilesDir(Environment.DIRECTORY_PICTURES));

            try {
                File image = File.createTempFile(
                        "img",  /* prefix */
                        ".jpg",         /* suffix */
                        f      /* directory */
                );
                picturePath = image.getName();
                Log.i("imagen nueva",picturePath);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }


    }
}
