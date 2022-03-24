package com.example.tasknew;

public class TareaModel {
    private int id;
    private String nombre;
    private String descrip;
    private String data;
    private String usuario;
    public TareaModel(String nom, String usuario){
        this.nombre=nom;
        this.usuario = usuario;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescrip() {
        return descrip;
    }

    public void setDescrip(String descrip) {
        this.descrip = descrip;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setUsuario(String usuario) {
        this.usuario =usuario;
    }

    public String getUsuario() {
        return usuario;
    }


    private boolean checked = false;

    private String itemText = "";

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public String getItemText() {
        return itemText;
    }

    public void setItemText(String itemText) {
        this.itemText = itemText;
    }

}
