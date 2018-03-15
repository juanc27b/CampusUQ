package co.edu.uniquindio.campusuq.util;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import co.edu.uniquindio.campusuq.R;
import co.edu.uniquindio.campusuq.activity.AnnouncementsActivity;
import co.edu.uniquindio.campusuq.vo.Announcement;

/**
 * Created by Juan Camilo on 2/03/2018.
 */

public class AnnouncementsAdapter extends RecyclerView.Adapter<AnnouncementsAdapter.AnnouncementViewHolder> {

    private ArrayList<Announcement> announcements;
    private static AnnouncementsAdapter.OnClickAnnouncementListener listener;


    public AnnouncementsAdapter(ArrayList<Announcement> announcements, AnnouncementsActivity announcementsActivity) {
        this.announcements = announcements;
        listener = (AnnouncementsAdapter.OnClickAnnouncementListener) announcementsActivity;
    }

    public static class AnnouncementViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        //public LinearLayout layout;
        public ImageView image;
        public TextView date;
        public TextView name;
        public TextView description;
        public Button read;
        public ImageView facebook;
        public ImageView twitter;
        public ImageView whatsapp;

        public AnnouncementViewHolder(View announcementView) {
            super(announcementView);

            //layout = (LinearLayout) newView.findViewById(R.id.new_layout);
            //layout.setOnClickListener(this);
            image = (ImageView) announcementView.findViewById(R.id.announcement_image);
            date = (TextView) announcementView.findViewById(R.id.announcement_date);
            name = (TextView) announcementView.findViewById(R.id.announcement_name);
            description = (TextView) announcementView.findViewById(R.id.announcement_description);
            read = (Button) announcementView.findViewById(R.id.announcement_read_button);
            read.setOnClickListener(this);
            facebook = (ImageView) announcementView.findViewById(R.id.announcement_facebook_button);
            facebook.setOnClickListener(this);
            twitter = (ImageView) announcementView.findViewById(R.id.announcement_twitter_button);
            twitter.setOnClickListener(this);
            whatsapp = (ImageView) announcementView.findViewById(R.id.announcement_whatsapp_button);
            whatsapp.setOnClickListener(this);

        }

        public void bindItem(Announcement a) {
            /*
            File imgFile = new  File(n.getImage());
            if(imgFile.exists()){
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                image.setImageBitmap(myBitmap);
            }
            */
            date.setText(a.getDate());
            name.setText(a.getName());
            description.setText(a.getDescription());
        }

        @Override
        public void onClick(View v) {
            String action;
            switch(v.getId()) {
                /*
                case R.id.new_layout:
                    action = "notice";
                    break;
                */
                case R.id.announcement_read_button:
                    action = "read";
                    break;
                case R.id.announcement_facebook_button:
                    action = "facebook";
                    break;
                case R.id.announcement_twitter_button:
                    action = "twitter";
                    break;
                case R.id.announcement_whatsapp_button:
                    action = "whatsapp";
                    break;
                default:
                    action = "undefined";
                    break;
            }
            listener.onAnnouncementClick(getAdapterPosition(), action);
        }

    }

    @Override
    public AnnouncementsAdapter.AnnouncementViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View announcementView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.announcement_detail, parent, false);
        AnnouncementsAdapter.AnnouncementViewHolder announcementVH =
                new AnnouncementsAdapter.AnnouncementViewHolder(announcementView);
        return announcementVH;
    }

    @Override
    public void onBindViewHolder(AnnouncementsAdapter.AnnouncementViewHolder holder, int position) {
        Announcement announcement = announcements.get(position);
        holder.bindItem(announcement);
    }

    @Override
    public int getItemCount() {
        return announcements.size();
    }

    public void setAnnouncements(ArrayList<Announcement> announcements) {
        this.announcements = announcements;
        notifyDataSetChanged();
    }

    public interface OnClickAnnouncementListener {
        void onAnnouncementClick(int pos, String action);
    }

}
