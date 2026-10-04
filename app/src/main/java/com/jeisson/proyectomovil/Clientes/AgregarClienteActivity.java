package com.jeisson.proyectomovil.Clientes;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import com.jeisson.proyectomovil.ui.AvisoDialog;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.jeisson.proyectomovil.PantallaActivity;
import com.jeisson.proyectomovil.R;
import com.jeisson.proyectomovil.UserProfile;
import com.jeisson.proyectomovil.clases.Cliente;

public class AgregarClienteActivity extends PantallaActivity {

    // =========================================================
    // EDITTEXT
    // =========================================================

    private EditText etnombrecli;
    private EditText etapellidoscli;
    private EditText etdnicli;
    private EditText etcorreocli;
    private EditText ettelefonocli;
    private EditText etdireccioncli;

    // =========================================================
    // TEXTVIEW
    // =========================================================

    private TextView tvNombreUsuario;

    // =========================================================
    // BOTÓN
    // =========================================================

    private Button btnGuardarCliente;

    // =========================================================
    // FIREBASE
    // =========================================================

    private FirebaseAuth firebaseAuth;
    private DatabaseReference usuariosRef;

    // =========================================================
    // MODO EDICIÓN
    // =========================================================

    private String idClienteEdit = null;

    private boolean esModoEdicion = false;

