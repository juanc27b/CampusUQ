package co.edu.uniquindio.campusuq.announcements;

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

/**
 * Created by Juan Camilo on 2/03/2018.
 */

public class AnnouncementsAdapter extends
        RecyclerView.Adapter<AnnouncementsAdapter.AnnouncementViewHolder> {

    static final String ANNOUNCEMENT = "announcement";
    static final String READ         = "read";
    static final String FACEBOOK     = "facebook";
    static final String TWITTER      = "twitter";
    static final String WHATSAPP     = "whatsapp";
    static final String UNDEFINED    = "undefined";

    private ArrayList<Announcement> announcements;
    private ArrayList<AnnouncementLink> announcementsLinks;
    private OnClickAnnouncementListener listener;

    AnnouncementsAdapter(ArrayList<Announcement> announcements,
                                ArrayList<AnnouncementLink> announcementsLinks,
                                AnnouncementsActivity announcementsActivity) {
        this.announcements = announcements;
        this.announcementsLinks = announcementsLinks;
        this.listener = announcementsActivity;
    }

    public class AnnouncementViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener {

        private ImageView[] images;
        private TextView date;
        private TextView name;
        private TextView description;

        AnnouncementViewHolder(View view) {
            super(view);

            images = new ImageView[]{
                    view.findViewById(R.id.announcement_image_0),
                    view.findViewById(R.id.announcement_image_1),
                    view.findViewById(R.id.announcement_image_2),
                    view.findViewById(R.id.announcement_image_3),
                    view.findViewById(R.id.announcement_image_4),
                    view.findViewById(R.id.announcement_image_5),
                    view.findViewById(R.id.announcement_image_6),
                    view.findViewById(R.id.announcement_image_7),
                    view.findViewById(R.id.announcement_image_8),
                    view.findViewById(R.id.announcement_image_9),
            };
            date = view.findViewById(R.id.announcement_date);
            name = view.findViewById(R.id.announcement_name);
            description = view.findViewById(R.id.announcement_description);

            view.findViewById(R.id.announcement_layout).setOnClickListener(this);
            view.findViewById(R.id.announcement_read_button).setOnClickListener(this);
            view.findViewById(R.id.announcement_facebook_button).setOnClickListener(this);
            view.findViewById(R.id.announcement_twitter_button).setOnClickListener(this);
            view.findViewById(R.id.announcement_whatsapp_button).setOnClickListener(this);
        }

        void bindItem(Announcement announcement, ArrayList<AnnouncementLink> announcementLinks) {
            for (int i = 0; i < images.length; i++) {
                if (i < announcementLinks.size()) {
                    File imageFile = new File(announcementLinks.get(i).getLink());
                    if (imageFile.exists()) images[i].setImageBitmap(BitmapFactory
                            .decodeFile(imageFile.getAbsolutePath()));
                    else images[i].setImageResource(R.drawable.rectangle_gray);
                    images[i].setVisibility(View.VISIBLE);
                } else {
                    images[i].setImageResource(R.drawable.rectangle_gray);
                    images[i].setVisibility(i != 0 ? View.GONE : View.VISIBLE);
                }
            }

            try {
                Date d = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                        .parse(announcement.getDate());
                date.setText(String.format("%s\n%s",
                        DateFormat.getDateInstance(DateFormat.FULL).format(d),
                        DateFormat.getTimeInstance(DateFormat.SHORT).format(d)));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            name.setText(announcement.getName());
            description.setText(announcement.getDescription());
        }

        @Override
        public void onClick(View view) {
            String action;
            switch (view.getId()) {
                case R.id.announcement_layout         : action = ANNOUNCEMENT; break;
                case R.id.announcement_read_button    : action = READ        ; break;
                case R.id.announcement_facebook_button: action = FACEBOOK    ; break;
                case R.id.announcement_twitter_button : action = TWITTER     ; break;
                case R.id.announcement_whatsapp_button: action = WHATSAPP    ; break;
                default                               : action = UNDEFINED   ; break;
            }
            listener.onAnnouncementClick(getAdapterPosition(), action);
        }

    }

    @Override
    public AnnouncementViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new AnnouncementViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.announcement_detail, parent, false));
    }

    @Override
    public void onBindViewHolder(AnnouncementViewHolder holder, int position) {
        Announcement announcement = announcements.get(position);
        ArrayList<AnnouncementLink> announcementLinks = new ArrayList<>();
        for (AnnouncementLink announcementLink : announcementsLinks)
            if (announcementLink.getAnnouncement_ID() == announcement.get_ID())
                announcementLinks.add(announcementLink);
        holder.bindItem(announcement, announcementLinks);
    }

    @Override
    public int getItemCount() {
        return announcements.size();
    }

    void setAnnouncements(ArrayList<Announcement> announcements,
                                 ArrayList<AnnouncementLink> announcementsLinks) {
        this.announcements = announcements;
        this.announcementsLinks = announcementsLinks;
        notifyDataSetChanged();
    }

    public interface OnClickAnnouncementListener {
        void onAnnouncementClick(int pos, String action);
    }

}
