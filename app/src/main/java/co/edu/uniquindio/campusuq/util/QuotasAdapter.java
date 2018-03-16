package co.edu.uniquindio.campusuq.util;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import co.edu.uniquindio.campusuq.R;
import co.edu.uniquindio.campusuq.activity.QuotasActivity;
import co.edu.uniquindio.campusuq.vo.Quota;

public class QuotasAdapter extends RecyclerView.Adapter<QuotasAdapter.QuotaViewHolder> {

    private ArrayList<Quota> quotas;
    private OnClickQuotaListener listener;

    public QuotasAdapter(ArrayList<Quota> quotas, QuotasActivity quotasActivity) {
        this.quotas = quotas;
        listener = quotasActivity;
    }

    public class QuotaViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        private ImageView icon;
        private TextView name;
        private TextView quota;

        QuotaViewHolder(View view) {
            super(view);
            LinearLayout layout;
            layout = view.findViewById(R.id.quota_layout);
            layout.setOnClickListener(this);
            layout.setOnLongClickListener(this);
            icon = view.findViewById(R.id.quota_icon);
            name = view.findViewById(R.id.quota_name);
            quota = view.findViewById(R.id.quota_quota);
        }

        void bindItem(Quota q) {
            icon.setImageResource(q.getQuota().equals("0")? R.drawable.circle_gray : R.drawable.circle_green);
            name.setText(q.getName());
            name.setTextSize(q.getType().equals("S")? 20 : 15);
            quota.setText(q.getQuota());
        }

        @Override
        public void onClick(View view) {
            listener.onQuotaClick(false, getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View view) {
            listener.onQuotaClick(true, getAdapterPosition());
            return true;
        }
    }

    @Override
    public QuotaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new QuotaViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.quota_detail, parent, false));
    }

    @Override
    public void onBindViewHolder(QuotaViewHolder holder, int position) {
        holder.bindItem(quotas.get(position));
    }

    @Override
    public int getItemCount() {
        return quotas.size();
    }

    public void setQuotas(ArrayList<Quota> quotas) {
        this.quotas = quotas;
        notifyDataSetChanged();
    }

    public interface OnClickQuotaListener {
        void onQuotaClick(boolean long_click, int index);
    }

}
