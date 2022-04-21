package com.example.tasknew;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

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

public class RegistrarUsuarioPHP extends AsyncTask<Usuario,Void,String> {
    Context context;
    Usuario usuario;

    public RegistrarUsuarioPHP(Context context,Usuario usu){
        this.context=context;
        this.usuario=usu;
    }
    @Override
    protected String doInBackground(Usuario... usuarios) {
        String result="";
        this.usuario=usuarios[0];
        String connstr= "http://ec2-52-56-170-196.eu-west-2.compute.amazonaws.com/everhorst001/WEB/registro.php";

        result= gestionarUsuarios(connstr,usuario);
        return result;
    }


    private String gestionarUsuarios(String connstr, Usuario usuario){
        String result="";
        String user=usuario.getUsuario();
        String pass= usuario.getContraseña();
        try {
            URL url= new URL(connstr);
            HttpURLConnection http= (HttpURLConnection) url.openConnection();
            http.setRequestMethod("POST");
            http.setDoInput(true);;
            http.setDoOutput(true);

            OutputStream ops= http.getOutputStream();
            BufferedWriter writer= new BufferedWriter( new OutputStreamWriter(ops,"UTF-8"));
            String data= URLEncoder.encode("user","UTF-8")+"="+URLEncoder.encode(user,"UTF-8")
                    +"&&"+ URLEncoder.encode("pass","UTF-8")+"="+URLEncoder.encode(pass,"UTF-8");
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
            Log.i("el resultado en registro",result);
            if(result.equals("true")){
                dejarEntrar();
            }
            return result;

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private void dejarEntrar(){
        Log.i("si","ha entrado en el dejarentrar");
        Intent i= new Intent(context, MainActivity.class);

        i.putExtra("user", usuario.getUsuario()    ); // para conseguir el nombre del usuario ingresado en el login al cargar el MainActivity
        context.startActivity(i);
    }
}
