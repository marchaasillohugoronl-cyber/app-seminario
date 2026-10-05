package com.jeisson.proyectomovil;

import androidx.activity.EdgeToEdge;
import androidx.annotation.LayoutRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowCompat;

public abstract class PantallaActivity extends AppCompatActivity {
    protected void mostrarPantalla(@LayoutRes int layout) {
        EdgeToEdge.enable(this);
        setContentView(layout);
        if (layout == R.layout.activity_agregar_cliente || layout == R.layout.activity_mis_datos
                || layout == R.layout.activity_dashboard) {
            boolean fondoClaro = layout != R.layout.activity_dashboard;
            WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView())
                    .setAppearanceLightStatusBars(fondoClaro);
            WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView())
                    .setAppearanceLightNavigationBars(fondoClaro);
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });
    }
}
