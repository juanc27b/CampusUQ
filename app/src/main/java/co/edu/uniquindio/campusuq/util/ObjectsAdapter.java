package co.edu.uniquindio.campusuq.util;

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
import co.edu.uniquindio.campusuq.activity.ObjectsActivity;
import co.edu.uniquindio.campusuq.vo.LostObject;

public class ObjectsAdapter extends RecyclerView.Adapter<ObjectsAdapter.ObjectViewHolder> {
    public static final String DIALOG = "dialog", READED = "readed", FOUND = "found";

    private ArrayList<LostObject> objects;
    private OnClickObjectListener listener;

    public ObjectsAdapter(ArrayList<LostObject> objects, ObjectsActivity objectsActivity) {
        this.objects = objects;
        listener = objectsActivity;
    }

    public class ObjectViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView name, place, date, description;
        private ImageView image;

        ObjectViewHolder(View view) {
            super(view);
            view.findViewById(R.id.object_layout).setOnClickListener(this);
            name = view.findViewById(R.id.object_name);
            place = view.findViewById(R.id.object_place);
            date = view.findViewById(R.id.object_date);
            image = view.findViewById(R.id.object_image);
            description = view.findViewById(R.id.object_description);
            view.findViewById(R.id.object_readed).setOnClickListener(this);
            view.findViewById(R.id.object_found).setOnClickListener(this);
        }

        void bindItem(LostObject lostObject) {
            name.setText(lostObject.getName());
            place.setText(lostObject.getPlace());
            date.setText(lostObject.getDate());
            File imgFile = new  File(lostObject.getImage());
            if(imgFile.exists()) image.setImageBitmap(BitmapFactory.decodeFile(imgFile.getAbsolutePath()));
            else image.setImageResource(R.drawable.rectangle_gray);
            description.setText(lostObject.getDescription());
        }

        @Override
        public void onClick(View view) {
            String action;
            switch(view.getId()) {
            case R.id.object_readed:
                action = READED;
                break;
            case R.id.object_found:
                action = FOUND;
                break;
            default:
                action = DIALOG;
            }
            listener.onObjectClick(getAdapterPosition(), action);
        }
    }

    @Override
    public ObjectViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ObjectViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.object_detail, parent, false));
    }

    @Override
    public void onBindViewHolder(ObjectViewHolder holder, int position) {
        holder.bindItem(objects.get(position));
    }

    @Override
    public int getItemCount() {
        return objects.size();
    }

    public interface OnClickObjectListener {
        void onObjectClick(int index, String action);
    }

    public void setObjects(ArrayList<LostObject> objects) {
        this.objects = objects;
        notifyDataSetChanged();
    }
}
