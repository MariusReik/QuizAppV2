package no.hvl.quizappv2.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.quizapp.entity.PhotoEntry;
import com.example.quizapp.R;

import java.util.List;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.PhotoEntryViewHolder> {
    private List<PhotoEntry> entries;
    private final LayoutInflater inflater;

    public GalleryAdapter(GalleryActivity context) {
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public PhotoEntryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.item_gallery, parent, false);
        return new PhotoEntryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoEntryViewHolder holder, int position) {
        if (entries != null) {
            PhotoEntry current = entries.get(position);
            holder.nameTextView.setText(current.name);
            Glide.with(holder.itemView.getContext())
                    .load(current.photoUri)
                    .into(holder.photoImageView);
        }
    }

    @Override
    public int getItemCount() {
        return entries != null ? entries.size() : 0;
    }

    public void setEntries(List<PhotoEntry> entries) {
        this.entries = entries;
        notifyDataSetChanged();
    }

    static class PhotoEntryViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameTextView;
        private final ImageView photoImageView;

        public PhotoEntryViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            photoImageView = itemView.findViewById(R.id.photoImageView);
        }
    }
}