    // =========================================================
    // ON CREATE
    // =========================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        mostrarPantalla(
                R.layout.activity_agregar_cliente
        );

        // =====================================================
        // INICIALIZAR ELEMENTOS
        // =====================================================

        tvNombreUsuario =
                findViewById(
                        R.id.tvNombreUsuario
                );

        etnombrecli =
                findViewById(
                        R.id.etnombrecli
                );

        etapellidoscli =
                findViewById(
                        R.id.etapellidoscli
                );

        etdnicli =
                findViewById(
                        R.id.etdnicli
                );

        etcorreocli =
                findViewById(
                        R.id.etcorreocli
                );

        ettelefonocli =
                findViewById(
                        R.id.ettelefonocli
                );

        etdireccioncli =
                findViewById(
                        R.id.etdireccioncli
                );

        btnGuardarCliente =
                findViewById(
                        R.id.btnGuardarCliente
                );

        // =====================================================
        // FIREBASE
        // =====================================================

        firebaseAuth =
                FirebaseAuth.getInstance();

        usuariosRef =
                FirebaseDatabase.getInstance()
                        .getReference(
                                UserProfile.PATH
                        );

        // =====================================================
        // CARGAR NOMBRE DEL USUARIO
        // =====================================================

        cargarNombreUsuario();

        // =====================================================
        // COMPROBAR SI ES NUEVO O EDICIÓN
        // =====================================================

        comprobarModoEdicion();

        // =====================================================
        // BOTÓN GUARDAR / ACTUALIZAR
        // =====================================================

        btnGuardarCliente.setOnClickListener(
                view -> guardarCliente()
        );
    }

    // =========================================================
    // COMPROBAR MODO EDICIÓN
    // =========================================================

    private void comprobarModoEdicion() {

        if (getIntent() != null
                && getIntent().hasExtra("id_cliente")) {

            idClienteEdit =
                    getIntent().getStringExtra(
                            "id_cliente"
                    );

            if (idClienteEdit != null
                    && !idClienteEdit.trim().isEmpty()) {

                esModoEdicion = true;

                // =================================================
                // CAMBIAR TEXTO DEL BOTÓN
                // =================================================

                btnGuardarCliente.setText(
                        "Actualizar Cliente"
                );

                // =================================================
                // CARGAR DATOS
                // =================================================

                cargarDatosDelCliente(
                        idClienteEdit
                );
            }
        }
    }

    // =========================================================
    // CARGAR NOMBRE DEL USUARIO
    // =========================================================

    private void cargarNombreUsuario() {

        FirebaseUser usuario =
                firebaseAuth.getCurrentUser();

        if (usuario == null) {

            tvNombreUsuario.setText(
                    "Usuario no autenticado"
            );

            return;
        }

        String uid =
                usuario.getUid();

        usuariosRef
                .child(uid)
                .addListenerForSingleValueEvent(
                        new ValueEventListener() {

                            @Override
                            public void onDataChange(
                                    @NonNull DataSnapshot snapshot) {

                                if (snapshot.exists()) {

                                    String nombres =
                                            snapshot.child("nombres")
                                                    .getValue(
                                                            String.class
                                                    );

                                    String apellidos =
                                            snapshot.child("apellidos")
                                                    .getValue(
                                                            String.class
                                                    );

                                    if (nombres == null) {
                                        nombres = "";
                                    }

                                    if (apellidos == null) {
                                        apellidos = "";
                                    }

                                    String nombreCompleto =
                                            (
                                                    nombres
                                                            + " "
                                                            + apellidos
                                            ).trim();

                                    if (!nombreCompleto.isEmpty()) {

                                        tvNombreUsuario.setText(
                                                "Usuario: "
                                                        + nombreCompleto
                                        );

                                    } else {

                                        tvNombreUsuario.setText(
                                                "Nombre no registrado"
                                        );
                                    }

                                } else {

                                    tvNombreUsuario.setText(
                                            "Usuario no encontrado"
                                    );
                                }
                            }

                            @Override
                            public void onCancelled(
                                    @NonNull DatabaseError error) {

                                tvNombreUsuario.setText(
                                        "No se pudo cargar el nombre"
                                );

                                Toast.makeText(
                                        AgregarClienteActivity.this,
                                        "Error: "
                                                + error.getMessage(),
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                        }
                );
    }

    // =========================================================
    // CARGAR DATOS DEL CLIENTE PARA MODIFICAR
    // =========================================================

    private void cargarDatosDelCliente(
            String idCliente) {

        // =====================================================
        // USUARIO AUTENTICADO
        // =====================================================

        FirebaseUser usuario =
                firebaseAuth.getCurrentUser();

        if (usuario == null) {

            Toast.makeText(
                    this,
                    "No hay un usuario autenticado",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        String uid =
                usuario.getUid();

        // =====================================================
        // REFERENCIA:
        //
        // usuarios/{uid}/clientes/{idCliente}
        // =====================================================

        DatabaseReference clienteRef =
                FirebaseDatabase.getInstance()
                        .getReference("usuarios")
                        .child(uid)
                        .child("clientes")
                        .child(idCliente);

        // =====================================================
        // LEER CLIENTE
        // =====================================================

        clienteRef.addListenerForSingleValueEvent(
                new ValueEventListener() {

                    @Override
                    public void onDataChange(
                            @NonNull DataSnapshot snapshot) {

                        if (!snapshot.exists()) {

                            Toast.makeText(
                                    AgregarClienteActivity.this,
                                    "No se encontró el cliente",
                                    Toast.LENGTH_SHORT
                            ).show();

                            return;
                        }

                        try {

                            Cliente cliente =
                                    snapshot.getValue(
                                            Cliente.class
                                    );

                            if (cliente == null) {

                                Toast.makeText(
                                        AgregarClienteActivity.this,
                                        "No se pudieron cargar los datos",
                                        Toast.LENGTH_SHORT
                                ).show();

                                return;
                            }

                            // =================================================
                            // AUTOCOMPLETAR CAMPOS
                            // =================================================

                            etnombrecli.setText(
                                    cliente.getNombres() != null
                                            ? cliente.getNombres()
                                            : ""
                            );

                            etapellidoscli.setText(
                                    cliente.getApellidos() != null
                                            ? cliente.getApellidos()
                                            : ""
                            );

                            etdnicli.setText(
                                    cliente.getDni() != null
                                            ? cliente.getDni()
                                            : ""
                            );

                            etcorreocli.setText(
                                    cliente.getCorreo() != null
                                            ? cliente.getCorreo()
                                            : ""
                            );

                            ettelefonocli.setText(
                                    cliente.getTelefono() != null
                                            ? cliente.getTelefono()
                                            : ""
                            );

                            etdireccioncli.setText(
                                    cliente.getDireccion() != null
                                            ? cliente.getDireccion()
                                            : ""
                            );

                        } catch (Exception e) {

                            Toast.makeText(
                                    AgregarClienteActivity.this,
                                    "Error al cargar cliente: "
                                            + e.getMessage(),
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    }

                    @Override
                    public void onCancelled(
                            @NonNull DatabaseError error) {

                        Toast.makeText(
                                AgregarClienteActivity.this,
                                "Error al cargar datos: "
                                        + error.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
        );
    }

    // =========================================================
    // GUARDAR / ACTUALIZAR CLIENTE
    // =========================================================

    private void guardarCliente() {

        // =====================================================
        // OBTENER DATOS
        // =====================================================

        String nombres =
                etnombrecli.getText()
                        .toString()
                        .trim();

        String apellidos =
                etapellidoscli.getText()
                        .toString()
                        .trim();

        String dni =
                etdnicli.getText()
                        .toString()
                        .trim();

        String correo =
                etcorreocli.getText()
                        .toString()
                        .trim();

        String telefono =
                ettelefonocli.getText()
                        .toString()
                        .trim();

        String direccion =
                etdireccioncli.getText()
                        .toString()
                        .trim();

        // =====================================================
        // VALIDACIONES
        // =====================================================

        if (TextUtils.isEmpty(nombres)) {

            etnombrecli.setError(
                    "Ingrese los nombres"
            );

            etnombrecli.requestFocus();

            return;
        }

        if (TextUtils.isEmpty(apellidos)) {

            etapellidoscli.setError(
                    "Ingrese los apellidos"
            );

            etapellidoscli.requestFocus();

            return;
        }

        if (TextUtils.isEmpty(dni)) {

            etdnicli.setError(
                    "Ingrese el DNI"
            );

            etdnicli.requestFocus();

            return;
        }

        if (dni.length() != 8) {

            etdnicli.setError(
                    "El DNI debe tener 8 dígitos"
            );

            etdnicli.requestFocus();

            return;
        }

        if (TextUtils.isEmpty(correo)) {

            etcorreocli.setError(
                    "Ingrese el correo"
            );

            etcorreocli.requestFocus();

            return;
        }

        if (TextUtils.isEmpty(telefono)) {

            ettelefonocli.setError(
                    "Ingrese el teléfono"
            );

            ettelefonocli.requestFocus();

            return;
        }

        if (TextUtils.isEmpty(direccion)) {

            etdireccioncli.setError(
                    "Ingrese la dirección"
            );

            etdireccioncli.requestFocus();

            return;
        }

        // =====================================================
        // USUARIO AUTENTICADO
        // =====================================================

        FirebaseUser usuario =
                firebaseAuth.getCurrentUser();

        if (usuario == null) {

            Toast.makeText(
                    this,
                    "No hay un usuario autenticado",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        String uid =
                usuario.getUid();

        // =====================================================
        // REFERENCIA:
        //
        // usuarios/{uid}/clientes
        // =====================================================

        DatabaseReference misClientesRef =
                FirebaseDatabase.getInstance()
                        .getReference("usuarios")
                        .child(uid)
                        .child("clientes");

        // =====================================================
        // DETERMINAR ID
        // =====================================================

        String idCliente;

        // =====================================================
        // MODO EDICIÓN
        // =====================================================

        if (esModoEdicion) {

            idCliente =
                    idClienteEdit;

            if (idCliente == null
                    || idCliente.trim().isEmpty()) {

                Toast.makeText(
                        this,
                        "No se encontró el ID del cliente",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

        }

        // =====================================================
        // MODO NUEVO CLIENTE
        // =====================================================

        else {

            String key =
                    misClientesRef
                            .push()
                            .getKey();

            if (key == null) {

                Toast.makeText(
                        this,
                        "No se pudo generar el ID del cliente",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            idCliente =
                    "CLI-"
                            + key.substring(
                            Math.max(
                                    0,
                                    key.length() - 6
                            )
                    );
        }

        // =====================================================
        // CREAR OBJETO CLIENTE
        // =====================================================

        Cliente cliente =
                new Cliente(
                        idCliente,
                        direccion,
                        dni,
                        telefono,
                        correo,
                        apellidos,
                        nombres,
                        uid
                );

        // =====================================================
        // GUARDAR EN FIREBASE
        //
        // Si es nuevo:
        // crea el cliente.
        //
        // Si es edición:
        // reemplaza los datos del mismo cliente.
        // =====================================================

        misClientesRef
                .child(idCliente)
                .setValue(cliente)

                .addOnSuccessListener(
                        unused -> {

                            String mensajeNombre =
                                    (
                                            nombres
                                                    + " "
                                                    + apellidos
                                    ).trim();

                            mostrarAvisoGuardado(
                                    mensajeNombre
                            );
                        }
                )

                .addOnFailureListener(
                        e -> {

                            AvisoDialog.mostrar(this, "No se pudo guardar",
                                    "Revisa tu conexión e inténtalo de nuevo.",
                                    R.drawable.ic_aviso_error, null);
                        }
                );
    }

    // =========================================================
    // AVISO PERSONALIZADO
    // =========================================================

    private void mostrarAvisoGuardado(
            String nombreCliente) {

        AvisoDialog.mostrar(this,
                esModoEdicion ? "Cliente actualizado" : "Cliente guardado",
                "Los datos de " + nombreCliente + (esModoEdicion
                        ? " se actualizaron correctamente." : " se guardaron correctamente."),
                R.drawable.ic_aviso_exito, this::finish);
    }
}
