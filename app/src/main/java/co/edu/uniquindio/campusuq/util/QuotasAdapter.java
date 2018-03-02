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
            quotaView.findViewById(R.id.quota_layout).setOnClickListener(this);
            icon = quotaView.findViewById(R.id.quota_icon);
            name = quotaView.findViewById(R.id.quota_name);
            quota = quotaView.findViewById(R.id.quota_quota);
        }

        public void bindItem(Quota q) {
            icon.setImageResource(q.getQuota().equals("0")? R.drawable.circle_gray : R.drawable.circle_green);
            name.setText(q.getName());
            name.setTextSize(q.getType().equals("S")? 20 : 15);
            quota.setText(q.getQuota());
        }

        @Override
        public void onClick(View view) {
            listener.onQuotaClick(getAdapterPosition());
        }
    }

    private ArrayList<Quota> mQuotas;
    private static OnClickQuotaListener listener;

    public QuotasAdapter(ArrayList<Quota> mQuotas, QuotasActivity quotasActivity) {
        this.mQuotas = mQuotas;
        listener = quotasActivity;
    }

    @Override
    public QuotaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new QuotaViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.quota_detail, parent, false));
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
        void onQuotaClick(int index);
    }
}