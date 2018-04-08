package co.edu.uniquindio.campusuq.emails;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import co.edu.uniquindio.campusuq.R;
import co.edu.uniquindio.campusuq.items.ItemsPresenter;

public class EmailsAdapter extends RecyclerView.Adapter<EmailsAdapter.EmailViewHolder> {

    private ArrayList<Email> emails;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm:ssZ", new Locale("es", "CO"));
    private OnClickEmailListener listener;

    EmailsAdapter(ArrayList<Email> emails, EmailsActivity emailsActivity) {
        this.emails = emails;
        listener = emailsActivity;
    }

    public class EmailViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView icon;
        private TextView date;
        private TextView from;
        private TextView content;

        EmailViewHolder(View view) {
            super(view);

            icon = view.findViewById(R.id.email_icon);
            date = view.findViewById(R.id.email_date);
            from = view.findViewById(R.id.email_from);
            content = view.findViewById(R.id.email_content);

            view.findViewById(R.id.email_layout).setOnClickListener(this);
        }

        void bindItem(Email email) {
            icon.setText(email.getFrom().isEmpty() ?
                    "" : email.getFrom().substring(0, 1).toUpperCase());
            icon.setBackgroundResource(ItemsPresenter.getColor());

            try {
                Date dateTime = simpleDateFormat.parse(email.getDate());
                date.setText(DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.SHORT)
                        .format(dateTime));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            from.setText(email.getFrom());
            content.setText(email.getSnippet());
        }

        @Override
        public void onClick(View view) {
            listener.onEmailClick(getAdapterPosition());
        }

    }

    @Override
    public EmailViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new EmailViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.email_detail, parent, false));
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
