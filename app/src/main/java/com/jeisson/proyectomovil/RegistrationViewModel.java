package com.jeisson.proyectomovil;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

/** Mantiene la operación activa al girar la pantalla, sin retener la Activity. */
public class RegistrationViewModel extends ViewModel {
    static final class State {
        final boolean busy;
        final boolean complete;
        final String message;

        State(boolean busy, boolean complete, String message) {
            this.busy = busy;
            this.complete = complete;
            this.message = message;
        }
    }

    private final MutableLiveData<State> state = new MutableLiveData<>(
            new State(false, false, ""));

    LiveData<State> state() { return state; }

    void register(String email, String password, String names, String lastNames) {
        if (state.getValue().busy || state.getValue().complete) return;
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            saveProfile(user, names, lastNames);
            return;
        }
        state.setValue(new State(true, false, "Creando tu cuenta…"));
        auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(result -> saveProfile(result.getUser(), names, lastNames))
                .addOnFailureListener(error -> state.setValue(
                        new State(false, false, AuthErrors.message(error))));
    }

    private void saveProfile(FirebaseUser user, String names, String lastNames) {
        if (user == null) {
            state.setValue(new State(false, false, "La sesión expiró. Inicia sesión nuevamente."));
            return;
        }
        state.setValue(new State(true, false,
                "Guardando tu perfil… Si pierdes la conexión, el guardado continuará al recuperarla."));
        FirebaseDatabase.getInstance().getReference(UserProfile.PATH).child(user.getUid())
                .updateChildren(UserProfile.registration(user.getUid(), user.getEmail(), names, lastNames))
                .addOnSuccessListener(unused -> state.setValue(
                        new State(false, true, "Usuario creado con éxito")))
                .addOnFailureListener(error -> state.setValue(new State(false, false,
                        "Tu cuenta ya existe, pero no se pudo guardar el perfil. Puedes reintentar sin crear otra cuenta. "
                                + "Detalle: " + error.getMessage())));
    }
}
