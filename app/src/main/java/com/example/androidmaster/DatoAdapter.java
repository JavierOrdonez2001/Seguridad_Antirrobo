package com.example.androidmaster;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import com.example.androidmaster.Dato;



public class DatoAdapter extends RecyclerView.Adapter<DatoAdapter.DatoViewHolder> {

    private List<Dato> listaDatos;

    public DatoAdapter(List<Dato> listaDatos) {
        this.listaDatos = listaDatos;
    }

    @NonNull
    @Override
    public DatoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dato, parent, false);
        return new DatoViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull DatoViewHolder holder, int position) {
        holder.tvDato.setText(listaDatos.get(position).getValor());
    }

    @Override
    public int getItemCount() {
        return listaDatos.size();
    }

    public static class DatoViewHolder extends RecyclerView.ViewHolder {
        TextView tvDato;

        public DatoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDato = itemView.findViewById(R.id.tvDato);
        }
    }
}
