package com.jeisson.proyectomovil.AdapterCliente;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jeisson.proyectomovil.R;
import com.jeisson.proyectomovil.ViewHolderCliente.ViewHolderCliente;
import com.jeisson.proyectomovil.clases.Cliente;

import java.util.List;

public class AdapterCliente extends RecyclerView.Adapter<ViewHolderCliente> {

    private final List<Cliente> listaClientes;

    public AdapterCliente(List<Cliente> listaClientes) {
        this.listaClientes = listaClientes;
    }

    @NonNull
    @Override
    public ViewHolderCliente onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.iten_cliente, parent, false);

        return new ViewHolderCliente(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolderCliente holder,
            int position) {

        Cliente cliente = listaClientes.get(position);

        holder.setearDatosCliente(
                cliente.getId_cliente(),
                cliente.getUid_cliente(),
                cliente.getNombres(),
                cliente.getApellidos(),
                cliente.getCorreo(),
                cliente.getDni(),
                cliente.getTelefono(),
                cliente.getDireccion()
        );
    }

    @Override
    public int getItemCount() {
        return listaClientes.size();
    }
}