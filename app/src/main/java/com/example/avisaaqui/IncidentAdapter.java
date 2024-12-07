package com.example.avisaaqui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class IncidentAdapter extends RecyclerView.Adapter<IncidentAdapter.IncidentViewHolder> {
    private List<Incident> incidentList;

    public IncidentAdapter(List<Incident> incidentList) {
        this.incidentList = incidentList;
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
        holder.textDate.setText("Data: " + incident.getDtRegister());
    }

    @Override
    public int getItemCount() {
        return incidentList.size();
    }

    public static class IncidentViewHolder extends RecyclerView.ViewHolder {
        TextView textId, textValue, textDate;

        public IncidentViewHolder(@NonNull View itemView) {
            super(itemView);
            textId = itemView.findViewById(R.id.text_id);
            textValue = itemView.findViewById(R.id.text_value);
            textDate = itemView.findViewById(R.id.text_date);
        }
    }
}
