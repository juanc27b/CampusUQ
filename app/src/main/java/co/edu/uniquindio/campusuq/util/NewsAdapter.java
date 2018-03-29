package co.edu.uniquindio.campusuq.util;

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
import java.util.Date;

import co.edu.uniquindio.campusuq.R;
import co.edu.uniquindio.campusuq.activity.NewsActivity;
import co.edu.uniquindio.campusuq.vo.New;

/**
 * Created by Juan Camilo on 8/02/2018.
 */

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewViewHolder> {

    private ArrayList<New> news;
    private OnClickNewListener listener;


    public NewsAdapter(ArrayList<New> news, NewsActivity newsActivity) {
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
            view.findViewById(R.id.new_more_button).setOnClickListener(this);
            view.findViewById(R.id.new_facebook_button).setOnClickListener(this);
            view.findViewById(R.id.new_twitter_button).setOnClickListener(this);
            view.findViewById(R.id.new_whatsapp_button).setOnClickListener(this);

        }

        void bindItem(New n) {
            File imageFile = new  File(n.getImage());
            if (imageFile.exists())
                image.setImageBitmap(BitmapFactory.decodeFile(imageFile.getAbsolutePath()));
            else image.setImageResource(R.drawable.rectangle_gray);

            try {
                Date d = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(n.getDate());
                date.setText(String.format("%s\n%s",
                        DateFormat.getDateInstance(DateFormat.MEDIUM).format(d),
                        DateFormat.getTimeInstance(DateFormat.SHORT).format(d)));
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
                case R.id.new_layout:
                    action = "notice";
                    break;
                case R.id.new_more_button:
                    action = "more";
                    break;
                case R.id.new_facebook_button:
                    action = "facebook";
                    break;
                case R.id.new_twitter_button:
                    action = "twitter";
                    break;
                case R.id.new_whatsapp_button:
                    action = "whatsapp";
                    break;
                default:
                    action = "undefined";
                    break;
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
        void onNewClick(int pos, String action);
    }

}
