package com.quimica.reportassist.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.quimica.reportassist.R;
import com.quimica.reportassist.model.AttendanceRecord;

import java.util.ArrayList;
import java.util.List;

public class EmployeeAttendanceAdapter extends RecyclerView.Adapter<EmployeeAttendanceAdapter.RecordViewHolder> {

    private final Context context;
    private final List<AttendanceRecord> records = new ArrayList<>();

    public EmployeeAttendanceAdapter(Context context) {
        this.context = context;
    }

    public void updateData(List<AttendanceRecord> newRecords) {
        this.records.clear();
        if (newRecords != null) {
            this.records.addAll(newRecords);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_employee_attendance, parent, false);
        return new RecordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecordViewHolder holder, int position) {
        AttendanceRecord item = records.get(position);

        holder.tvDate.setText("📅 " + (item.getAttendanceDate() != null ? item.getAttendanceDate() : "Fecha --"));
        holder.tvTimes.setText(item.getFormattedTimes());

        // Estado y Badge
        String status = item.getStatus() != null ? item.getStatus() : "NORMAL";
        if ("ON_TIME".equalsIgnoreCase(status) || "NORMAL".equalsIgnoreCase(status)) {
            holder.tvBadge.setText("A TIEMPO");
            holder.tvBadge.setBackgroundResource(R.drawable.bg_badge_ontime);
            holder.tvBadge.setTextColor(ContextCompat.getColor(context, R.color.status_ontime));
        } else if ("LATE".equalsIgnoreCase(status)) {
            holder.tvBadge.setText("ATRASADO");
            holder.tvBadge.setBackgroundResource(R.drawable.bg_badge_late);
            holder.tvBadge.setTextColor(ContextCompat.getColor(context, R.color.status_late));
        } else if ("EARLY_EXIT".equalsIgnoreCase(status)) {
            holder.tvBadge.setText("SALIDA TEMP.");
            holder.tvBadge.setBackgroundResource(R.drawable.bg_badge_early);
            holder.tvBadge.setTextColor(ContextCompat.getColor(context, R.color.status_early));
        } else {
            holder.tvBadge.setText(status);
            holder.tvBadge.setBackgroundResource(R.drawable.bg_badge_ontime);
            holder.tvBadge.setTextColor(ContextCompat.getColor(context, R.color.status_ontime));
        }

        holder.tvHours.setText(item.getNotes() != null && !item.getNotes().isEmpty() ? item.getNotes() : "Completada");
    }

    @Override
    public int getItemCount() {
        return records.size();
    }

    static class RecordViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvBadge, tvTimes, tvHours;

        public RecordViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvRecordDate);
            tvBadge = itemView.findViewById(R.id.tvRecordStatusBadge);
            tvTimes = itemView.findViewById(R.id.tvRecordTimes);
            tvHours = itemView.findViewById(R.id.tvRecordHours);
        }
    }
}
