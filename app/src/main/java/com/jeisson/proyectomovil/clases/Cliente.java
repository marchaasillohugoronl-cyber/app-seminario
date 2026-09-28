package com.jeisson.proyectomovil.clases;

public class Cliente {

    private String id_cliente;
    private String uid_cliente;

    private String nombres;
    private String apellidos;
    private String correo;
    private String telefono;
    private String dni;
    private String direccion;

    // Constructor vacío.
    // Firebase lo necesita para leer los datos.
    public Cliente() {
    }

    // Constructor completo
    public Cliente(
            String id_cliente,
            String direccion,
            String dni,
            String telefono,
            String correo,
            String apellidos,
            String nombres,
            String uid_cliente) {

        this.id_cliente = id_cliente;
        this.direccion = direccion;
        this.dni = dni;
        this.telefono = telefono;
        this.correo = correo;
        this.apellidos = apellidos;
        this.nombres = nombres;
        this.uid_cliente = uid_cliente;
    }

    public String getId_cliente() {
        return id_cliente;
    }

    public void setId_cliente(String id_cliente) {
        this.id_cliente = id_cliente;
    }

    public String getUid_cliente() {
        return uid_cliente;
    }

    public void setUid_cliente(String uid_cliente) {
        this.uid_cliente = uid_cliente;
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

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }
}