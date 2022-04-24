package com.example.tasknew;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PipedReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class SubirImagenesPHP extends AsyncTask<String,Bitmap,String> {
    Context context;
    String result;
    private ProgressDialog pd;

    public SubirImagenesPHP(Context ctx){
        this.context=ctx;
        this.pd= new ProgressDialog(ctx);

    }

    protected void onPreExecute() {
        super.onPreExecute();
        pd.setMessage("Subiendo...");
        pd.show();
    }


    @Override
    protected String doInBackground(String... strings) {
        result="";

        String titulo= strings[0];
        String imagen= strings[1];


        result=subirImagen(titulo,imagen);

        return result;
    }
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        pd.hide();
        pd.dismiss();
    }



    private String subirImagen(String titulo, String imagen){
        String link="http://ec2-52-56-170-196.eu-west-2.compute.amazonaws.com/everhorst001/WEB/subirimagen.php";
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
            Log.i("la imagen",imagen);
            String parametros = "&titulo="+titulo+"&img="+imagen;
            Log.i("la imagen", imagen);
            PrintWriter out = new PrintWriter(urlConnection.getOutputStream());
            out.print(parametros);
            out.close();
            Log.i("insert","statusCode: " + urlConnection);
            int statusCode = urlConnection.getResponseCode();
            Log.i("insert ","statusCode: " + statusCode);
            if (statusCode == 200)
            {
                BufferedInputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                String line="";
                while ((line = bufferedReader.readLine()) != null) {
                    result += line;
                }
                inputStream.close();
                Log.i("el resultado de subir foto",result);
            }


        }
        catch (MalformedURLException e) {e.printStackTrace();}
        catch (IOException e) {e.printStackTrace();}

        return result;
    }

}

