package com.bresan.learning.filemanager.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bresan.learning.filemanager.model.Item;
import com.bresan.learning.filemanager.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rodrigobresan on 4/26/16.
 */
public class ItemAdapter extends SelectableAdapter<ItemAdapter.ItemViewHolder>{

    private Context mContext;
    private List<Item> mItemList;

    private ClickListener clickListener;
    private boolean isActionModeEnabled;

    public ItemAdapter(List<Item> itemList, ClickListener clickListener) {
        this.mItemList = itemList;
        this.clickListener = clickListener;
        this.isActionModeEnabled = false;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_file, parent, false);

        return new ItemViewHolder(view, clickListener);
    }

    @Override
    public void onBindViewHolder(final ItemViewHolder holder, final int position) {
        final Item currentItem = mItemList.get(position);

        if (currentItem.isDirectory()) {
            holder.imgThumbnail.setImageResource(R.drawable.folder);
        } else {

            Glide.with(mContext).load(currentItem.getPath()).listener(new RequestListener<String, GlideDrawable>() {
                @Override
                public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                    holder.imgThumbnail.setImageResource(R.drawable.unknown);
                    return true;
                }

                @Override
                public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                    return false;
                }
            }).into(holder.imgThumbnail);
        }

        holder.txtTitle.setText(currentItem.getName());
        holder.txtItems.setText(currentItem.getData());
        holder.background.setSelected(isSelected(position));
    }

    @Override
    public int getItemCount() {
        return mItemList.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        public TextView txtTitle, txtItems;
        public ImageView imgThumbnail;
        private ClickListener listener;
        public RelativeLayout background;

        public ItemViewHolder(View itemView, ClickListener clickListener) {
            super(itemView);

            this.listener = clickListener;

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

            background = (RelativeLayout) itemView.findViewById(R.id.item_background);
            imgThumbnail = (ImageView) itemView.findViewById(R.id.img_item_thumbnail);
            txtTitle = (TextView) itemView.findViewById(R.id.txt_item_name);
            txtItems = (TextView) itemView.findViewById(R.id.txt_item_info);
        }

        @Override
        public void onClick(View v) {
            if (listener != null) {
                listener.onItemClicked(getAdapterPosition());

                if (isActionModeEnabled) {

                    if (isSelected(getAdapterPosition())) {
                        background.setSelected(false);
                    } else {
                        background.setSelected(true);
                    }

                }
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if (listener != null) {
                return listener.onItemLongClicked(getAdapterPosition());
            }
            return false;
        }
    }

    public void setActionModeEnabled(boolean isEnabled) {
        this.isActionModeEnabled = isEnabled;
    }

    public interface ClickListener {
        void onItemClicked(int position);
        boolean onItemLongClicked(int position);
    }
}
