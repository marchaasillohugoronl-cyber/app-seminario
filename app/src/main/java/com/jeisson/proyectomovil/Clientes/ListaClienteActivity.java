package com.jeisson.proyectomovil.Clientes;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import com.jeisson.proyectomovil.R;
import com.jeisson.proyectomovil.PantallaActivity;
import com.jeisson.proyectomovil.clases.Cliente;

import java.util.ArrayList;
import java.util.List;

public class ListaClienteActivity extends PantallaActivity {

    private FloatingActionButton btnagregarcliente;
    private RecyclerView rvClientes;

    private AdapterCliente adapterCliente;
    private List<Cliente> listaClientes;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        mostrarPantalla(R.layout.activity_lista_cliente);

        // REFERENCIAS DE LA PANTALLA

        rvClientes = findViewById(R.id.rvClientes);

        btnagregarcliente = findViewById(
                R.id.btnAgregarCliente
        );

        // FIREBASE AUTH
        firebaseAuth = FirebaseAuth.getInstance();

        // LISTA DE CLIENTES

        listaClientes = new ArrayList<>();

        // RECYCLERVIEW - 2 COLUMNAS

        rvClientes.setLayoutManager(
                new GridLayoutManager(this, 2)
        );

        // ADAPTER

        adapterCliente = new AdapterCliente(
                listaClientes
        );

        rvClientes.setAdapter(
                adapterCliente
        );
        // CARGAR CLIENTES
        cargarClientes();

        // BOTÓN AGREGAR CLIENTE

        btnagregarcliente.setOnClickListener(
                view -> {

                    Intent intent = new Intent(
                            ListaClienteActivity.this,
                            AgregarClienteActivity.class
                    );

                    startActivity(intent);
                }
        );
    }

    // ==========================================
    // LISTAR CLIENTES DEL USUARIO ACTUAL
    // ==========================================

    private void cargarClientes() {

        // OBTENER USUARIO AUTENTICADO

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

        // ==========================================
        // OBTENER UID
        // ==========================================

        String uid = usuario.getUid();

        // ==========================================
        // REFERENCIA:
        // usuarios/{uid}/clientes
        // ==========================================

        DatabaseReference misClientesRef =
                FirebaseDatabase.getInstance()
                        .getReference("usuarios")
                        .child(uid)
                        .child("clientes");

        // ==========================================
        // LEER CLIENTES
        // ==========================================

        misClientesRef.addValueEventListener(
                new ValueEventListener() {

                    @Override
                    public void onDataChange(
                            @NonNull DataSnapshot snapshot) {
                        // LIMPIAR LISTA ANTERIOR
                        listaClientes.clear();
                        // RECORRER CLIENTES
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

                        // ==========================================
                        // ACTUALIZAR RECYCLERVIEW
                        // ==========================================

                        adapterCliente.notifyDataSetChanged();

                        // ==========================================
                        // MENSAJE SI NO HAY CLIENTES
                        // ==========================================

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

    // ==========================================
    // ACTUALIZAR CLIENTES AL VOLVER
    // ==========================================

    @Override
    protected void onResume() {

        super.onResume();

        // Volvemos a cargar los clientes
        // cuando regresamos de AgregarClienteActivity

        cargarClientes();
    }
}
