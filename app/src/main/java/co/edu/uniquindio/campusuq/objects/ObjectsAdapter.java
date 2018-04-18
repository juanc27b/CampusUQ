package co.edu.uniquindio.campusuq.objects;

import android.content.Context;
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
import java.util.Locale;

import co.edu.uniquindio.campusuq.R;
import co.edu.uniquindio.campusuq.items.ItemsPresenter;
import co.edu.uniquindio.campusuq.users.UsersPresenter;
import co.edu.uniquindio.campusuq.users.User;
import co.edu.uniquindio.campusuq.util.Utilities;

/**
 * Adaptador de objetos perdidos.
 */
public class ObjectsAdapter extends RecyclerView.Adapter<ObjectsAdapter.ObjectViewHolder> {

    static final String OBJECT    = "object";
    static final String IMAGE     = "image";
    static final String READED    = "readed";
    static final String FOUND     = "found";
    static final String NOT_FOUND = "not_found";
    static final String CONTACT   = "contact";

    private ArrayList<LostObject> objects;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm:ssZ", new Locale("es", "CO"));
    private OnClickObjectListener listener;
    private Context context;
    private User user;

    /**
     * Constructor que asigna el arreglo de objetos perdidos inicial y la interfaz para
     * redireccionar el procesamiento del click en un objeto perdido.
     * @param objects Arreglo de objetos perdidos.
     * @param objectsActivity Actividad que implementa la interfaz OnClickQuotaListener.
     */
    ObjectsAdapter(ArrayList<LostObject> objects, ObjectsActivity objectsActivity) {
        this.objects = objects;
        listener = objectsActivity;
        context = objectsActivity.getApplicationContext();
        user = UsersPresenter.loadUser(context);
    }

    /**
     * Portador de vistas de objetos perdidos, el cual está atento a clicks.
     */
    public class ObjectViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView icon;
        private TextView name;
        private TextView place;
        private TextView dateLost;
        private TextView timeLost;
        private ImageView image;
        private TextView description;
        private TextView found;

        /**
         * Obtiene los objetos de vista a partir de sus identificadores y asigna los listener de
         * click.
         * @param view Vista de un item en la cual buscar las subvistas que se controlan en el
         *             adaptador.
         */
        ObjectViewHolder(View view) {
            super(view);

            icon = view.findViewById(R.id.object_icon);
            name = view.findViewById(R.id.object_name);
            place = view.findViewById(R.id.object_place);
            dateLost = view.findViewById(R.id.object_date_lost);
            timeLost = view.findViewById(R.id.object_time_lost);
            image = view.findViewById(R.id.object_image);
            description = view.findViewById(R.id.object_description);
            found = view.findViewById(R.id.object_found);

            view.findViewById(R.id.object_layout).setOnClickListener(this);
            image.setOnClickListener(this);
            view.findViewById(R.id.object_readed).setOnClickListener(this);
        }

        /**
         * Asigna los valores de las vistas a partir del objeto perdido suministrado.
         * @param object Objeto perdido a visualizar.
         */
        void bindItem(LostObject object) {
            icon.setImageResource(ItemsPresenter.getColor());
            name.setText(object.getName());
            place.setText(object.getPlace());

            try {
                Date dateTimeLost = simpleDateFormat.parse(object.getDateLost());
                dateLost.setText(DateFormat.getDateInstance(DateFormat.LONG).format(dateTimeLost));
                timeLost.setText(DateFormat.getTimeInstance(DateFormat.SHORT).format(dateTimeLost));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            // Se concatena una cadena vacia para evitar el caso File(null)
            File imageFile = new File("" + object.getImage());
            if (imageFile.exists()) {
                image.setImageBitmap(Utilities
                        .getResizedBitmap(BitmapFactory.decodeFile(imageFile.getAbsolutePath())));
            } else {
                image.setImageResource(R.drawable.rectangle_gray);
            }

            description.setText(object.getDescription());

            if (user != null && !"campusuq@uniquindio.edu.co".equals(user.getEmail()) &&
                    object.getUserFound_ID() != null &&
                    object.getUserFound_ID().equals(user.get_ID())) {
                found.setText(R.string.object_not_found);
                found.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onObjectClick(getAdapterPosition(), NOT_FOUND);
                    }
                });
            } else if (user != null && !"campusuq@uniquindio.edu.co".equals(user.getEmail()) &&
                    object.getUserLost_ID().equals(user.get_ID()) &&
                    object.getUserFound_ID() != null) {
                found.setText(R.string.object_view_contact);
                found.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onObjectClick(getAdapterPosition(), CONTACT);
                    }
                });
            } else {
                found.setText(R.string.object_report_found);
                found.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onObjectClick(getAdapterPosition(), FOUND);
                    }
                });
            }
        }

        /**
         * Redirige hacia la funcion en la actividad que procesara el click en el objeto perdido
         * asignando una accion que depende de a cual vista se le ha dado click.
         * @param view Vista en la que se ha hecho click.
         */
        @Override
        public void onClick(View view) {
            String action;

            switch (view.getId()) {
                case R.id.object_layout: action = OBJECT     ; break;
                case R.id.object_image : action = IMAGE      ; break;
                case R.id.object_readed: action = READED     ; break;
                default                : action = "undefined"; break;
            }

            listener.onObjectClick(getAdapterPosition(), action);
        }

    }

    /**
     * Crea el portador de objetos perdidos inflando su diseño.
     * @param parent Vista donde inflar el portador de cupos.
     * @param viewType Tipo de la vista (no utilizado).
     * @return Nuevo portador de objetos perdidos creado.
     */
    @Override
    public ObjectViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ObjectViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.object_detail, parent, false));
    }

    /**
     * Vincula los valores del objeto perdido con sus vistas.
     * @param holder Portador de objetos perdidos.
     * @param position Posición del objeto perdido en el arreglo de objetos perdidos.
     */
    @Override
    public void onBindViewHolder(ObjectViewHolder holder, int position) {
        holder.bindItem(objects.get(position));
    }

    /**
     * Obtiene la cantidad de ítems en el arreglo de objetos perdidos.
     * @return Cantidad de ítems.
     */
    @Override
    public int getItemCount() {
        return objects.size();
    }

    /**
     * Asigna el nuevo arreglo de objetos perdidos y notifica que los datos han cambiado.
     * @param objects Arreglo de objetos perdidos.
     */
    public void setObjects(ArrayList<LostObject> objects) {
        user = UsersPresenter.loadUser(context);
        this.objects = objects;
        notifyDataSetChanged();
    }

    /**
     * Interfaz para ser implementada por una actividad hacia la cual se redireccionará el
     * procesamiento del click.
     */
    public interface OnClickObjectListener {
        void onObjectClick(int index, String action);
    }

}
