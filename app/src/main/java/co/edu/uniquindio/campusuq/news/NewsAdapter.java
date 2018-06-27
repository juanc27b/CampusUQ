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
 * Adaptador de noticias.
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

    /**
     * Constructor que asigna el arreglo de noticias y la interfaz para redireccionar el
     * procesamiento del click en una noticia.
     * @param news Arreglo de noticias.
     * @param newsActivity Actividad que implementa la interfaz OnClickNewListener.
     */
    NewsAdapter(ArrayList<New> news, NewsActivity newsActivity) {
        this.news = news;
        listener = newsActivity;
    }

    /**
     * Portador de vistas de noticias, el cual está atento a clicks.
     */
    public class NewViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView image;
        private TextView date;
        private TextView author;
        private TextView title;
        private TextView summary;

        /**
         * Obtiene los objetos de vista a partir de sus identificadores y asigna los listener de
         * click.
         * @param itemView Vista de un item en la cual buscar las subvistas que se controlan en el
         *                 adaptador.
         */
        NewViewHolder(View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.new_image);
            date = itemView.findViewById(R.id.new_date);
            author = itemView.findViewById(R.id.new_author);
            title = itemView.findViewById(R.id.new_title);
            summary = itemView.findViewById(R.id.new_summary);

            itemView.findViewById(R.id.new_layout).setOnClickListener(this);
            image.setOnClickListener(this);
            itemView.findViewById(R.id.new_more_button).setOnClickListener(this);
            itemView.findViewById(R.id.new_facebook_button).setOnClickListener(this);
            itemView.findViewById(R.id.new_twitter_button).setOnClickListener(this);
            itemView.findViewById(R.id.new_whatsapp_button).setOnClickListener(this);
        }

        /**
         * Asigna los valores de las vistas a partir de la noticia suministrada.
         * @param n Noticia a visualizar.
         */
        void bindItem(New n) {
            File imageFile = new  File("" + n.getImage());

            if (imageFile.exists()) {
                image.setImageBitmap(BitmapFactory.decodeFile(imageFile.getAbsolutePath()));
            } else {
                image.setImageResource(R.drawable.rectangle_gray);
            }

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

        /**
         * Redirige hacia la funcion en la actividad que procesara el click en la noticia asignando
         * una accion que depende de a cual vista se le ha dado click.
         * @param view Vista en la que se ha hecho click.
         */
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

    /**
     * Crea el portador de noticias inflando su diseño.
     * @param parent Vista donde inflar el portador de noticias.
     * @param viewType Tipo de la vista (no utilizado).
     * @return Nuevo portador de noticias creado.
     */
    @Override
    public NewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new NewViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.new_detail, parent, false));
    }

    /**
     * Vincula los valores de la noticia con sus vistas.
     * @param holder Portador de noticias.
     * @param position Posición de la noticia en el arreglo de noticias.
     */
    @Override
    public void onBindViewHolder(NewViewHolder holder, int position) {
        holder.bindItem(news.get(position));
    }

    /**
     * Obtiene la cantidad de ítems en el arreglo de noticias.
     * @return Cantidad de ítems.
     */
    @Override
    public int getItemCount() {
        return news.size();
    }

    /**
     * Obtiene el arrego de noticias.
     * @return Arreglo de noticias
     */
    public ArrayList<New> getNews() {
        return news;
    }

    /**
     * Asigna el nuevo arreglo de noticias y notifica que los datos han cambiado.
     * @param news Arreglo de noticias.
     */
    public void setNews(ArrayList<New> news) {
        this.news = news;
        notifyDataSetChanged();
    }

    /**
     * Interfaz para ser implementada por una actividad hacia la cual se redireccionará el
     * procesamiento del click.
     */
    public interface OnClickNewListener {
        void onNewClick(int index, String action);
    }

}
