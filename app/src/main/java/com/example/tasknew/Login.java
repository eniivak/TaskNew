package com.example.tasknew;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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

                /*if(gestorDB.existeUsuario(usuario)){ //el usuario esta creado

                    if(verificarCredenciales(gestorDB,usuario)){//verificar que la contraseña es correcta
                        verificarenremoto();

                        Intent i= new Intent(Login.this, MainActivity.class);

                        i.putExtra("user", textousuario.getText().toString()    ); // para conseguir el nombre del usuario ingresado en el login al cargar el MainActivity
                        startActivity(i);
                        tw.setText("",TextView.BufferType.EDITABLE);
                    }

                    else{ //si la contraseña es incorrecta
                        //preguntar que meta otra vez la contraseña
                        textocontra.setText("");
                        AlertDialog.Builder adb=new AlertDialog.Builder(Login.this);
                        adb.setTitle("CONTRASEÑA INCORRECTA");
                        adb.setMessage("Por favor, vuelve a introducir la contraseña");
                        adb.setNegativeButton("Cancelar", null);
                        adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //comprobar si esta bien la contraseña

                                usuario.setContraseña(textocontra.getText().toString());
                                if(verificarCredenciales(gestorDB,usuario)){
                                    Intent i= new Intent(Login.this, MainActivity.class);

                                    i.putExtra("user", textousuario.getText().toString()    ); // para conseguir el nombre del usuario ingresado en el login al cargar el MainActivity
                                    startActivity(i);
                                }
                            }});
                        adb.show();
                    }

                }
                else{ //preguntar si quiere registrarse con esos datos
                    AlertDialog.Builder adb=new AlertDialog.Builder(Login.this);
                    adb.setTitle("Registro");
                    adb.setMessage("Quieres registrarte con el usuario y contraseña que has introducido? ");
                    adb.setNegativeButton("Cancelar", null);
                    adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            tw.setText("Pulsa el botón LOGIN nuevamente para ingresar",TextView.BufferType.EDITABLE);
                            gestorDB.añadirUsuario(usuario); //hacer otro xml?
                        }});
                    adb.show();
                }*/



            }
        });


    }
    private void verificarenremoto(){
        String usuario= textousuario.getText().toString();
        String contraseña= textocontra.getText().toString();

        //UsuariosPHP bg= new UsuariosPHP(this,);
       // bg.execute(usuario,contraseña);
    }
    private boolean verificarCredenciales(miDB gestorDB, Usuario usuario){
        if(gestorDB.contrabien(usuario)){
            return true;
        }
        else{
            return false;
        }
    }

}