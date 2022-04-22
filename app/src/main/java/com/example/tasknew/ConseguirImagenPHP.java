package com.example.tasknew;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class ConseguirImagenPHP extends AsyncTask<String,Void,String> {
    @Override
    protected String doInBackground(String... strings) {
        String result=conseguirImagen(strings[0]);
        return result;
    }


    private String conseguirImagen(String titulo){
        String result="";
        String link="http://ec2-52-56-170-196.eu-west-2.compute.amazonaws.com/everhorst001/WEB/conseguirimagen.php";
        try {
            URL url= new URL(link);
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
}
