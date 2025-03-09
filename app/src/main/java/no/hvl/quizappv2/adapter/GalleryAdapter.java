package no.hvl.quizappv2.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import androidx.lifecycle.ViewModelProvider;
import com.bumptech.glide.Glide;
import java.util.List;
import no.hvl.quizappv2.R;
import no.hvl.quizappv2.entity.PhotoEntry;
import no.hvl.quizappv2.view.GalleryActivity;
import no.hvl.quizappv2.viewmodel.GalleryViewModel;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.PhotoEntryViewHolder> {

    private List<PhotoEntry> entries;
    private final LayoutInflater inflater;
    private final GalleryViewModel viewModel;

    public GalleryAdapter(GalleryActivity context) {
        inflater = LayoutInflater.from(context);
        viewModel = new ViewModelProvider(context).get(GalleryViewModel.class);
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
            holder.nameTextView.setText(current.getName());

            Glide.with(holder.itemView.getContext())
                    .load(current.getPhotoUri())
                    .error(R.drawable.cute_fox) // Bruk cute_fox.jpg som placeholder
                    .into(holder.photoImageView);

            holder.itemView.setOnLongClickListener(v -> {
                viewModel.deleteById(current.getId());
                return true;
            });
        }
    }

    @Override
    public int getItemCount() {
        return entries != null ? entries.size() : 0;
    }

    public void setEntries(List<PhotoEntry> newEntries) {
        if (entries == null) {
            entries = newEntries;
            notifyDataSetChanged();
        } else {
            DiffUtil.DiffResult result = DiffUtil.calculateDiff(new PhotoEntryDiffCallback(entries, newEntries));
            entries.clear();
            entries.addAll(newEntries);
            result.dispatchUpdatesTo(this);
        }
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

    static class PhotoEntryDiffCallback extends DiffUtil.Callback {
        private final List<PhotoEntry> oldEntries;
        private final List<PhotoEntry> newEntries;

        public PhotoEntryDiffCallback(List<PhotoEntry> oldEntries, List<PhotoEntry> newEntries) {
            this.oldEntries = oldEntries;
            this.newEntries = newEntries;
        }

        @Override
        public int getOldListSize() {
            return oldEntries.size();
        }

        @Override
        public int getNewListSize() {
            return newEntries.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return oldEntries.get(oldItemPosition).getId() == newEntries.get(newItemPosition).getId();
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return oldEntries.get(oldItemPosition).equals(newEntries.get(newItemPosition));
        }
    }
}