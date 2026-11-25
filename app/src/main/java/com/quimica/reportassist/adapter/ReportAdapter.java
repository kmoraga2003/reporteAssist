package com.quimica.reportassist.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.quimica.reportassist.R;
import com.quimica.reportassist.model.ReportItem;

import java.util.ArrayList;
import java.util.List;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ReportViewHolder> {

    public enum ReportType { LATE, EARLY, ABSENT }

    private final Context context;
    private final List<ReportItem> items = new ArrayList<>();
    private ReportType currentType = ReportType.LATE;

    public ReportAdapter(Context context) {
        this.context = context;
    }

    public void updateData(List<ReportItem> newItems, ReportType type) {
        this.currentType = type;
        this.items.clear();
        if (newItems != null) {
            this.items.addAll(newItems);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_report_card, parent, false);
        return new ReportViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportViewHolder holder, int position) {
        ReportItem item = items.get(position);

        holder.tvEmployeeName.setText(item.getEmployeeName() != null ? item.getEmployeeName() : "Sin nombre");

        String code = item.getEmployeeCode() != null ? item.getEmployeeCode() : "N/A";
        String dept = item.getDepartment() != null ? item.getDepartment() : "General";
        holder.tvDeptCode.setText(code + " • " + dept);

        holder.tvDate.setText("📅 " + (item.getDate() != null ? item.getDate() : "--"));

        if (item.getEventTime() != null && !item.getEventTime().isEmpty()) {
            holder.tvEventTime.setText("⏰ " + item.getEventTime());
            holder.tvEventTime.setVisibility(View.VISIBLE);
        } else {
            holder.tvEventTime.setVisibility(View.GONE);
        }

        // Configuración visual según el tipo de reporte
        if (currentType == ReportType.LATE) {
            holder.tvBadge.setText(item.getDetail() != null ? item.getDetail() : "Atraso");
            holder.tvBadge.setBackgroundResource(R.drawable.bg_badge_late);
            holder.tvBadge.setTextColor(ContextCompat.getColor(context, R.color.status_late));
        } else if (currentType == ReportType.EARLY) {
            holder.tvBadge.setText(item.getDetail() != null ? item.getDetail() : "Salida Temp.");
            holder.tvBadge.setBackgroundResource(R.drawable.bg_badge_early);
            holder.tvBadge.setTextColor(ContextCompat.getColor(context, R.color.status_early));
        } else {
            holder.tvBadge.setText("Inasistencia");
            holder.tvBadge.setBackgroundResource(R.drawable.bg_badge_absent);
            holder.tvBadge.setTextColor(ContextCompat.getColor(context, R.color.status_absent));
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ReportViewHolder extends RecyclerView.ViewHolder {
        TextView tvEmployeeName, tvDeptCode, tvBadge, tvDate, tvEventTime;

        public ReportViewHolder(@NonNull View itemView) {
            super(itemView);
            tvEmployeeName = itemView.findViewById(R.id.tvItemEmployeeName);
            tvDeptCode = itemView.findViewById(R.id.tvItemDeptCode);
            tvBadge = itemView.findViewById(R.id.tvReportBadge);
            tvDate = itemView.findViewById(R.id.tvItemDate);
            tvEventTime = itemView.findViewById(R.id.tvItemEventTime);
        }
    }
}
