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
 * Adaptador de anuncios.
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

    /**
     * Constructor que asigna los arreglos de anuncios y enlaces de anuncios iniciales y la interfaz
     * para redireccionar el procesamiento del click en un anuncio.
     * @param announcements Arreglo de anuncios.
     * @param announcementsLinks Arreglo de enlaces de anuncios.
     * @param announcementsActivity Actividad que implementa la interfaz OnClickQuotaListener.
     */
    AnnouncementsAdapter(ArrayList<Announcement> announcements,
                                ArrayList<AnnouncementLink> announcementsLinks,
                                AnnouncementsActivity announcementsActivity) {
        this.announcements = announcements;
        this.announcementsLinks = announcementsLinks;
        this.listener = announcementsActivity;
    }

    /**
     * Portador de vistas de anuncios, el cual está atento a clicks.
     */
    public class AnnouncementViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener {

        private ImageView[] images;
        private TextView date;
        private TextView name;
        private TextView description;

        /**
         * Obtiene los objetos de vista a partir de sus identificadores y asigna los listener de
         * click.
         * @param view Vista de un item en la cual buscar las subvistas que se controlan en el
         *             adaptador.
         */
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

        /**
         * Asigna los valores de las vistas a partir del anuncio y el arreglo de enlaces de anuncio
         * suministrado.
         * @param announcement Anuncio a visualizar.
         * @param announcementLinks Enlaces de anuncio a visualizar.
         */
        void bindItem(Announcement announcement, ArrayList<AnnouncementLink> announcementLinks) {
            for (int i = 0; i < images.length; i++) {
                if (i < announcementLinks.size()) {
                    // Se concatena una cadena vacia para evitar el caso File(null)
                    File linkFile = new File("" + announcementLinks.get(i).getLink());

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

        /**
         * Redirige hacia la funcion en la actividad que procesara el click en el anuncio asignando
         * una accion que depende de a cual vista se le ha dado click.
         * @param view Vista en la que se ha hecho click.
         */
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

    /**
     * Crea el portador de anuncios inflando su diseño.
     * @param parent Vista donde inflar el portador de cupos.
     * @param viewType Tipo de la vista (no utilizado).
     * @return Nuevo portador de anuncios creado.
     */
    @Override
    public AnnouncementViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new AnnouncementViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.announcement_detail, parent, false));
    }

    /**
     * Vincula los valores del anuncio con sus vistas.
     * @param holder Portador de anuncios.
     * @param position Posición del anuncio en el arreglo de anuncios.
     */
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

    /**
     * Obtiene la cantidad de ítems en el arreglo de anuncios.
     * @return Cantidad de ítems.
     */
    @Override
    public int getItemCount() {
        return announcements.size();
    }

    /**
     * Obtiene el arrego de anuncios.
     * @return Arreglo de anuncios
     */
    public ArrayList<Announcement> getAnnouncements() {
        return announcements;
    }

    /**
     * Asigna los nuevos arreglos de anuncios y enlaces de anuncios y notifica que los datos han
     * cambiado.
     * @param announcements Arreglo de anuncios.
     * @param announcementsLinks Arreglo de enlaces de anuncios.
     */
    void setAnnouncements(ArrayList<Announcement> announcements,
                                 ArrayList<AnnouncementLink> announcementsLinks) {
        this.announcements = announcements;
        this.announcementsLinks = announcementsLinks;
        notifyDataSetChanged();
    }

    /**
     * Obtiene el arrego de enlaces de anuncios.
     * @return Arreglo de enlaces de anuncios
     */
    ArrayList<AnnouncementLink> getAnnouncementsLinks() {
        return announcementsLinks;
    }

    /**
     * Interfaz para ser implementada por una actividad hacia la cual se redireccionará el
     * procesamiento del click.
     */
    public interface OnClickAnnouncementListener {
        void onAnnouncementClick(int pos, String action);
    }

}
