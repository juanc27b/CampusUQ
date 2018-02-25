package co.edu.uniquindio.campusuq.util;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import co.edu.uniquindio.campusuq.R;
import co.edu.uniquindio.campusuq.activity.QuotasActivity;
import co.edu.uniquindio.campusuq.vo.Quota;

public class QuotasAdapter extends RecyclerView.Adapter<QuotasAdapter.QuotaViewHolder> {
    public static class QuotaViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView icon;
        public TextView name, quota;

        public QuotaViewHolder(View quotaView) {
            super(quotaView);
            icon = quotaView.findViewById(R.id.quota_icon);
            name = quotaView.findViewById(R.id.quota_name);
            quota = quotaView.findViewById(R.id.quota_quota);
        }

        public void bindItem(Quota q) {
            if(q.getQuota().equals("0")) icon.setImageResource(R.drawable.circle_gray);
            name.setText(q.getName());
            if(q.getType().equals("P")) name.setTextSize(12);
            quota.setText(q.getQuota());
        }

        @Override
        public void onClick(View view) {
            String action;
            switch(view.getId()) {
            default:
                action = "undefined";
            }
            listener.onQuotaClick(getAdapterPosition(), action);
        }
    }

    private ArrayList<Quota> mQuotas;
    private static QuotasAdapter.OnClickQuotaListener listener;

    public QuotasAdapter(ArrayList<Quota> mQuotas, QuotasActivity quotasActivity) {
        this.mQuotas = mQuotas;
        listener = quotasActivity;
    }

    @Override
    public QuotaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new QuotasAdapter.QuotaViewHolder(LayoutInflater.from(parent.getContext()).inflate(
            R.layout.quota_detail, parent, false
        ));
    }

    @Override
    public void onBindViewHolder(QuotaViewHolder holder, int position) {
        holder.bindItem(mQuotas.get(position));
    }

    @Override
    public int getItemCount() {
        return mQuotas.size();
    }

    public void setQuotas(ArrayList<Quota> mQuotas) {
        this.mQuotas = mQuotas;
        notifyDataSetChanged();
    }

    public interface OnClickQuotaListener {
        void onQuotaClick(int pos, String action);
    }
}