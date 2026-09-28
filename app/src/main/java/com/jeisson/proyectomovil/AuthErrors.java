package com.jeisson.proyectomovil;

import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuthException;

final class AuthErrors {
    private AuthErrors() { }

    static String message(Exception error) {
        if (error instanceof FirebaseNetworkException) {
            return "No se pudo conectar. Revisa tu conexión a Internet e inténtalo de nuevo.";
        }
        if (error instanceof FirebaseTooManyRequestsException) {
            return "Demasiados intentos. Espera unos minutos y vuelve a intentar.";
        }
        if (error instanceof FirebaseAuthException) {
            switch (((FirebaseAuthException) error).getErrorCode()) {
                case "ERROR_EMAIL_ALREADY_IN_USE":
                    return "Este correo ya tiene una cuenta. Inicia sesión para continuar o completar tu perfil.";
                case "ERROR_INVALID_EMAIL":
                    return "Ingresa un correo válido.";
                case "ERROR_WEAK_PASSWORD":
                    return "La contraseña no cumple los requisitos de seguridad.";
                case "ERROR_OPERATION_NOT_ALLOWED":
                    return "El registro por correo no está habilitado en Firebase Authentication.";
                case "ERROR_USER_DISABLED":
                    return "Esta cuenta está deshabilitada.";
                case "ERROR_WRONG_PASSWORD":
                case "ERROR_USER_NOT_FOUND":
                case "ERROR_INVALID_CREDENTIAL":
                case "ERROR_INVALID_LOGIN_CREDENTIALS":
                    return "El correo o la contraseña son incorrectos.";
            }
        }
        return "No se pudo autenticar la cuenta. Inténtalo de nuevo.";
    }
}
