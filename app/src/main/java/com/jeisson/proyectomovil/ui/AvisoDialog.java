package com.jeisson.proyectomovil.ui;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import com.jeisson.proyectomovil.R;
import com.google.android.material.button.MaterialButton;

/** Avisos compartidos con cierre explícito y ligados a la vida de la pantalla. */
public final class AvisoDialog {
    private AvisoDialog() {}

    public static void mostrar(AppCompatActivity activity, String titulo, String mensaje,
                               int icono, Runnable alCerrar) {
        abrir(activity, titulo, mensaje, icono, false, alCerrar);
    }

    public static void confirmarEliminacion(AppCompatActivity activity, String nombre, Runnable eliminar) {
        abrir(activity, "¿Eliminar cliente?", "¿Estás seguro de eliminar a " + nombre
                + "? Esta acción no se puede deshacer.", R.drawable.ic_aviso_eliminar, true, eliminar);
    }

    public static void opcionesCliente(AppCompatActivity activity, String nombre,
                                       Runnable modificar, Runnable eliminar) {
        if (activity.isFinishing() || activity.isDestroyed()) return;
        View contenido = activity.getLayoutInflater().inflate(R.layout.dialogo_aviso, null);
        ((TextView) contenido.findViewById(R.id.aviso_encabezado)).setText(R.string.opciones_encabezado);
        ((TextView) contenido.findViewById(R.id.aviso_titulo)).setText(nombre);
        ((TextView) contenido.findViewById(R.id.aviso_mensaje)).setText(R.string.opciones_mensaje);
        ((ImageView) contenido.findViewById(R.id.aviso_icono)).setImageResource(R.drawable.ic_aviso_usuario);
        AlertDialog dialogo = new AlertDialog.Builder(activity).setView(contenido).create();
        MaterialButton editar = contenido.findViewById(R.id.aviso_aceptar);
        editar.setText(R.string.opciones_modificar);
        editar.setIconResource(R.drawable.ic_opcion_modificar);
        editar.setIconTint(android.content.res.ColorStateList.valueOf(Color.rgb(21, 53, 81)));
        editar.setIconGravity(MaterialButton.ICON_GRAVITY_TEXT_START);
        editar.setOnClickListener(v -> {
            dialogo.dismiss();
            modificar.run();
        });
        View borrar = contenido.findViewById(R.id.aviso_secundario);
        borrar.setVisibility(View.VISIBLE);
        borrar.setOnClickListener(v -> {
            dialogo.dismiss();
            eliminar.run();
        });
        View cancelar = contenido.findViewById(R.id.aviso_cancelar);
        cancelar.setVisibility(View.VISIBLE);
        cancelar.setOnClickListener(v -> dialogo.dismiss());
        mostrarTarjeta(activity, dialogo);
    }

    private static void abrir(AppCompatActivity activity, String titulo, String mensaje,
                              int icono, boolean confirmacion, Runnable accion) {
        if (activity.isFinishing() || activity.isDestroyed()) return;
        View contenido = activity.getLayoutInflater().inflate(R.layout.dialogo_aviso, null);
        ((TextView) contenido.findViewById(R.id.aviso_titulo)).setText(titulo);
        ((TextView) contenido.findViewById(R.id.aviso_mensaje)).setText(mensaje);
        ((ImageView) contenido.findViewById(R.id.aviso_icono)).setImageResource(icono);
        AlertDialog dialogo = new AlertDialog.Builder(activity).setView(contenido)
                .setCancelable(confirmacion).create();
        TextView aceptar = contenido.findViewById(R.id.aviso_aceptar);
        aceptar.setText(confirmacion ? R.string.aviso_eliminar : R.string.aviso_entendido);
        aceptar.setOnClickListener(v -> {
            dialogo.dismiss();
            if (accion != null) accion.run();
        });
        View cancelar = contenido.findViewById(R.id.aviso_cancelar);
        cancelar.setVisibility(confirmacion ? View.VISIBLE : View.GONE);
        cancelar.setOnClickListener(v -> dialogo.dismiss());
        mostrarTarjeta(activity, dialogo);
    }

    private static void mostrarTarjeta(AppCompatActivity activity, AlertDialog dialogo) {
        DefaultLifecycleObserver observer = new DefaultLifecycleObserver() {
            @Override public void onDestroy(LifecycleOwner owner) { dialogo.dismiss(); }
        };
        activity.getLifecycle().addObserver(observer);
        dialogo.setOnDismissListener(d -> activity.getLifecycle().removeObserver(observer));
        dialogo.show();
        dialogo.setCanceledOnTouchOutside(false);
        Window window = dialogo.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            float density = activity.getResources().getDisplayMetrics().density;
            int ancho = activity.getResources().getDisplayMetrics().widthPixels;
            window.setLayout(Math.min((int) (360 * density), ancho - (int) (40 * density)),
                    WindowManager.LayoutParams.WRAP_CONTENT);
            window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            WindowManager.LayoutParams params = window.getAttributes();
            params.dimAmount = 0.65f;
            window.setAttributes(params);
        }
    }
}
