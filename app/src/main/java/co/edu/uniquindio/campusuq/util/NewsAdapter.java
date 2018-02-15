package co.edu.uniquindio.campusuq.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

import co.edu.uniquindio.campusuq.R;
import co.edu.uniquindio.campusuq.activity.NewsActivity;
import co.edu.uniquindio.campusuq.vo.New;

/**
 * Created by Juan Camilo on 8/02/2018.
 */

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewViewHolder> {

    private ArrayList<New> mNews;
    private static NewsAdapter.OnClickNewListener listener;


    public NewsAdapter(ArrayList<New> mNews, NewsActivity newsActivity) {
        this.mNews = mNews;
        listener = (NewsAdapter.OnClickNewListener) newsActivity;
    }

    public static class NewViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public LinearLayout layout;
        public ImageView image;
        public TextView date;
        public TextView author;
        public TextView title;
        public TextView summary;
        public Button more;
        public ImageView facebook;
        public ImageView twitter;
        public ImageView whatsapp;

        public NewViewHolder(View newView) {
            super(newView);

            layout = (LinearLayout) newView.findViewById(R.id.new_layout);
            layout.setOnClickListener(this);
            image = (ImageView) newView.findViewById(R.id.new_image);
            date = (TextView) newView.findViewById(R.id.new_date);
            author = (TextView) newView.findViewById(R.id.new_author);
            title = (TextView) newView.findViewById(R.id.new_title);
            summary = (TextView) newView.findViewById(R.id.new_summary);
            more = (Button) newView.findViewById(R.id.new_more_button);
            more.setOnClickListener(this);
            facebook = (ImageView) newView.findViewById(R.id.new_facebook_button);
            facebook.setOnClickListener(this);
            twitter = (ImageView) newView.findViewById(R.id.new_twitter_button);
            twitter.setOnClickListener(this);
            whatsapp = (ImageView) newView.findViewById(R.id.new_whatsapp_button);
            whatsapp.setOnClickListener(this);

        }

        public void bindItem(New n) {
            File imgFile = new  File(n.getImage());
            if(imgFile.exists()){
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                image.setImageBitmap(myBitmap);
            }
            date.setText(n.getDate());
            author.setText(n.getAuthor());
            title.setText(n.getName());
            summary.setText(n.getSummary());
        }

        @Override
        public void onClick(View v) {
            String action;
            switch(v.getId()) {
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
    public NewsAdapter.NewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View newView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.new_detail, parent, false);
        NewsAdapter.NewViewHolder newVH = new NewsAdapter.NewViewHolder(newView);
        return newVH;
    }

    @Override
    public void onBindViewHolder(NewsAdapter.NewViewHolder holder, int position) {
        New mNew = mNews.get(position);
        holder.bindItem(mNew);
    }

    @Override
    public int getItemCount() {
        return mNews.size();
    }

    public void setNews(ArrayList<New> mNews) {
        this.mNews = mNews;
        notifyDataSetChanged();
    }

    public interface OnClickNewListener {
        void onNewClick(int pos, String action);
    }

}
