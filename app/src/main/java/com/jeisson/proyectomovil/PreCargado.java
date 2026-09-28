package com.jeisson.proyectomovil;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

public class PreCargado extends PantallaActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mostrarPantalla(R.layout.activity_pre_cargado);

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(PreCargado.this, MainActivity.class));
                finish();
            }
        }, 5000); // 5 segundos de retraso
    }
}
