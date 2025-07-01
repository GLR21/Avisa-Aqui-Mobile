package com.example.avisaaqui;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class IncidentAdapter extends RecyclerView.Adapter<IncidentAdapter.IncidentViewHolder> {
    private List<Incident> incidentList;
    private List<SpinnerItem> categoriaList;
    private Context context;
    private ApiService apiService;

    public IncidentAdapter(List<Incident> incidentList, List<SpinnerItem> categoriaList, Context context) {
        this.incidentList = incidentList;
        this.categoriaList = categoriaList;
        this.context = context;
        this.apiService = new ApiService(context);
    }

    @NonNull
    @Override
    public IncidentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_incident, parent, false);
        return new IncidentViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull IncidentViewHolder holder, int position) {
        Incident incident = incidentList.get(position);

        holder.textId.setText("ID: " + incident.getId());
        holder.textValue.setText("Descrição: " + incident.getValue());

        // Buscar a descrição da categoria
        String categoriaDescricao = "Categoria: " + incident.getRefCategory(); // valor padrão
        for (SpinnerItem item : categoriaList) {
            if (item.getValue().equals(String.valueOf(incident.getRefCategory()))) {
                categoriaDescricao = "Categoria: " + item.getDescription();
                break;
            }
        }
        holder.textCategory.setText(categoriaDescricao);
        holder.textCategory.setTypeface(null, Typeface.BOLD);

        holder.textLocation.setText("Local: " + incident.getLatitude() + ", " + incident.getLongitude());
        holder.textDate.setText("Data: " + incident.getDtRegister());
        holder.textStatus.setText(incident.isActive() ? "Ativo" : "Resolvido");

        // Mostra o botão apenas se o incidente estiver ativo
        holder.btnResolver.setVisibility(incident.isActive() ? View.VISIBLE : View.GONE);

        holder.btnResolver.setOnClickListener(v -> {
            int adapterPosition = holder.getAdapterPosition();
            if (adapterPosition == RecyclerView.NO_POSITION) return;

            Incident currentIncident = incidentList.get(adapterPosition);
            apiService.resolverIncident(currentIncident.getId(), new ApiService.ApiCallback<String>() {
                @Override
                public void onSuccess(String response) {
                    new Handler(Looper.getMainLooper()).post(() -> {
                        Toast.makeText(context, "Incidente resolvido com sucesso", Toast.LENGTH_SHORT).show();
                        if (adapterPosition >= 0 && adapterPosition < incidentList.size()) {
                            incidentList.remove(adapterPosition);
                            notifyItemRemoved(adapterPosition);
                            notifyItemRangeChanged(adapterPosition, incidentList.size());
                        }
                    });
                }

                @Override
                public void onFailure(String error) {
                    new Handler(Looper.getMainLooper()).post(() -> {
                        Toast.makeText(context, "Erro ao resolver incidente: " + error, Toast.LENGTH_SHORT).show();
                    });
                }
            });
        });
    }


    private String getCategoriaDescricao(int categoryId) {
        for (SpinnerItem item : categoriaList) {
            try {
                if (Integer.parseInt(item.getValue()) == categoryId) {
                    return item.getDescription();
                }
            } catch (NumberFormatException e) {
                Log.e("IncidentAdapter", "Erro ao converter categoria: " + e.getMessage());
            }
        }
        return "Categoria desconhecida";
    }

    @Override
    public int getItemCount() {
        return incidentList.size();
    }

    public void updateIncidentList(List<Incident> newIncidentList) {
        this.incidentList = newIncidentList;
        notifyDataSetChanged();
    }

    public void addIncident(Incident incident) {
        incidentList.add(incident);
        notifyItemInserted(incidentList.size() - 1);
    }

    public void removeIncidentById(int incidentId) {
        for (int i = 0; i < incidentList.size(); i++) {
            if (incidentList.get(i).getId() == incidentId) {
                incidentList.remove(i);
                notifyItemRemoved(i);
                notifyItemRangeChanged(i, incidentList.size());
                break;
            }
        }
    }

    public static class IncidentViewHolder extends RecyclerView.ViewHolder {
        TextView textId, textValue, textCategory, textLocation, textDate, textStatus;
        Button btnResolver;

        public IncidentViewHolder(@NonNull View itemView) {
            super(itemView);
            textId = itemView.findViewById(R.id.text_id);
            textValue = itemView.findViewById(R.id.text_value);
            textCategory = itemView.findViewById(R.id.text_category);
            textLocation = itemView.findViewById(R.id.text_location);
            textDate = itemView.findViewById(R.id.text_date);
            textStatus = itemView.findViewById(R.id.text_status);
            btnResolver = itemView.findViewById(R.id.btn_resolver);
        }
    }
}
