package com.jeisson.proyectomovil.Clientes;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.jeisson.proyectomovil.UserProfile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jeisson.proyectomovil.R;
import com.jeisson.proyectomovil.PantallaActivity;
import com.jeisson.proyectomovil.clases.Cliente;

public class AgregarClienteActivity extends PantallaActivity {

    private EditText etnombrecli;
    private EditText etapellidoscli;
    private EditText etdnicli;
    private EditText etcorreocli;
    private EditText ettelefonocli;
    private EditText etdireccioncli;

    private TextView tvNombreUsuario;

    private Button btnGuardarCliente;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference clientesRef;
    private DatabaseReference usuariosRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mostrarPantalla(R.layout.activity_agregar_cliente);

        // Referencias de los campos
        tvNombreUsuario = findViewById(R.id.tvNombreUsuario);

        etnombrecli = findViewById(R.id.etnombrecli);
        etapellidoscli = findViewById(R.id.etapellidoscli);
        etdnicli = findViewById(R.id.etdnicli);
        etcorreocli = findViewById(R.id.etcorreocli);
        ettelefonocli = findViewById(R.id.ettelefonocli);
        etdireccioncli = findViewById(R.id.etdireccioncli);

        btnGuardarCliente = findViewById(R.id.btnGuardarCliente);

        // Firebase
        firebaseAuth = FirebaseAuth.getInstance();

        clientesRef = FirebaseDatabase.getInstance()
                .getReference("clientes");

        usuariosRef = FirebaseDatabase.getInstance()
                .getReference(UserProfile.PATH);

        // Cargar nombre real del usuario
        cargarNombreUsuario();

        // Guardar cliente
        btnGuardarCliente.setOnClickListener(
                view -> guardarCliente()
        );
    }

    private void cargarNombreUsuario() {

        FirebaseUser usuario = firebaseAuth.getCurrentUser();

        if (usuario == null) {
            tvNombreUsuario.setText("Usuario no autenticado");
            return;
        }

        String uid = usuario.getUid();

        usuariosRef.child(uid)
                .addListenerForSingleValueEvent(
                        new ValueEventListener() {

                            @Override
                            public void onDataChange(
                                    @NonNull DataSnapshot snapshot) {

                                if (snapshot.exists()) {

                                    String nombres =
                                            snapshot.child("nombres")
                                                    .getValue(String.class);

                                    String apellidos =
                                            snapshot.child("apellidos")
                                                    .getValue(String.class);

                                    if (nombres == null) {
                                        nombres = "";
                                    }

                                    if (apellidos == null) {
                                        apellidos = "";
                                    }

                                    String nombreCompleto =
                                            (nombres + " " + apellidos).trim();

                                    if (!nombreCompleto.isEmpty()) {

                                        tvNombreUsuario.setText(
                                                "Usuario: " + nombreCompleto
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

    private void guardarCliente() {

        String nombres =
                etnombrecli.getText().toString().trim();

        String apellidos =
                etapellidoscli.getText().toString().trim();

        String dni =
                etdnicli.getText().toString().trim();

        String correo =
                etcorreocli.getText().toString().trim();

        String telefono =
                ettelefonocli.getText().toString().trim();

        String direccion =
                etdireccioncli.getText().toString().trim();

        // Validaciones
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

        // Usuario autenticado
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

        String uid = usuario.getUid();

        // Crear ID Firebase
        String key = clientesRef.push().getKey();

        if (key == null) {

            Toast.makeText(
                    this,
                    "No se pudo generar el ID del cliente",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        // ID visible del cliente
        String idCliente =
                "CLI-" + key.substring(
                        Math.max(0, key.length() - 6)
                );

        // Crear objeto Cliente
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

        // Guardar en Firebase
        clientesRef
                .child(key)
                .setValue(cliente)
                .addOnSuccessListener(unused -> {

                    Toast.makeText(
                            AgregarClienteActivity.this,
                            "Cliente guardado correctamente",
                            Toast.LENGTH_SHORT
                    ).show();

                    finish();
                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            AgregarClienteActivity.this,
                            "Error al guardar: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

}
