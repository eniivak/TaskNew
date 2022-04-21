package com.example.tasknew;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DBRemota extends AppCompatActivity {

    Button button;
    EditText Name , Email;
    String server_url = "http://ec2-52-56-170-196.eu-west-2.compute.amazonaws.com/everhorst001/WEB/";
    AlertDialog.Builder builder;
    String parametros;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);







/*
        button = (Button) findViewById(R.id.bn);
        Name = (EditText) findViewById(R.id.name1);
        Email = (EditText) findViewById(R.id.email1);
        builder = new AlertDialog.Builder(DBRemota.this);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name , email ;
                name =Name.getText().toString();
                email=Email.getText().toString();
                StringRequest stringRequest = new StringRequest(Request.Method.POST, server_url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        builder.setTitle("Server Response");
                        builder.setMessage("Response :"+response);
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Name.setText("");
                                Email.setText("");
                            }
                        });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();

                    }
                }

                        , new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(DBRemota.this,"some error found .....",Toast.LENGTH_SHORT).show();
                        error.printStackTrace();

                    }
                }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map <String,String> Params = new HashMap<String, String>();
                        Params.put("name",name);
                        Params.put("email",email);
                        return Params;

                    }
                };
                DBRemotaHelper.getInstance(DBRemota.this).addTorequestque(stringRequest);
            }
        });*/

    }


    public void verificarConexion(){
        Log.i("algo","ha entrado en dbremota");
        server_url= server_url+ "pruebaConexion.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, server_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                builder.setTitle("Server Response");
                builder.setMessage("Response :"+response);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Name.setText("");
                        Email.setText("");
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

            }
        }

                , new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(DBRemota.this,"some error found .....",Toast.LENGTH_SHORT).show();
                error.printStackTrace();

            }
        });

        DBRemotaHelper.getInstance(DBRemota.this).addTorequestque(stringRequest);
    }

    public void subirImagen(Bitmap foto) {
        Log.i("img","ha entrado en imagen");
        server_url = server_url + "subirimagen.php";
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        foto.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] fototransformada = stream.toByteArray();
        String fotoen64 = Base64.encodeToString(fototransformada, Base64.DEFAULT);

        Uri.Builder builder = new Uri.Builder()
                .appendQueryParameter("identificador", UUID.randomUUID().toString())
                .appendQueryParameter("imagen", fotoen64)
                .appendQueryParameter("titulo", "algo");
        String parametrosURL = builder.build().getEncodedQuery();

        HttpURLConnection urlConnection = null;
        try {
            Log.i("img","ha entrado en el try de imagen");
            URL destino = new URL(server_url);
            urlConnection = (HttpURLConnection) destino.openConnection();
            urlConnection.setConnectTimeout(5000);
            urlConnection.setReadTimeout(5000);

            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");


            String parametros = "identificador="+UUID.randomUUID().toString()+"&imagen="+fotoen64+"&titulo="+"algo";
            PrintWriter out = new PrintWriter(urlConnection.getOutputStream());
            out.print(parametros);
            out.close();

            int statusCode = urlConnection.getResponseCode();

            if (statusCode == 200) {
                Log.i("img","ha entrado en el status 200 de la imagen");
                BufferedInputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                String line, result = "";
                while ((line = bufferedReader.readLine()) != null) {
                    result += line;
                }
                inputStream.close();

            }
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
