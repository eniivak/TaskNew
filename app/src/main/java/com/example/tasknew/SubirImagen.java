package com.example.tasknew;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.base.Utf8;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.HttpResponse;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.NameValuePair;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.HttpClient;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.entity.UrlEncodedFormEntity;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.methods.HttpPost;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.impl.client.DefaultHttpClient;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.message.BasicNameValuePair;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.util.EntityUtils;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.Console;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.net.ssl.HttpsURLConnection;

public class SubirImagen extends Activity {

    Button camarabot, btnup;
    String picturePath,tarea,usuario;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    ImageView img;
    private String result="" ;
    Bitmap imageBitmap;
    private StorageReference mStorageRef;
    private FirebaseStorage mFireStorage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.subir_imagenes);
        Bundle extras= getIntent().getExtras();
        tarea = extras.getString("tarea");
        usuario= extras.getString("usuario");
        Log.i("desde subir imagen: ",usuario);
        img = findViewById(R.id.Imageprev);

        //COMPROBAR SI YA HAY UNA FOTO
        conseguirImagendelServidor();


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
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                String titulo= tarea;
                String imagen= picturePath;
                SubirImagenesPHP subirImagenesPHP= new SubirImagenesPHP(SubirImagen.this);
                //Log.i("encoded",encode(imageBitmap));
                subirImagenesPHP.execute(titulo,encode(imageBitmap));
                lanzarNotificacion();
                Log.i("id",result);
            }
        });

        //VOLVER A LOS SETTINGS
        Button volver= findViewById(R.id.boton_volversettings);
        volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(SubirImagen.this, TareaSettings.class);
                intent.putExtra("tarea",tarea);
                intent.putExtra("usuario",usuario);

                startActivity(intent);
            }
        });
    }
    private String encode(Bitmap img){

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        img.compress(Bitmap.CompressFormat.JPEG, 50, baos); //bm is the bitmap object
        byte[] b = baos.toByteArray();
        String encodedImage = Base64.encodeToString(b , Base64.URL_SAFE);
        return encodedImage;
    }
    public void uploadImage(Bitmap bm){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        mStorageRef = FirebaseStorage.getInstance().getReference("tareas/"+tarea);
        mStorageRef.putBytes(data)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK ) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
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
                //Uri uri=Uri.fromFile(new File(picturePath));
                uploadImage(imageBitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void conseguirImagendelServidor(){
        class ConseguirImagenPHP extends AsyncTask<String,Void,String> {
            @Override
            protected String doInBackground(String... strings) {
                conseguirImagen(strings[0]);
                try {
                    mostrarImagen();

                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
                return result;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                result=s;
                Log.i("desde conseguirimafenphp",result);
                if(!result.equals("false")){
                    img.setImageBitmap(imageBitmap);
                }

            }
            private String conseguirImagen(String titulo){
                String link="http://ec2-52-56-170-196.eu-west-2.compute.amazonaws.com/everhorst001/WEB/conseguirimagen.php";
                try {
                    java.net.URL url= new URL(link);
                    HttpURLConnection http= (HttpURLConnection) url.openConnection();
                    http.setRequestMethod("POST");
                    http.setDoInput(true);;
                    http.setDoOutput(true);

                    Log.i("el titulo",titulo);
                    OutputStream ops= http.getOutputStream();
                    BufferedWriter writer= new BufferedWriter( new OutputStreamWriter(ops,"UTF-8"));
                    String data= URLEncoder.encode("titulo","UTF-8")+"="+URLEncoder.encode(titulo,"UTF-8");
                    writer.write(data);
                    writer.flush();
                    writer.close();

                    ops.close();

                    InputStream ips= http.getInputStream();
                    BufferedReader reader= new BufferedReader(new InputStreamReader(ips,"ISO-8859-1"));
                    String line="";
                    while((line=reader.readLine())!=null){
                        result+=line;
                    }

                    reader.close();
                    ips.close();
                    http.disconnect();
                    Log.i("el resultado en conseguirimagenes del servidor",result);
                    if(result.equals("true")){
                        Log.i("php","se supone que se ha subido");
                    }
                    return result;

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return result;
            }
            private void mostrarImagen() throws IOException, JSONException {
                if (!result.equals("false")){
                    byte[] bytes= Base64.decode(result,Base64.URL_SAFE);
                    imageBitmap= BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                    Log.i("bitmap", String.valueOf(imageBitmap));
                    if(imageBitmap==null){
                        Uri uri = Uri.fromFile(new File(result));
                        imageBitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
                    }
                    Log.i("bitmap", String.valueOf(imageBitmap));
                }

            }
        }
        ConseguirImagenPHP conseguirImagenPHP= new ConseguirImagenPHP();
        conseguirImagenPHP.execute(tarea);
    }

private void lanzarNotificacion(){
     class NotificacionPHP extends AsyncTask<String,Void,String> {
        Context context;
        String result;


        protected void onPreExecute() {
            super.onPreExecute();
        }


        @Override
        protected String doInBackground(String... strings) {
            result="";
            String titulo= strings[0];
            String mensaje= strings[1];


            result= subirNotificacion(titulo,mensaje);

            return result;
        }
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }



        private String subirNotificacion(String titulo, String mensaje){
            String link="http://ec2-52-56-170-196.eu-west-2.compute.amazonaws.com/everhorst001/WEB/notificacion.php";
            HttpURLConnection urlConnection = null;
            try
            {
                URL destino = new URL(link);
                urlConnection = (HttpURLConnection) destino.openConnection();
                urlConnection.setConnectTimeout(5000);
                urlConnection.setReadTimeout(5000);
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoOutput(true);
                urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                Log.i("el mensaje",mensaje);
                String parametros = "&titulo="+titulo+"&men="+mensaje;
                Log.i("el  titulo", titulo);
                PrintWriter out = new PrintWriter(urlConnection.getOutputStream());
                out.print(parametros);
                out.close();
                // Log.i("insert","urlconn: " + urlConnection);
                int statusCode = urlConnection.getResponseCode();
                //Log.i("insert ","statusCode: " + statusCode);
                if (statusCode == 200)
                {
                    BufferedInputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                    String line="";
                    while ((line = bufferedReader.readLine()) != null) {
                        result += line;

                    }
                    inputStream.close();
                    Log.i("el resultado de subir notificacion",result);
                }


            }
            catch (MalformedURLException e) {e.printStackTrace();}
            catch (IOException e) {e.printStackTrace();}

            return result;
        }

    }
    NotificacionPHP notificacionPHP= new NotificacionPHP();
    notificacionPHP.execute("has borrado la tarea: ",tarea);

    }

}
