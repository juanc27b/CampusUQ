package co.edu.uniquindio.campusuq.util;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    private OnClickAnnouncementListener listener;


    public AnnouncementsAdapter(ArrayList<Announcement> announcements, AnnouncementsActivity announcementsActivity) {
        this.announcements = announcements;
        listener = announcementsActivity;
    }

    public class AnnouncementViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView image;
        private TextView date;
        private TextView name;
        private TextView description;
        private TextView read;
        private ImageView facebook;
        private ImageView twitter;
        private ImageView whatsapp;

        AnnouncementViewHolder(View view) {
            super(view);

            image = view.findViewById(R.id.announcement_image);
            date = view.findViewById(R.id.announcement_date);
            name = view.findViewById(R.id.announcement_name);
            description = view.findViewById(R.id.announcement_description);
            read = view.findViewById(R.id.announcement_read_button);
            facebook = view.findViewById(R.id.announcement_facebook_button);
            twitter = view.findViewById(R.id.announcement_twitter_button);
            whatsapp = view.findViewById(R.id.announcement_whatsapp_button);

            view.findViewById(R.id.announcement_layout).setOnClickListener(this);
            read.setOnClickListener(this);
            facebook.setOnClickListener(this);
            twitter.setOnClickListener(this);
            whatsapp.setOnClickListener(this);
        }

        void bindItem(Announcement announcement) {
            /*File imageFile = new File(announcement.getImage());
            if (imageFile.exists()) image.setImageBitmap(BitmapFactory.decodeFile(imageFile.getAbsolutePath()));
            else image.setImageResource(R.drawable.rectangle_gray);*/

            date.setText(announcement.getDate());
            name.setText(announcement.getName());
            description.setText(announcement.getDescription());
        }

        @Override
        public void onClick(View view) {
            String action;
            switch (view.getId()) {
                case R.id.announcement_layout:
                    action = "notice";
                    break;
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
        return new AnnouncementViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.announcement_detail, parent, false));
    }

    @Override
    public void onBindViewHolder(AnnouncementsAdapter.AnnouncementViewHolder holder, int position) {
        holder.bindItem(announcements.get(position));
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
