package com.example.nextcode.models;

import java.util.ArrayList;

public class Usuario {

    private int id;
    private String contrasenia;
    private String nombres;
    private String apellidos;
    private String cedula;
    private String correo;
    private String status;

    private ArrayList<Plan> planes;


    public Usuario(){
        planes = new ArrayList<>();
    }

    public void setPlan(Plan plan){
        planes.add(plan);
    }

    public ArrayList<Plan> getPlanes(){
        return this.planes;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContrasenia() {
        return contrasenia;
    }

    public void setContrasenia(String contrasenia) {
        this.contrasenia = contrasenia;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
