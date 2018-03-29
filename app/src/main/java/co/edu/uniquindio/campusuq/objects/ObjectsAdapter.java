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
import java.util.ArrayList;

import co.edu.uniquindio.campusuq.R;
import co.edu.uniquindio.campusuq.items.ItemsPresenter;
import co.edu.uniquindio.campusuq.users.UsersPresenter;
import co.edu.uniquindio.campusuq.users.User;

public class ObjectsAdapter extends RecyclerView.Adapter<ObjectsAdapter.ObjectViewHolder> {

    static final String DIALOG    = "dialog";
    static final String READED    = "readed";
    static final String FOUND     = "found";
    static final String NOT_FOUND = "not_found";
    static final String CONTACT   = "contact";

    private ArrayList<LostObject> objects;
    private OnClickObjectListener listener;
    private Context context;
    private User user;

    ObjectsAdapter(ArrayList<LostObject> objects, ObjectsActivity objectsActivity) {
        this.objects = objects;
        listener = objectsActivity;
        context = objectsActivity.getApplicationContext();
        user = UsersPresenter.loadUser(context);
    }

    public class ObjectViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView icon;
        private TextView name;
        private TextView place;
        private TextView date;
        private ImageView image;
        private TextView description;
        private TextView found;

        ObjectViewHolder(View view) {
            super(view);

            icon = view.findViewById(R.id.object_icon);
            name = view.findViewById(R.id.object_name);
            place = view.findViewById(R.id.object_place);
            date = view.findViewById(R.id.object_date);
            image = view.findViewById(R.id.object_image);
            description = view.findViewById(R.id.object_description);
            found = view.findViewById(R.id.object_found);

            view.findViewById(R.id.object_layout).setOnClickListener(this);
            view.findViewById(R.id.object_readed).setOnClickListener(this);
        }

        void bindItem(LostObject lostObject) {
            icon.setImageResource(ItemsPresenter.getColor());
            name.setText(lostObject.getName());
            place.setText(lostObject.getPlace());
            date.setText(lostObject.getDate());

            // Se concatena una cadena vacia para evitar el caso File(null)
            File imageFile = new File(""+lostObject.getImage());
            if (imageFile.exists())
                image.setImageBitmap(BitmapFactory.decodeFile(imageFile.getAbsolutePath()));
            else image.setImageResource(R.drawable.rectangle_gray);

            description.setText(lostObject.getDescription());

            if (user != null && !user.getEmail().equals("campusuq@uniquindio.edu.co") &&
                    lostObject.getUserFound_ID() != null && lostObject.getUserFound_ID() == user.get_ID()) {
                found.setText(R.string.object_not_found);
                found.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onObjectClick(getAdapterPosition(), NOT_FOUND);
                    }
                });
            } else if (user != null && !user.getEmail().equals("campusuq@uniquindio.edu.co") &&
                    lostObject.getUserLost_ID() == user.get_ID() && lostObject.getUserFound_ID() != null) {
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

        @Override
        public void onClick(View view) {
            String action;
            switch (view.getId()) {
                case R.id.object_readed: action = READED; break;
                default                : action = DIALOG; break;
            }
            listener.onObjectClick(getAdapterPosition(), action);
        }
    }

    @Override
    public ObjectViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ObjectViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.object_detail, parent, false));
    }

    @Override
    public void onBindViewHolder(ObjectViewHolder holder, int position) {
        holder.bindItem(objects.get(position));
    }

    @Override
    public int getItemCount() {
        return objects.size();
    }

    public void setObjects(ArrayList<LostObject> objects) {
        user = UsersPresenter.loadUser(context);
        this.objects = objects;
        notifyDataSetChanged();
    }

    public interface OnClickObjectListener {
        void onObjectClick(int index, String action);
    }

}
