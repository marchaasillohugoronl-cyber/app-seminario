package com.jeisson.proyectomovil.Clientes;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import com.jeisson.proyectomovil.ui.AvisoDialog;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.jeisson.proyectomovil.AdapterCliente.AdapterCliente;
import com.jeisson.proyectomovil.PantallaActivity;
import com.jeisson.proyectomovil.R;
import com.jeisson.proyectomovil.clases.Cliente;

import java.util.ArrayList;
import java.util.List;

public class ListaClienteActivity extends PantallaActivity {

    // =========================================================
    // ELEMENTOS DE LA PANTALLA
    // =========================================================

    private FloatingActionButton btnagregarcliente;
    private RecyclerView rvClientes;
    private EditText etBuscarCliente;

    // =========================================================
    // ADAPTER Y LISTA
    // =========================================================

    private AdapterCliente adapterCliente;

    // Lista completa de clientes
    private List<Cliente> listaClientes;

    // =========================================================
    // FIREBASE
    // =========================================================

    private FirebaseAuth firebaseAuth;

    // =========================================================
    // ON CREATE
    // =========================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        mostrarPantalla(
                R.layout.activity_lista_cliente
        );

        // =====================================================
        // REFERENCIAS DE LA PANTALLA
        // =====================================================

        rvClientes = findViewById(
                R.id.rvClientes
        );

        btnagregarcliente = findViewById(
                R.id.btnAgregarCliente
        );

        etBuscarCliente = findViewById(
                R.id.etBuscarCliente
        );

        // =====================================================
        // FIREBASE AUTH
        // =====================================================

        firebaseAuth =
                FirebaseAuth.getInstance();

        // =====================================================
        // LISTA DE CLIENTES
        // =====================================================

        listaClientes =
                new ArrayList<>();

        // =====================================================
        // RECYCLERVIEW
        // 2 COLUMNAS
        // =====================================================

        rvClientes.setLayoutManager(
                new GridLayoutManager(
                        this,
                        2
                )
        );

        // =====================================================
        // ADAPTER
        // =====================================================

        adapterCliente =
                new AdapterCliente(
                        new ArrayList<>(
                                listaClientes
                        )
                );

        rvClientes.setAdapter(
                adapterCliente
        );

        // =====================================================
        // CONFIGURAR BUSCADOR
        // =====================================================

        configurarBuscador();

        // =====================================================
        // CLICK CORTO
        // =====================================================

        adapterCliente.setOnClienteClickListener(
                cliente -> {

                    Intent intent =
                            new Intent(
                                    ListaClienteActivity.this,
                                    AgregarClienteActivity.class
                            );

                    intent.putExtra(
                            "id_cliente",
                            cliente.getId_cliente()
                    );

                    startActivity(intent);
                }
        );

        // =====================================================
        // CLICK PROLONGADO
        // =====================================================

        adapterCliente.setOnClienteLongClickListener(
                cliente -> {

                    mostrarOpciones(
                            cliente
                    );
                }
        );

        // =====================================================
        // IMPORTANTE
        //
        // NO colocamos cargarClientes() aquí.
        //
        // La carga se realiza únicamente en onResume()
        // para evitar una doble consulta a Firebase.
        // =====================================================

        // =====================================================
        // BOTÓN AGREGAR CLIENTE
        // =====================================================

        btnagregarcliente.setOnClickListener(
                view -> {

                    Intent intent =
                            new Intent(
                                    ListaClienteActivity.this,
                                    AgregarClienteActivity.class
                            );

                    startActivity(intent);
                }
        );
    }

    // =========================================================
    // BUSCADOR
    // =========================================================

    private void configurarBuscador() {

        etBuscarCliente.addTextChangedListener(
                new TextWatcher() {

                    @Override
                    public void beforeTextChanged(
                            CharSequence s,
                            int start,
                            int count,
                            int after) {

                        // No hacer nada
                    }

                    @Override
                    public void onTextChanged(
                            CharSequence s,
                            int start,
                            int before,
                            int count) {

                        String texto =
                                s.toString()
                                        .trim()
                                        .toLowerCase();

                        ArrayList<Cliente>
                                listaFiltrada =
                                new ArrayList<>();

                        // =================================================
                        // RECORRER LISTA COMPLETA
                        // =================================================

                        for (Cliente cliente :
                                listaClientes) {

                            String nombres =
                                    cliente.getNombres() != null
                                            ? cliente.getNombres()
                                            .toLowerCase()
                                            : "";

                            String apellidos =
                                    cliente.getApellidos() != null
                                            ? cliente.getApellidos()
                                            .toLowerCase()
                                            : "";

                            String telefono =
                                    cliente.getTelefono() != null
                                            ? cliente.getTelefono()
                                            : "";

                            // =================================================
                            // BUSCAR POR:
                            // NOMBRE
                            // APELLIDO
                            // TELÉFONO
                            // =================================================

                            if (nombres.contains(texto)
                                    || apellidos.contains(texto)
                                    || telefono.contains(texto)) {

                                listaFiltrada.add(
                                        cliente
                                );
                            }
                        }

                        // =================================================
                        // ACTUALIZAR ADAPTER
                        // =================================================

                        adapterCliente.filtrarLista(
                                listaFiltrada
                        );
                    }

                    @Override
                    public void afterTextChanged(
                            Editable s) {

                        // No hacer nada
                    }
                }
        );
    }

    // =========================================================
    // CARGAR CLIENTES DESDE FIREBASE
    // =========================================================

    private void cargarClientes() {

        // =====================================================
        // OBTENER USUARIO AUTENTICADO
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

        // =====================================================
        // OBTENER UID
        // =====================================================

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
        // LEER CLIENTES UNA SOLA VEZ
        //
        // No utilizamos addValueEventListener()
        // para evitar listeners permanentes.
        // =====================================================

        misClientesRef.addListenerForSingleValueEvent(
                new ValueEventListener() {

                    @Override
                    public void onDataChange(
                            @NonNull DataSnapshot snapshot) {

                        // =================================================
                        // LIMPIAR LISTA ANTERIOR
                        // =================================================

                        listaClientes.clear();

                        // =================================================
                        // RECORRER CLIENTES
                        // =================================================

                        for (DataSnapshot dataSnapshot :
                                snapshot.getChildren()) {

                            try {

                                Cliente cliente =
                                        dataSnapshot.getValue(
                                                Cliente.class
                                        );

                                if (cliente != null) {

                                    listaClientes.add(
                                            cliente
                                    );
                                }

                            } catch (Exception e) {

                                Toast.makeText(
                                        ListaClienteActivity.this,
                                        "Error con un cliente: "
                                                + e.getMessage(),
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                        }

                        // =================================================
                        // ACTUALIZAR ADAPTER
                        // =================================================

                        adapterCliente.filtrarLista(
                                new ArrayList<>(
                                        listaClientes
                                )
                        );

                        // =================================================
                        // SI NO HAY CLIENTES
                        // =================================================

                        if (listaClientes.isEmpty()) {

                            Toast.makeText(
                                    ListaClienteActivity.this,
                                    "No tienes clientes registrados",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }

                    @Override
                    public void onCancelled(
                            @NonNull DatabaseError error) {

                        Toast.makeText(
                                ListaClienteActivity.this,
                                "Error al cargar clientes: "
                                        + error.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
        );
    }

    // =========================================================
    // MOSTRAR OPCIONES
    // =========================================================

    private void mostrarOpciones(
            Cliente cliente) {

        String nombre =
                cliente.getNombres() != null
                        ? cliente.getNombres()
                        : "";

        String apellidos =
                cliente.getApellidos() != null
                        ? cliente.getApellidos()
                        : "";

        String nombreCompleto =
                (
                        nombre
                                + " "
                                + apellidos
                ).trim();

        AvisoDialog.opcionesCliente(this, nombreCompleto, () -> {
            Intent intent = new Intent(this, AgregarClienteActivity.class);
            intent.putExtra("id_cliente", cliente.getId_cliente());
            startActivity(intent);
        }, () -> confirmarEliminar(cliente));
    }

    // =========================================================
    // CONFIRMAR ELIMINACIÓN
    // =========================================================

    private void confirmarEliminar(
            Cliente cliente) {

        String nombre =
                cliente.getNombres() != null
                        ? cliente.getNombres()
                        : "";

        String apellidos =
                cliente.getApellidos() != null
                        ? cliente.getApellidos()
                        : "";

        String nombreCompleto =
                (
                        nombre
                                + " "
                                + apellidos
                ).trim();

        AvisoDialog.confirmarEliminacion(this, nombreCompleto,
                () -> eliminarClienteDeFirebase(cliente));
    }

    // =========================================================
    // ELIMINAR CLIENTE DE FIREBASE
    // =========================================================

    private void eliminarClienteDeFirebase(
            Cliente cliente) {

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

        // =====================================================
        // UID
        // =====================================================

        String uid =
                usuario.getUid();

        // =====================================================
        // ID DEL CLIENTE
        // =====================================================

        String idCliente =
                cliente.getId_cliente();

        if (idCliente == null
                || idCliente.trim().isEmpty()) {

            Toast.makeText(
                    this,
                    "No se encontró el ID del cliente",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        // =====================================================
        // REFERENCIA:
        //
        // usuarios/{uid}/clientes/{id_cliente}
        // =====================================================

        DatabaseReference clienteRef =
                FirebaseDatabase.getInstance()
                        .getReference("usuarios")
                        .child(uid)
                        .child("clientes")
                        .child(idCliente);

        // =====================================================
        // ELIMINAR
        // =====================================================

        clienteRef.removeValue()
                .addOnSuccessListener(
                        unused -> {

                            AvisoDialog.mostrar(this, "Cliente eliminado",
                                    "El cliente se eliminó correctamente.",
                                    R.drawable.ic_aviso_eliminar, null);

                            // =================================================
                            // ACTUALIZAR LISTA
                            // =================================================

                            cargarClientes();
                        }
                )
                .addOnFailureListener(
                        e -> {

                            AvisoDialog.mostrar(this, "No se pudo eliminar",
                                    "Revisa tu conexión e inténtalo de nuevo.",
                                    R.drawable.ic_aviso_error, null);
                        }
                );
    }

    // =========================================================
    // ACTUALIZAR CLIENTES AL VOLVER
    // =========================================================

    @Override
    protected void onResume() {

        super.onResume();

        // =====================================================
        // AQUÍ SE REALIZA LA ÚNICA CARGA AL ABRIR/REGRESAR
        // =====================================================

        cargarClientes();
    }
}
