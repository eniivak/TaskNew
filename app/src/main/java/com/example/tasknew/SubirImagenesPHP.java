package com.example.tasknew;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PipedReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class SubirImagenesPHP extends AsyncTask<String,Void,String> {
    Context context;
    String result;
    private ProgressDialog pd;

    public SubirImagenesPHP(Context ctx){
        this.context=ctx;
        this.pd= new ProgressDialog(ctx);
    }

    protected void onPreExecute() {
        super.onPreExecute();
        pd.setMessage("Wait image uploading!");
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
        String result="";
        String link="http://ec2-52-56-170-196.eu-west-2.compute.amazonaws.com/everhorst001/WEB/subirimagen.php";
        try {
            URL url= new URL(link);
            HttpURLConnection http= (HttpURLConnection) url.openConnection();
            http.setRequestMethod("POST");
            http.setDoInput(true);;
            http.setDoOutput(true);

            Log.i("el titulo",titulo);
            Log.i("la imagen",imagen);
            OutputStream ops= http.getOutputStream();
            BufferedWriter writer= new BufferedWriter( new OutputStreamWriter(ops,"UTF-8"));
            String data= URLEncoder.encode("titulo","UTF-8")+"="+URLEncoder.encode(titulo,"UTF-8")
                    +"&&"+ URLEncoder.encode("img","UTF-8")+"="+URLEncoder.encode(imagen,"UTF-8");
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
            Log.i("el resultado en subirimagenes al servidor",result);
            if(result.equals("true")){
                Log.i("php","se supone que se ha subido");
                //dejarEntrar();
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
