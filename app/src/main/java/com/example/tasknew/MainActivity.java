package com.example.tasknew;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    List<TareaModel> initItemList;
    AdapterListView listViewDataAdapter;
    ListView listViewWithCheckbox;
    String usuario;

    @SuppressLint("LongLogTag")
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.i("ha entrado", " en la clase MainActivity");
        miDB gestorDB = new miDB(MainActivity.this);
        Bundle extras = getIntent().getExtras();
        listViewWithCheckbox = (ListView) findViewById(R.id.listview);
        Log.i("en la clase main, los extras:",extras.getString("usuario"));
        usuario= extras.getString("usuario");
        // Initiate listview data.
        Log.i("desde la clase main el nombre del usuario",usuario);

        initItemList = this.display(gestorDB, usuario);
        Log.i("ha hecho bien el display", initItemList.toString());
        // Create a custom list view adapter with checkbox control.
        listViewDataAdapter = new AdapterListView(getApplicationContext(), initItemList);

        //final ArrayList<Boolean> checkedarray=null;
        listViewDataAdapter.notifyDataSetChanged();

        // Set data adapter to list view.
        listViewWithCheckbox.setAdapter(listViewDataAdapter);

        listViewWithCheckbox.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int itemIndex, long l) {
                // Get user selected item.
                Object itemObject = adapterView.getAdapter().getItem(itemIndex);

                // Translate the selected item to DTO object.
                TareaModel itemDto = (TareaModel) itemObject;

                // Get the checkbox.
                CheckBox itemCheckbox = (CheckBox) view.findViewById(R.id.checkbox);


                // Reverse the checkbox and clicked item check state.
                if (itemDto.isChecked()) {
                    itemCheckbox.setChecked(false);
                    itemDto.setChecked(false);
                } else {
                    itemCheckbox.setChecked(true);
                    itemDto.setChecked(true);
                }

                //Toast.makeText(getApplicationContext(), "select item text : " + itemDto.getItemText(), Toast.LENGTH_SHORT).show();
            }
        });

        //BOT??N DE MARCAR QUE LA TAREA EST?? HECHA
        Button marcarHecha = (Button) findViewById(R.id.list_marcar_hecha);
        marcarHecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int size = initItemList.size();
                for (int i = 0; i < size; i++) { //recorrer las tareas para saber cu??les est??n marcadas
                    TareaModel tm = initItemList.get(i);
                    if (tm.isChecked()) { //si est?? marcada, entonces strikethru
                        TextView prueba=(TextView) listViewWithCheckbox.getChildAt(i).findViewById(R.id.textView);
                        prueba.setPaintFlags(prueba.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        Log.i("algo","algo"+prueba.toString());
                        listViewWithCheckbox.deferNotifyDataSetChanged();
                        listViewDataAdapter.notifyDataSetChanged();
                        tm.setChecked(false);
                    }
                }
            }

        });

        // LISTENER DE ABRIR EL SETTINGS
        Button selectAllButton = (Button) findViewById(R.id.list_abrir_config);

        selectAllButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("LongLogTag")
            @Override
            public void onClick(View view) {
                //TextView textView = (TextView) view;
                //textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                Intent intent = new Intent(MainActivity.this, TareaSettings.class);

                int size = initItemList.size();
                for (int i = 0; i < size; i++) {
                    TareaModel dto = initItemList.get(i);

                    if (dto.isChecked()) {
                        Log.i("la tarea de la que conseguimos los settings", initItemList.get(i).getNombre());
                        intent.putExtra("tarea", initItemList.get(i).getNombre()); // para conseguir el nombre del usuario ingresado en el login al cargar el MainActivity
                        intent.putExtra("usuario", usuario);
                        startActivity(intent);

                        dto.setChecked(false);
                    } else {
                        dto.setChecked(true);
                    }
                }

                listViewDataAdapter.notifyDataSetChanged();

            }
        });


        //LISTENER DEL BOTON A??ADIR
        Button botonanadir = findViewById(R.id.id_boton_anadir);
        botonanadir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initItemList = addItemToList(v, gestorDB, extras.getString("usuario"), initItemList);
                Log.i("ha pulsado", " boton a??adir");

            }
        });





    }

    private List<TareaModel> display(miDB gestorDB, String usuario) {
        final ArrayList<TareaModel>[] listaTareas = new ArrayList[]{gestorDB.displayAll(usuario)};
        List<TareaModel> ret = new ArrayList<TareaModel>();
        for (int i = 0; i < listaTareas[0].size(); i++) {
            Log.i("tarea", " " + listaTareas[0].size());
            TareaModel tm = new TareaModel(listaTareas[0].get(i).getNombre(), usuario);
            tm.setChecked(false);
            tm.setItemText(tm.getNombre());
            ret.add(tm);
        }
        return ret;
    }

    public List<TareaModel> addItemToList(View view, miDB gestorDB, String usuario, List<TareaModel> lista) {

        EditText editText = findViewById(R.id.id_edit_text);
        TareaModel tm = new TareaModel(editText.getText().toString(), usuario);
        gestorDB.a??adirTarea(tm);
        editText.setText(" ");
        lista.add(tm);
        tm.setChecked(false);
        tm.setItemText(tm.getNombre());
        listViewDataAdapter.notifyDataSetChanged(); //PARA QUE SE ACTUALICE LA LISTA Y NOS SALGA LA TAREA A??ADIDA EN EL LISTVIEW
        return lista;
    }
}
