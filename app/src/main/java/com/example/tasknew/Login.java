package com.example.tasknew;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

public class Login extends AppCompatActivity {

    EditText textousuario,textocontra;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        miDB gestorDB= new miDB(Login.this);
        Button botonlogin=  findViewById(R.id.button_login);
        textousuario= findViewById(R.id.text_usuario);
        textocontra= findViewById(R.id.text_contra);
        //recorrer todos los usuarios y las tareas y coger las fechas, si alguno es hoy saltar notificacion? (esto para siguientes mejoras)
        //sino pensar en otra notificacion, por ejemplo cuando borres una tarea o asi
        botonlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView tw= findViewById(R.id.registro_info);
                Usuario usuario= new Usuario(textousuario.getText().toString(),textocontra.getText().toString());
                UsuariosPHP usuariosphp= new UsuariosPHP(Login.this,usuario);
                usuariosphp.execute(usuario);
            }
        });
        //SUBSCRIBIR USUARIO AL TOPIC
        FirebaseMessaging.getInstance().subscribeToTopic("subirimagen")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "bien";
                        if (!task.isSuccessful()) {
                            msg = "mal";
                        }
                    }
                });



    }

}
