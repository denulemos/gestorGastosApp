package com.example.gestorgastos.Modelos;

public class UsuariosModel {

    private int id, admin, estado, solicitud;
    private String dni, contraseña, nombre, apellido, mail, camion;

    public UsuariosModel(int id, String dni, String contraseña, String nombre, String apellido, String mail, String camion, int admin,int solicitud, int estado) {
        this.id = id;
        this.dni = dni;
        this.contraseña = contraseña;
        this.nombre = nombre;
        this.apellido = apellido;
        this.mail = mail;
        this.camion = camion;
        this.admin = admin;
        this.solicitud = solicitud;
        this.estado = estado;
    }

    public UsuariosModel() {
    }

    public int getSolicitud() {
        return solicitud;
    }

    public void setSolicitud(int solicitud) {
        this.solicitud = solicitud;
    }

    public int getId() {
        return id;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getContraseña() {
        return contraseña;
    }

    public void setContraseña(String contraseña) {
        this.contraseña = contraseña;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getCamion() {
        return camion;
    }

    public void setCamion(String camion) {
        this.camion = camion;
    }

    public int getAdmin() {
        return admin;
    }

    public void setAdmin(int admin) {
        this.admin = admin;
    }
}
