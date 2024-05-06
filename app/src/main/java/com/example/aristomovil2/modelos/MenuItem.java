package com.example.aristomovil2.modelos;

public class MenuItem {
    int id, orden;
    String texto, imagen, grupo, titulo;


    public MenuItem(int id, int orden, String texto, String imagen, String grupo, String titulo) {
        this.id = id;
        this.orden = orden;
        this.texto = texto;
        this.imagen = imagen;
        this.grupo = grupo;
        this.titulo = titulo;
    }

    public int getId() {
        return id;
    }

    public int getOrden() {
        return orden;
    }

    public String getTexto() {
        return texto;
    }

    public String getImagen() {
        return imagen;
    }

    public String getGrupo() { return grupo; }

    public String getTitulo() {return  titulo; }
}
