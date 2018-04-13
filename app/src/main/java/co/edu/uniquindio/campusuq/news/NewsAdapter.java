package co.edu.uniquindio.campusuq.news;

import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import co.edu.uniquindio.campusuq.R;

/**
 * Created by Juan Camilo on 8/02/2018.
 */

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewViewHolder> {

    static final String NOTICE    = "notice";
    static final String IMAGE     = "image";
    static final String MORE      = "more";
    static final String FACEBOOK  = "facebook";
    static final String TWITTER   = "twitter";
    static final String WHATSAPP  = "whatsapp";
    static final String UNDEFINED = "undefined";

    private ArrayList<New> news;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm:ssZ", new Locale("es", "CO"));
    private OnClickNewListener listener;

    NewsAdapter(ArrayList<New> news, NewsActivity newsActivity) {
        this.news = news;
        listener = newsActivity;
    }

    public class NewViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView image;
        private TextView date;
        private TextView author;
        private TextView title;
        private TextView summary;

        NewViewHolder(View view) {
            super(view);

            image = view.findViewById(R.id.new_image);
            date = view.findViewById(R.id.new_date);
            author = view.findViewById(R.id.new_author);
            title = view.findViewById(R.id.new_title);
            summary = view.findViewById(R.id.new_summary);

            view.findViewById(R.id.new_layout).setOnClickListener(this);
            image.setOnClickListener(this);
            view.findViewById(R.id.new_more_button).setOnClickListener(this);
            view.findViewById(R.id.new_facebook_button).setOnClickListener(this);
            view.findViewById(R.id.new_twitter_button).setOnClickListener(this);
            view.findViewById(R.id.new_whatsapp_button).setOnClickListener(this);
        }

        void bindItem(New n) {
            File imageFile = new  File("" + n.getImage());
            if (imageFile.exists())
                image.setImageBitmap(BitmapFactory.decodeFile(imageFile.getAbsolutePath()));
            else image.setImageResource(R.drawable.rectangle_gray);

            try {
                date.setText(DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT)
                        .format(simpleDateFormat.parse(n.getDate())));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            author.setText(n.getAuthor());
            title.setText(n.getName());
            summary.setText(n.getSummary());
        }

        @Override
        public void onClick(View view) {
            String action;

            switch (view.getId()) {
                case R.id.new_layout         : action = NOTICE   ; break;
                case R.id.new_image          : action = IMAGE    ; break;
                case R.id.new_more_button    : action = MORE     ; break;
                case R.id.new_facebook_button: action = FACEBOOK ; break;
                case R.id.new_twitter_button : action = TWITTER  ; break;
                case R.id.new_whatsapp_button: action = WHATSAPP ; break;
                default                      : action = UNDEFINED; break;
            }

            listener.onNewClick(getAdapterPosition(), action);
        }

    }

    @Override
    public NewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new NewViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.new_detail, parent, false));
    }

    @Override
    public void onBindViewHolder(NewViewHolder holder, int position) {
        holder.bindItem(news.get(position));
    }

    @Override
    public int getItemCount() {
        return news.size();
    }

    public void setNews(ArrayList<New> news) {
        this.news = news;
        notifyDataSetChanged();
    }

    public interface OnClickNewListener {
        void onNewClick(int index, String action);
    }

}
