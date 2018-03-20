package co.edu.uniquindio.campusuq.util;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import co.edu.uniquindio.campusuq.R;
import co.edu.uniquindio.campusuq.activity.EmailsActivity;
import co.edu.uniquindio.campusuq.vo.Email;

public class EmailsAdapter extends RecyclerView.Adapter<EmailsAdapter.EmailViewHolder> {

    private ArrayList<Email> emails;
    private OnClickEmailListener listener;

    public EmailsAdapter(ArrayList<Email> emails, EmailsActivity emailsActivity) {
        this.emails = emails;
        listener = emailsActivity;
    }

    public class EmailViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView icon, date, from, content;

        EmailViewHolder(View view) {
            super(view);
            view.findViewById(R.id.email_layout).setOnClickListener(this);
            icon = view.findViewById(R.id.email_icon);
            date = view.findViewById(R.id.email_date);
            from = view.findViewById(R.id.email_from);
            content = view.findViewById(R.id.email_content);
        }

        void bindItem(Email email) {
            icon.setText(email.getFrom().isEmpty()? "" : email.getFrom().substring(0, 1).toUpperCase());
            icon.setBackgroundResource(ItemsPresenter.getColor());
            date.setText(email.getDate());
            from.setText(email.getFrom());
            content.setText(email.getContent());
        }

        @Override
        public void onClick(View view) {
            listener.onEmailClick(getAdapterPosition());
        }
    }

    @Override
    public EmailViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new EmailViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.email_detail, parent, false));
    }

    @Override
    public void onBindViewHolder(EmailViewHolder holder, int position) {
        holder.bindItem(emails.get(position));
    }

    @Override
    public int getItemCount() {
        return emails.size();
    }

    public void setEmails(ArrayList<Email> emails) {
        this.emails = emails;
        notifyDataSetChanged();
    }

    public interface OnClickEmailListener {
        void onEmailClick(int index);
    }

}
