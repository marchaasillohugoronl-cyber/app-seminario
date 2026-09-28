package com.jeisson.proyectomovil;

import java.util.HashMap;
import java.util.Map;

/** Contrato compartido por el registro y las pantallas de perfil. */
public final class UserProfile {
    // Debe coincidir con usuarios/$uid en las reglas de Realtime Database.
    public static final String PATH = "usuarios";

    private UserProfile() { }

    public static boolean isComplete(String names, String lastNames) {
        return names != null && !names.trim().isEmpty()
                && lastNames != null && !lastNames.trim().isEmpty();
    }

    public static Map<String, Object> registration(String uid, String email,
                                                    String names, String lastNames) {
        Map<String, Object> values = new HashMap<>();
        values.put("uid", uid);
        values.put("mail", email);
        values.put("nombres", names.trim());
        values.put("apellidos", lastNames.trim());
        // Actualización parcial: conserva foto y demás datos si se reintenta.
        // La contraseña pertenece exclusivamente a Firebase Authentication.
        return values;
    }
}
