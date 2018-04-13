package co.edu.uniquindio.campusuq.announcements;

import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
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
import co.edu.uniquindio.campusuq.util.Utilities;

/**
 * Created by Juan Camilo on 2/03/2018.
 */

public class AnnouncementsAdapter extends
        RecyclerView.Adapter<AnnouncementsAdapter.AnnouncementViewHolder> {

    static final String ANNOUNCEMENT = "announcement";
    static final String IMAGE_0      = "image_0";
    static final String IMAGE_1      = "image_1";
    static final String IMAGE_2      = "image_2";
    static final String IMAGE_3      = "image_3";
    static final String IMAGE_4      = "image_4";
    static final String IMAGE_5      = "image_5";
    static final String IMAGE_6      = "image_6";
    static final String IMAGE_7      = "image_7";
    static final String IMAGE_8      = "image_8";
    static final String IMAGE_9      = "image_9";
    static final String READ         = "read";
    static final String FACEBOOK     = "facebook";
    static final String TWITTER      = "twitter";
    static final String WHATSAPP     = "whatsapp";
    static final String UNDEFINED    = "undefined";

    private ArrayList<Announcement> announcements;
    private ArrayList<AnnouncementLink> announcementsLinks;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm:ssZ", new Locale("es", "CO"));
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
            for (ImageView image : images) image.setOnClickListener(this);
            view.findViewById(R.id.announcement_read_button).setOnClickListener(this);
            view.findViewById(R.id.announcement_facebook_button).setOnClickListener(this);
            view.findViewById(R.id.announcement_twitter_button).setOnClickListener(this);
            view.findViewById(R.id.announcement_whatsapp_button).setOnClickListener(this);
        }

        void bindItem(Announcement announcement, ArrayList<AnnouncementLink> announcementLinks) {
            for (int i = 0; i < images.length; i++) {
                if (i < announcementLinks.size()) {
                    File linkFile = new File(announcementLinks.get(i).getLink());

                    if (linkFile.exists()) {
                        if ("I".equals(announcementLinks.get(i).getType())) {
                            images[i].setImageBitmap(Utilities.getResizedBitmap(BitmapFactory
                                    .decodeFile(linkFile.getAbsolutePath())));
                        } else {
                            MediaMetadataRetriever mediaMetadataRetriever =
                                    new MediaMetadataRetriever();
                            mediaMetadataRetriever.setDataSource(linkFile.getAbsolutePath());
                            images[i].setImageBitmap(Utilities.getResizedBitmap(
                                    mediaMetadataRetriever.getFrameAtTime(1000000)));
                        }
                    } else {
                        images[i].setImageResource(R.drawable.rectangle_gray);
                    }

                    images[i].setVisibility(View.VISIBLE);
                } else {
                    images[i].setImageResource(R.drawable.rectangle_gray);
                    images[i].setVisibility(i != 0 ? View.GONE : View.VISIBLE);
                }
            }

            try {
                date.setText(DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.SHORT)
                        .format(simpleDateFormat.parse(announcement.getDate())));
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
                case R.id.announcement_image_0        : action = IMAGE_0     ; break;
                case R.id.announcement_image_1        : action = IMAGE_1     ; break;
                case R.id.announcement_image_2        : action = IMAGE_2     ; break;
                case R.id.announcement_image_3        : action = IMAGE_3     ; break;
                case R.id.announcement_image_4        : action = IMAGE_4     ; break;
                case R.id.announcement_image_5        : action = IMAGE_5     ; break;
                case R.id.announcement_image_6        : action = IMAGE_6     ; break;
                case R.id.announcement_image_7        : action = IMAGE_7     ; break;
                case R.id.announcement_image_8        : action = IMAGE_8     ; break;
                case R.id.announcement_image_9        : action = IMAGE_9     ; break;
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
        for (AnnouncementLink announcementLink : announcementsLinks) {
            if (announcementLink.getAnnouncement_ID().equals(announcement.get_ID())) {
                announcementLinks.add(announcementLink);
            }
        }
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
