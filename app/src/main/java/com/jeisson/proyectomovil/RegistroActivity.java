package com.jeisson.proyectomovil;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegistroActivity extends PantallaActivity {
    private EditText names, lastNames, email, password, confirmation;
    private Button register;
    private TextView status, back;
    private boolean busy;
    private RegistrationViewModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mostrarPantalla(R.layout.activity_registro);
        names = findViewById(R.id.et_names);
        lastNames = findViewById(R.id.et_lastnames);
        email = findViewById(R.id.et_mail);
        password = findViewById(R.id.et_password);
        confirmation = findViewById(R.id.et_confirmpassword);
        register = findViewById(R.id.btn_registrar);
        status = findViewById(R.id.registration_status);
        back = findViewById(R.id.lbl_goback);
        model = new ViewModelProvider(this).get(RegistrationViewModel.class);
        back.setOnClickListener(v -> goBack());
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override public void handleOnBackPressed() { goBack(); }
        });
        register.setOnClickListener(v -> validateAndRegister());
        model.state().observe(this, state -> {
            busy = state.busy;
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            boolean completingProfile = user != null;
            names.setEnabled(!busy);
            lastNames.setEnabled(!busy);
            email.setEnabled(!busy && !completingProfile);
            password.setEnabled(!busy && !completingProfile);
            confirmation.setEnabled(!busy && !completingProfile);
            register.setEnabled(!busy);
            back.setEnabled(!busy);
            register.setText(completingProfile ? "Guardar perfil" : "Registrarme");
            if (completingProfile) {
                email.setText(user.getEmail());
                password.setText("");
                confirmation.setText("");
            }
            String message = state.message;
            if (message.isEmpty() && completingProfile) {
                message = "Tu cuenta ya está creada. Completa tus nombres y apellidos para continuar; no necesitas repetir la contraseña.";
            }
            status.setText(message);
            status.setVisibility(message.isEmpty() ? View.GONE : View.VISIBLE);
            if (state.complete) {
                Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, DashboardActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                finish();
            }
        });
    }

    private void validateAndRegister() {
        if (busy) return;
        String first = names.getText().toString().trim();
        String last = lastNames.getText().toString().trim();
        String mail = email.getText().toString().trim();
        // No recortar contraseñas: los espacios también forman parte de ellas.
        String secret = password.getText().toString();
        if (first.isEmpty()) { showError(names, "Ingresa tus nombres"); return; }
        if (last.isEmpty()) { showError(lastNames, "Ingresa tus apellidos"); return; }
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            if (!Patterns.EMAIL_ADDRESS.matcher(mail).matches()) {
                showError(email, "Ingresa un correo válido"); return;
            }
            if (secret.length() < 8) {
                showError(password, "Usa al menos 8 caracteres"); return;
            }
            if (!secret.equals(confirmation.getText().toString())) {
                showError(confirmation, "Las contraseñas no coinciden"); return;
            }
        }
        model.register(mail, secret, first, last);
    }

    private void showError(EditText field, String message) {
        field.setError(message);
        field.requestFocus();
    }

    private void goBack() {
        if (busy) {
            Toast.makeText(this, "Espera a que termine la operación. Revisa tu conexión si tarda.", Toast.LENGTH_LONG).show();
            return;
        }
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(this, MainActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
        finish();
    }
}
