package com.jeisson.proyectomovil;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.jeisson.proyectomovil.Clientes.ListaClienteActivity;
import com.jeisson.proyectomovil.Mis_datos.Mis_datosActivity;

public class DashboardActivity extends PantallaActivity {

    CardView cvClientes, cvMisdatos;

    Dialog dialogoDev;

    TextView tvnombreapellido, tvcodigousuario;

    Button btncerrarsesion, btndesarrollador;

    FirebaseAuth firebaseAuth;
    DatabaseReference usuariosRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mostrarPantalla(R.layout.activity_dashboard);

        // ==========================================
        // VINCULAR VISTAS
        // ==========================================

        tvnombreapellido = findViewById(R.id.tv_NombreApellido);

        tvcodigousuario = findViewById(R.id.tvidUsuario);

        btncerrarsesion = findViewById(R.id.btncerrarsesion);

        btndesarrollador = findViewById(R.id.btndesarrollador);

        cvClientes = findViewById(R.id.cvclientes);

        cvMisdatos = findViewById(R.id.cvmisdatos);

        dialogoDev = new Dialog(this);

        // ==========================================
        // FIREBASE
        // ==========================================

        firebaseAuth = FirebaseAuth.getInstance();

        usuariosRef = FirebaseDatabase
                .getInstance()
                .getReference(UserProfile.PATH);

        // ==========================================
        // NAVEGACIÓN
        // ==========================================

        // CLIENTES
        cvClientes.setOnClickListener(v -> {

            Intent intent = new Intent(
                    DashboardActivity.this,
                    ListaClienteActivity.class
            );

            startActivity(intent);
        });

        // MIS DATOS
        cvMisdatos.setOnClickListener(v -> {

            Intent intent = new Intent(
                    DashboardActivity.this,
                    Mis_datosActivity.class
            );

            startActivity(intent);
        });

        // CERRAR SESIÓN
        btncerrarsesion.setOnClickListener(v ->
                cierreSesion()
        );

        // DESARROLLADOR
        btndesarrollador.setOnClickListener(v ->
                desarrollador()
        );

        // Los datos se recargan en onStart, también al volver de editar el perfil.
    }

    // ==========================================
    // VERIFICAR SESIÓN
    // ==========================================

    @Override
    protected void onStart() {

        super.onStart();

        if (firebaseAuth.getCurrentUser() == null) {

            redirectToLogin();
        } else {
            loadUserData();
        }
    }

    // ==========================================
    // REDIRECCIÓN AL LOGIN
    // ==========================================

    private void redirectToLogin() {

        Intent intent = new Intent(
                DashboardActivity.this,
                MainActivity.class
        );

        startActivity(intent);

        finish();
    }

    // ==========================================
    // CARGAR DATOS DEL USUARIO
    // ==========================================

    private void loadUserData() {

        FirebaseUser currentUser =
                firebaseAuth.getCurrentUser();

        if (currentUser == null) {
            return;
        }

        String uid = currentUser.getUid();

        usuariosRef
                .child(uid)
                .addListenerForSingleValueEvent(
                        new ValueEventListener() {

                            @Override
                            public void onDataChange(
                                    @NonNull DataSnapshot snapshot) {

                                if (snapshot.exists()) {

                                    String names =
                                            snapshot.child("nombres")
                                                    .getValue(String.class);

                                    String lastNames =
                                            snapshot.child("apellidos")
                                                    .getValue(String.class);

                                    String fullName =
                                            (names != null ? names : "")
                                                    + " "
                                                    + (lastNames != null
                                                    ? lastNames
                                                    : "");

                                    tvnombreapellido.setText(
                                            "Usuario: "
                                                    + fullName.trim()
                                    );

                                    tvcodigousuario.setText(
                                            "Código: " + uid
                                    );

                                } else {

                                    tvnombreapellido.setText(
                                            "Nombre de usuario no encontrado"
                                    );

                                    tvcodigousuario.setText(
                                            "Código: " + uid
                                    );
                                }
                            }

                            @Override
                            public void onCancelled(
                                    @NonNull DatabaseError error) {

                                Toast.makeText(
                                        DashboardActivity.this,
                                        "Error al cargar datos: "
                                                + error.getMessage(),
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                        }
                );
    }

    // ==========================================
    // CERRAR SESIÓN
    // ==========================================

    private void cierreSesion() {

        firebaseAuth.signOut();

        Toast.makeText(
                this,
                "Cerraste sesión correctamente",
                Toast.LENGTH_SHORT
        ).show();

        redirectToLogin();
    }

    // ==========================================
    // VENTANA DESARROLLADOR
    // ==========================================

    private void desarrollador() {

        ImageButton btnfonodev;
        ImageButton btnyutudev;

        Button btnvolverdev;

        dialogoDev.setContentView(
                R.layout.dialogo_developer
        );

        btnfonodev =
                dialogoDev.findViewById(
                        R.id.btnfonodev
                );

        btnyutudev =
                dialogoDev.findViewById(
                        R.id.btnyutudev
                );

        btnvolverdev =
                dialogoDev.findViewById(
                        R.id.btnvolverdev
                );

        // BOTÓN TELÉFONO
        btnfonodev.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        String numero =
                                "928177377";

                        Uri uri =
                                Uri.parse("tel:" + numero);

                        Intent intent =
                                new Intent(
                                        Intent.ACTION_VIEW,
                                        uri
                                );

                        startActivity(intent);
                    }
                }
        );

        // BOTÓN YOUTUBE
        btnyutudev.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        Uri uri =
                                Uri.parse(
                                        "https://www.youtube.com/watch?v=Zu7tiLtLYn8&list=RDGMEMYH9CUrFO7CfLJpaD7UR85wVMZu7tiLtLYn8&start_radio=1"
                                );

                        Intent intent =
                                new Intent(
                                        Intent.ACTION_VIEW,
                                        uri
                                );

                        startActivity(intent);
                    }
                }
        );

        // BOTÓN VOLVER
        btnvolverdev.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        dialogoDev.dismiss();
                    }
                }
        );

        dialogoDev.show();

        dialogoDev.setCanceledOnTouchOutside(false);
    }
}