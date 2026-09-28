package com.jeisson.proyectomovil.ViewHolderCliente;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jeisson.proyectomovil.R;

public class ViewHolderCliente extends RecyclerView.ViewHolder {

    View mview;

    public ViewHolderCliente(@NonNull View itemView) {
        super(itemView);
        mview = itemView;
    }

    public void setearDatosCliente(
            String id_cliente,
            String uid_cliente,
            String nombres,
            String apellidos,
            String correo,
            String dni,
            String telefono,
            String direccion) {

        TextView tvClienteI;
        TextView tviudClienteI;
        TextView tvNombresI;
        TextView tvApellidosI;
        TextView tvCorreoI;
        TextView tvTelefonoI;
        TextView tvDniI;
        TextView tvDireccionI;

        // Referencias a los elementos del XML
        tvClienteI = mview.findViewById(R.id.tvClienteI);
        tviudClienteI = mview.findViewById(R.id.tviudClienteI);
        tvNombresI = mview.findViewById(R.id.tvNombresI);
        tvApellidosI = mview.findViewById(R.id.tvApellidosI);
        tvCorreoI = mview.findViewById(R.id.tvCorreoI);
        tvDniI = mview.findViewById(R.id.tvDniI);
        tvTelefonoI = mview.findViewById(R.id.tvTelefonoI);
        tvDireccionI = mview.findViewById(R.id.tvDireccionI);

        // Mostrar los datos del cliente
        tvClienteI.setText(id_cliente);
        tviudClienteI.setText(uid_cliente);
        tvNombresI.setText(nombres);
        tvApellidosI.setText(apellidos);
        tvCorreoI.setText(correo);
        tvDniI.setText(dni);
        tvTelefonoI.setText(telefono);
        tvDireccionI.setText(direccion);
    }
}

