package no.hvl.quizzoblig2.ui.gallery;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import no.hvl.quizzoblig2.R;
import no.hvl.quizzoblig2.data.db.GalleryItem;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.GalleryViewHolder> {
    private static final String TAG = "GalleryAdapter";
    private final Context context;
    private List<GalleryItem> galleryItems;
    private OnItemLongClickListener longClickListener;

    // Add this interface
    public interface OnItemLongClickListener {
        void onItemLongClick(GalleryItem item);
    }

    // Add setter for the listener
    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.longClickListener = listener;
    }

    public GalleryAdapter(Context context, List<GalleryItem> galleryItems) {
        this.context = context;
        this.galleryItems = galleryItems;
    }

    @NonNull
    @Override
    public GalleryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.gallery_item, parent, false);
        return new GalleryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GalleryViewHolder holder, int position) {
        GalleryItem item = galleryItems.get(position);
        holder.textViewName.setText(item.name);

        try {
            // Improved image loading with error handling
            RequestOptions options = new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .error(android.R.drawable.ic_dialog_alert);

            Glide.with(context)
                    .load(Uri.parse(item.imageUri))
                    .apply(options)
                    .into(holder.imageView);

            Log.d(TAG, "Loading image URI: " + item.imageUri);
        } catch (Exception e) {
            Log.e(TAG, "Error loading image: " + e.getMessage());
            holder.imageView.setImageResource(android.R.drawable.ic_dialog_alert);
        }

        // Add long click listener
        holder.itemView.setOnLongClickListener(v -> {
            if (longClickListener != null) {
                longClickListener.onItemLongClick(item);
                return true;
            }
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return galleryItems != null ? galleryItems.size() : 0;
    }

    public void updateGalleryItems(List<GalleryItem> newItems) {
        this.galleryItems = newItems;
        notifyDataSetChanged();
        Log.d(TAG, "Updated gallery items: " + (newItems != null ? newItems.size() : 0));
    }

    static class GalleryViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName;
        ImageView imageView;

        GalleryViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.itemName);
            imageView = itemView.findViewById(R.id.itemImage);
        }
    }
}



