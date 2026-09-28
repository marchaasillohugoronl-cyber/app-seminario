package com.jeisson.proyectomovil.Mis_datos;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.jeisson.proyectomovil.MainActivity;
import com.jeisson.proyectomovil.R;
import com.jeisson.proyectomovil.PantallaActivity;

import java.util.HashMap;

public class Mis_datosActivity extends PantallaActivity {

    // TextViews
    private TextView tvcorreousuarioMD;
    private TextView tvcodigoMD;

    // EditTexts
    private EditText etNombreMD;
    private EditText etApellidoMD;
    private EditText etFechaNacimientoMD;
    private EditText etEdadMD;
    private EditText etTelefonoMD;
    private EditText etDomicilioMD;
    private EditText etTiktokMD;

    // Botón guardar datos
    private Button btnGuardarMD;

    // Imagen de perfil
    private ImageView ivfotousuario;

    // Firebase Authentication
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    // Firebase Realtime Database
    private DatabaseReference usuarios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        mostrarPantalla(R.layout.activity_mis_datos);

        inicializarVariables();
    }

    /**
     * Inicializa los componentes.
     */
    private void inicializarVariables() {

        // TextViews
        tvcorreousuarioMD =
                findViewById(R.id.tvcorreousuarioMD);

        tvcodigoMD =
                findViewById(R.id.tvcodigoMD);

        // EditTexts
        etNombreMD =
                findViewById(R.id.etNombreMD);

        etApellidoMD =
                findViewById(R.id.etApellidoMD);

        etFechaNacimientoMD =
                findViewById(R.id.etFechaNacimientoMD);

        etEdadMD =
                findViewById(R.id.etEdadMD);

        etTelefonoMD =
                findViewById(R.id.etTelefonoMD);

        etDomicilioMD =
                findViewById(R.id.etDomicilioMD);

        etTiktokMD =
                findViewById(R.id.etTiktokMD);

        // Botón guardar
        btnGuardarMD =
                findViewById(R.id.btnGuardarMD);

        // Imagen
        ivfotousuario =
                findViewById(R.id.ivfotousuarioMD);

        // Firebase Authentication
        firebaseAuth =
                FirebaseAuth.getInstance();

        firebaseUser =
                firebaseAuth.getCurrentUser();

        // Firebase Realtime Database
        usuarios =
                FirebaseDatabase
                        .getInstance()
                        .getReference(UserProfile.PATH);

        // Abrir pantalla para editar fotografía
        ivfotousuario.setOnClickListener(
                v -> abrirEditarFoto()
        );

        // Guardar datos personales
        btnGuardarMD.setOnClickListener(
                v -> actualizarDatos()
        );
    }

    /**
     * Abre la pantalla de edición de fotografía.
     */
    private void abrirEditarFoto() {

        Intent intent =
                new Intent(
                        Mis_datosActivity.this,
                        EditarFotoDatosActivity.class
                );

        startActivity(intent);
    }

    /**
     * Lee los datos del usuario desde Firebase.
     */
    private void lecturaDatos() {

        if (firebaseUser == null) {
            return;
        }

        btnGuardarMD.setEnabled(false);
        usuarios
                .child(firebaseUser.getUid())
                .addListenerForSingleValueEvent(
                        new ValueEventListener() {

                            @Override
                            public void onDataChange(
                                    @NonNull DataSnapshot snapshot
                            ) {

                                if (snapshot.exists()) {
                                    btnGuardarMD.setEnabled(true);

                                    String uid =
                                            obtenerValor(
                                                    snapshot,
                                                    "uid"
                                            );

                                    String nombres =
                                            obtenerValor(
                                                    snapshot,
                                                    "nombres"
                                            );

                                    String apellidos =
                                            obtenerValor(
                                                    snapshot,
                                                    "apellidos"
                                            );

                                    String mail =
                                            obtenerValor(
                                                    snapshot,
                                                    "mail"
                                            );

                                    String fechaNacimiento =
                                            obtenerValor(
                                                    snapshot,
                                                    "fechaNacimiento"
                                            );

                                    String edad =
                                            obtenerValor(
                                                    snapshot,
                                                    "edad"
                                            );

                                    String telefono =
                                            obtenerValor(
                                                    snapshot,
                                                    "telefono"
                                            );

                                    String domicilio =
                                            obtenerValor(
                                                    snapshot,
                                                    "domicilio"
                                            );

                                    String tiktok =
                                            obtenerValor(
                                                    snapshot,
                                                    "tiktok"
                                            );

                                    // Ahora buscamos Base64
                                    String imagenBase64 =
                                            obtenerValor(
                                                    snapshot,
                                                    "imagenBase64"
                                            );

                                    // Mostrar datos
                                    tvcorreousuarioMD.setText(
                                            mail
                                    );

                                    tvcodigoMD.setText(
                                            uid
                                    );

                                    etNombreMD.setText(
                                            nombres
                                    );

                                    etApellidoMD.setText(
                                            apellidos
                                    );

                                    etFechaNacimientoMD.setText(
                                            fechaNacimiento
                                    );

                                    etEdadMD.setText(
                                            edad
                                    );

                                    etTelefonoMD.setText(
                                            telefono
                                    );

                                    etDomicilioMD.setText(
                                            domicilio
                                    );

                                    etTiktokMD.setText(
                                            tiktok
                                    );

                                    // Mostrar fotografía Base64
                                    if (!imagenBase64.isEmpty()
                                            && !imagenBase64.equals("null")) {

                                        mostrarImagenBase64(
                                                imagenBase64
                                        );

                                    } else {

                                        ivfotousuario.setImageResource(
                                                R.drawable.profile2
                                        );
                                    }

                                } else {

                                    Toast.makeText(
                                            Mis_datosActivity.this,
                                            "No se encontraron datos previos",
                                            Toast.LENGTH_SHORT
                                    ).show();
                                }
                            }

                            @Override
                            public void onCancelled(
                                    @NonNull DatabaseError error
                            ) {

                                Toast.makeText(
                                        Mis_datosActivity.this,
                                        "Error: "
                                                + error.getMessage(),
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                        }
                );
    }

    /**
     * Convierte Base64 nuevamente en una imagen.
     */
    private void mostrarImagenBase64(
            String imagenBase64
    ) {

        try {

            byte[] bytes =
                    Base64.decode(
                            imagenBase64,
                            Base64.DEFAULT
                    );

            Bitmap bitmap =
                    BitmapFactory.decodeByteArray(
                            bytes,
                            0,
                            bytes.length
                    );

            if (bitmap != null) {

                ivfotousuario.setImageBitmap(
                        bitmap
                );

            } else {

                ivfotousuario.setImageResource(
                        R.drawable.profile2
                );
            }

        } catch (Exception e) {

            ivfotousuario.setImageResource(
                    R.drawable.profile2
            );

            Toast.makeText(
                    this,
                    "No se pudo cargar la fotografía",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    /**
     * Obtiene un valor de Firebase evitando mostrar "null".
     */
    private String obtenerValor(
            DataSnapshot snapshot,
            String nombreCampo
    ) {

        Object valor =
                snapshot.child(nombreCampo).getValue();

        if (valor == null) {
            return "";
        }

        return String.valueOf(valor);
    }

    /**
     * Actualiza los datos personales.
     *
     * La fotografía se maneja desde
     * EditarFotoDatosActivity.
     */
    private void actualizarDatos() {

        if (firebaseUser == null) {

            Toast.makeText(
                    this,
                    "Sesión no válida",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        String nombres =
                etNombreMD.getText()
                        .toString()
                        .trim();

        String apellidos =
                etApellidoMD.getText()
                        .toString()
                        .trim();

        if (!UserProfile.isComplete(nombres, apellidos)) {
            Toast.makeText(this, "Los nombres y apellidos no pueden estar vacíos", Toast.LENGTH_LONG).show();
            return;
        }

        String mail = firebaseUser.getEmail();

        String fechaNacimiento =
                etFechaNacimientoMD.getText()
                        .toString()
                        .trim();

        String edad =
                etEdadMD.getText()
                        .toString()
                        .trim();

        String telefono =
                etTelefonoMD.getText()
                        .toString()
                        .trim();

        String domicilio =
                etDomicilioMD.getText()
                        .toString()
                        .trim();

        String tiktok =
                etTiktokMD.getText()
                        .toString()
                        .trim();

        HashMap<String, Object> datos =
                new HashMap<>();

        datos.put(
                "uid",
                firebaseUser.getUid()
        );

        datos.put(
                "nombres",
                nombres
        );

        datos.put(
                "apellidos",
                apellidos
        );

        datos.put(
                "mail",
                mail
        );

        datos.put(
                "fechaNacimiento",
                fechaNacimiento
        );

        datos.put(
                "edad",
                edad
        );

        datos.put(
                "telefono",
                telefono
        );

        datos.put(
                "domicilio",
                domicilio
        );

        datos.put(
                "tiktok",
                tiktok
        );

        btnGuardarMD.setEnabled(false);
        usuarios
                .child(firebaseUser.getUid())
                .updateChildren(datos)
                .addOnSuccessListener(unused -> {
                    btnGuardarMD.setEnabled(true);

                    Toast.makeText(
                            Mis_datosActivity.this,
                            "Datos actualizados correctamente",
                            Toast.LENGTH_SHORT
                    ).show();

                })
                .addOnFailureListener(e -> {
                    btnGuardarMD.setEnabled(true);

                    Toast.makeText(
                            Mis_datosActivity.this,
                            "Error al guardar: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    /**
     * Comprueba la sesión.
     */
    private void compararSession() {

        if (firebaseUser != null) {

            lecturaDatos();

        } else {

            startActivity(
                    new Intent(
                            Mis_datosActivity.this,
                            MainActivity.class
                    )
            );

            finish();
        }
    }

    @Override
    protected void onStart() {

        super.onStart();

        compararSession();
    }
}