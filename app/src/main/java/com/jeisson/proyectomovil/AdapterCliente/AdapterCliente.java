package com.jeisson.proyectomovil.AdapterCliente;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jeisson.proyectomovil.R;
import com.jeisson.proyectomovil.ViewHolderCliente.ViewHolderCliente;
import com.jeisson.proyectomovil.clases.Cliente;

import java.util.ArrayList;
import java.util.List;

public class AdapterCliente extends RecyclerView.Adapter<ViewHolderCliente> {

    // =========================================================
    // LISTA PRINCIPAL
    // =========================================================

    private List<Cliente> listaClientes;

    // =========================================================
    // LISTENERS PARA LOS CLICS
    // =========================================================

    private OnClienteClickListener clickListener;

    private OnClienteLongClickListener longClickListener;

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public AdapterCliente(List<Cliente> listaClientes) {

        this.listaClientes = listaClientes;
    }

    // =========================================================
    // FILTRAR LISTA
    // =========================================================

    public void filtrarLista(ArrayList<Cliente> listaFiltrada) {

        this.listaClientes = listaFiltrada;

        notifyDataSetChanged();
    }

    // =========================================================
    // CLICK CORTO
    // =========================================================

    public void setOnClienteClickListener(
            OnClienteClickListener listener) {

        this.clickListener = listener;
    }

    // =========================================================
    // CLICK PROLONGADO
    // =========================================================

    public void setOnClienteLongClickListener(
            OnClienteLongClickListener listener) {

        this.longClickListener = listener;
    }

    // =========================================================
    // CREAR VISTA
    // =========================================================

    @NonNull
    @Override
    public ViewHolderCliente onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(
                        R.layout.iten_cliente,
                        parent,
                        false
                );

        return new ViewHolderCliente(view);
    }

    // =========================================================
    // MOSTRAR DATOS
    // =========================================================

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolderCliente holder,
            int position) {

        Cliente cliente = listaClientes.get(position);

        // =====================================================
        // MOSTRAR DATOS DEL CLIENTE
        // =====================================================

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

        // =====================================================
        // CLICK CORTO
        // =====================================================

        holder.itemView.setOnClickListener(v -> {

            if (clickListener != null) {

                clickListener.onClienteClick(cliente);
            }
        });

        // =====================================================
        // CLICK PROLONGADO
        // =====================================================

        holder.itemView.setOnLongClickListener(v -> {

            if (longClickListener != null) {

                longClickListener.onClienteLongClick(cliente);
            }

            // true = el clic prolongado fue manejado
            return true;
        });
    }

    // =========================================================
    // CANTIDAD DE ELEMENTOS
    // =========================================================

    @Override
    public int getItemCount() {

        return listaClientes.size();
    }

    // =========================================================
    // INTERFACE CLICK CORTO
    // =========================================================

    public interface OnClienteClickListener {

        void onClienteClick(Cliente cliente);
    }

    // =========================================================
    // INTERFACE CLICK PROLONGADO
    // =========================================================

    public interface OnClienteLongClickListener {

        void onClienteLongClick(Cliente cliente);
    }
}
