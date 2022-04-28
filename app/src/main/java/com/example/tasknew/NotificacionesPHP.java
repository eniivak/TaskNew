package com.example.tasknew;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class NotificacionesPHP extends AsyncTask<String,Void,String> {
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
                        Log.i("el resultado de subir notificacion",result);
                        Log.i("mssg", result);
                    }


                }
                catch (MalformedURLException e) {e.printStackTrace();}
                catch (IOException e) {e.printStackTrace();}

                return result;
            }

        }


