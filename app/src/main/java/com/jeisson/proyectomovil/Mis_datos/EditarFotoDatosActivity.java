package com.jeisson.proyectomovil.Mis_datos;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import android.media.ExifInterface;

import com.jeisson.proyectomovil.UserProfile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jeisson.proyectomovil.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public class EditarFotoDatosActivity extends AppCompatActivity {

    private static final int REQUEST_GALERIA = 100;
    private static final int REQUEST_CAMARA = 101;
    private static final int REQUEST_PERMISO_CAMARA = 102;

    private ImageView ivfotoactualizarEf;
    private Button btnelegirimgen;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    private DatabaseReference usuarios;

    private Uri imagenUri;
    private Uri fotoCamaraUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_editar_foto_datos);

        inicializarFirebase();

        inicializarComponentes();

        cargarFotoActual();
    }

    private void inicializarFirebase() {

        firebaseAuth = FirebaseAuth.getInstance();

        firebaseUser = firebaseAuth.getCurrentUser();

        usuarios = FirebaseDatabase
                .getInstance()
                .getReference(UserProfile.PATH);
    }

    private void inicializarComponentes() {

        ivfotoactualizarEf =
                findViewById(R.id.ivfotoactualizarEf);

        btnelegirimgen =
                findViewById(R.id.btnelegirimgen);

        btnelegirimgen.setOnClickListener(
                v -> mostrarDialogoFoto()
        );
    }

    private void cargarFotoActual() {

        if (firebaseUser == null) {
            return;
        }

        usuarios
                .child(firebaseUser.getUid())
                .child("imagenBase64")
                .get()
                .addOnSuccessListener(snapshot -> {

                    if (snapshot.exists()) {

                        String base64 =
                                String.valueOf(
                                        snapshot.getValue()
                                );

                        mostrarImagenBase64(base64);
                    }
                });
    }

    private void mostrarDialogoFoto() {

        View vistaDialogo =
                getLayoutInflater().inflate(
                        R.layout.dialogo_foto_datos,
                        null
                );

        ImageView ivGaleria =
                vistaDialogo.findViewById(
                        R.id.ivGaleria
                );

        ImageView ivCamara =
                vistaDialogo.findViewById(
                        R.id.ivCamara
                );

        Button btnelegirgaleriadialog =
                vistaDialogo.findViewById(
                        R.id.btnelegirgaleriadialog
                );

        Button btncamaradialog =
                vistaDialogo.findViewById(
                        R.id.btncamaradialog
                );

        AlertDialog dialogo =
                new AlertDialog.Builder(this)
                        .setView(vistaDialogo)
                        .create();

        View.OnClickListener elegirGaleria = v -> {
            dialogo.dismiss();
            abrirGaleria();
        };
        ivGaleria.setOnClickListener(elegirGaleria);
        btnelegirgaleriadialog.setOnClickListener(elegirGaleria);

        View.OnClickListener elegirCamara = v -> {
            dialogo.dismiss();
            comprobarPermisoCamara();
        };
        ivCamara.setOnClickListener(elegirCamara);
        btncamaradialog.setOnClickListener(elegirCamara);

        dialogo.show();
    }

    private void abrirGaleria() {

        Intent intent =
                new Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                );

        intent.setType("image/*");

        startActivityForResult(
                intent,
                REQUEST_GALERIA
        );
    }

    private void comprobarPermisoCamara() {

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED) {

            abrirCamara();

        } else {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.CAMERA
                    },
                    REQUEST_PERMISO_CAMARA
            );
        }
    }

    private void abrirCamara() {

        try {

            File archivoFoto =
                    crearArchivoImagen();

            fotoCamaraUri =
                    FileProvider.getUriForFile(
                            this,
                            getPackageName()
                                    + ".fileprovider",
                            archivoFoto
                    );

            Intent intent =
                    new Intent(
                            MediaStore.ACTION_IMAGE_CAPTURE
                    );

            intent.putExtra(
                    MediaStore.EXTRA_OUTPUT,
                    fotoCamaraUri
            );

            intent.addFlags(
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                            | Intent.FLAG_GRANT_READ_URI_PERMISSION
            );

            startActivityForResult(
                    intent,
                    REQUEST_CAMARA
            );

        } catch (IOException e) {

            Toast.makeText(
                    this,
                    "No se pudo abrir la cámara",
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    private File crearArchivoImagen()
            throws IOException {

        File directorio =
                getExternalFilesDir(
                        Environment.DIRECTORY_PICTURES
                );

        return File.createTempFile(
                "FOTO_",
                ".jpg",
                directorio
        );
    }

    @Override
    protected void onActivityResult(
            int requestCode,
            int resultCode,
            Intent data
    ) {

        super.onActivityResult(
                requestCode,
                resultCode,
                data
        );

        if (requestCode == REQUEST_GALERIA
                && resultCode == RESULT_OK) {

            if (data != null
                    && data.getData() != null) {

                imagenUri = data.getData();

                ivfotoactualizarEf.setImageURI(
                        imagenUri
                );

                convertirYGuardarBase64();
            }
        }

        if (requestCode == REQUEST_CAMARA
                && resultCode == RESULT_OK) {

            if (fotoCamaraUri != null) {

                imagenUri = fotoCamaraUri;

                ivfotoactualizarEf.setImageURI(
                        imagenUri
                );

                convertirYGuardarBase64();
            }
        }
    }

    private void convertirYGuardarBase64() {

        if (firebaseUser == null) {

            Toast.makeText(
                    this,
                    "Sesión no válida",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        if (imagenUri == null) {

            Toast.makeText(
                    this,
                    "No hay ninguna imagen",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        try {

            Bitmap bitmap =
                    BitmapFactory.decodeStream(
                            getContentResolver()
                                    .openInputStream(imagenUri)
                    );

            if (bitmap == null) {

                Toast.makeText(
                        this,
                        "No se pudo leer la imagen",
                        Toast.LENGTH_LONG
                ).show();

                return;
            }

            ExifInterface exif =
                    new ExifInterface(
                            getContentResolver()
                                    .openInputStream(imagenUri)
                    );

            int orientacion =
                    exif.getAttributeInt(
                            ExifInterface.TAG_ORIENTATION,
                            ExifInterface.ORIENTATION_NORMAL
                    );

            Matrix matrix = new Matrix();

            switch (orientacion) {

                case ExifInterface.ORIENTATION_ROTATE_90:

                    matrix.postRotate(90);

                    break;

                case ExifInterface.ORIENTATION_ROTATE_180:

                    matrix.postRotate(180);

                    break;

                case ExifInterface.ORIENTATION_ROTATE_270:

                    matrix.postRotate(270);

                    break;
            }

            if (!matrix.isIdentity()) {

                bitmap =
                        Bitmap.createBitmap(
                                bitmap,
                                0,
                                0,
                                bitmap.getWidth(),
                                bitmap.getHeight(),
                                matrix,
                                true
                        );
            }

            // Reducimos la imagen para evitar
            // guardar una cantidad excesiva de datos.
            bitmap = reducirImagen(bitmap);

            ByteArrayOutputStream byteArrayOutputStream =
                    new ByteArrayOutputStream();

            bitmap.compress(
                    Bitmap.CompressFormat.JPEG,
                    70,
                    byteArrayOutputStream
            );

            byte[] bytes =
                    byteArrayOutputStream.toByteArray();

            String imagenBase64 =
                    Base64.encodeToString(
                            bytes,
                            Base64.DEFAULT
                    );

            guardarBase64EnFirebase(
                    imagenBase64
            );

        } catch (Exception e) {

            Toast.makeText(
                    this,
                    "Error procesando imagen: "
                            + e.getMessage(),
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    private Bitmap reducirImagen(Bitmap bitmap) {

        int anchoMaximo = 800;

        if (bitmap.getWidth() <= anchoMaximo) {
            return bitmap;
        }

        float proporcion =
                (float) anchoMaximo
                        / bitmap.getWidth();

        int nuevoAncho =
                anchoMaximo;

        int nuevoAlto =
                Math.round(
                        bitmap.getHeight()
                                * proporcion
                );

        return Bitmap.createScaledBitmap(
                bitmap,
                nuevoAncho,
                nuevoAlto,
                true
        );
    }

    private void guardarBase64EnFirebase(
            String imagenBase64
    ) {

        Toast.makeText(
                this,
                "Guardando fotografía...",
                Toast.LENGTH_SHORT
        ).show();

        usuarios
                .child(firebaseUser.getUid())
                .child("imagenBase64")
                .setValue(imagenBase64)
                .addOnSuccessListener(unused -> {

                    Toast.makeText(
                            this,
                            "Fotografía guardada correctamente",
                            Toast.LENGTH_SHORT
                    ).show();

                    Intent intent =
                            new Intent(
                                    EditarFotoDatosActivity.this,
                                    Mis_datosActivity.class
                            );

                    intent.addFlags(
                            Intent.FLAG_ACTIVITY_CLEAR_TOP
                    );

                    startActivity(intent);

                    finish();
                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            this,
                            "Error al guardar fotografía: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

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

                ivfotoactualizarEf.setImageBitmap(
                        bitmap
                );
            }

        } catch (Exception e) {

            Toast.makeText(
                    this,
                    "No se pudo cargar la fotografía",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults
    ) {

        super.onRequestPermissionsResult(
                requestCode,
                permissions,
                grantResults
        );

        if (requestCode == REQUEST_PERMISO_CAMARA) {

            if (grantResults.length > 0
                    && grantResults[0]
                    == PackageManager.PERMISSION_GRANTED) {

                abrirCamara();

            } else {

                Toast.makeText(
                        this,
                        "Permiso de cámara denegado",
                        Toast.LENGTH_LONG
                ).show();
            }
        }
    }
}