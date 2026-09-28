package com.jeisson.proyectomovil;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends PantallaActivity {
    private EditText editTextUsername, editTextPassword;
    private Button buttonLogin;
    private TextView lblregistrar;
    private FirebaseAuth firebaseAuth;
    private boolean busy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mostrarPantalla(R.layout.activity_main);
        firebaseAuth = FirebaseAuth.getInstance();
        lblregistrar = findViewById(R.id.lbl_registrar);
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        lblregistrar.setOnClickListener(v -> startActivity(new Intent(this, RegistroActivity.class)));
        buttonLogin.setOnClickListener(v -> loginUsuario());
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (firebaseAuth.getCurrentUser() != null && !busy) checkProfile();
    }

    private void setBusy(boolean value) {
        busy = value;
        buttonLogin.setEnabled(!value);
        lblregistrar.setEnabled(!value);
        editTextUsername.setEnabled(!value);
        editTextPassword.setEnabled(!value);
    }

    private void loginUsuario() {
        if (busy) return;
        if (firebaseAuth.getCurrentUser() != null) { checkProfile(); return; }
        String email = editTextUsername.getText().toString().trim();
        String password = editTextPassword.getText().toString();
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextUsername.setError("Ingresa un correo válido");
            editTextUsername.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            editTextPassword.setError("Ingresa tu contraseña");
            editTextPassword.requestFocus();
            return;
        }
        setBusy(true);
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (isFinishing() || isDestroyed()) return;
            if (task.isSuccessful()) {
                checkProfile();
            } else {
                setBusy(false);
                new AlertDialog.Builder(this).setTitle("No se pudo iniciar sesión")
                        .setMessage(AuthErrors.message(task.getException()))
                        .setPositiveButton("Aceptar", null).show();
            }
        });
    }

    private void checkProfile() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null) { setBusy(false); return; }
        setBusy(true);
        FirebaseDatabase.getInstance().getReference(UserProfile.PATH).child(user.getUid()).get()
                .addOnCompleteListener(task -> {
                    if (isFinishing() || isDestroyed()) return;
                    setBusy(false);
                    if (!task.isSuccessful()) {
                        new AlertDialog.Builder(this)
                                .setTitle("No se pudo cargar tu perfil")
                                .setMessage("Tu sesión está iniciada, pero no pudimos comprobar tus datos. Revisa tu conexión o los permisos de la base de datos.")
                                .setPositiveButton("Reintentar", (dialog, which) -> checkProfile())
                                .setNegativeButton("Cerrar sesión", (dialog, which) -> firebaseAuth.signOut())
                                .setCancelable(false).show();
                        return;
                    }
                    boolean complete = UserProfile.isComplete(
                            task.getResult().child("nombres").getValue(String.class),
                            task.getResult().child("apellidos").getValue(String.class));
                    Class<?> destination = complete ? DashboardActivity.class : RegistroActivity.class;
                    startActivity(new Intent(this, destination)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                    finish();
                });
    }
}
