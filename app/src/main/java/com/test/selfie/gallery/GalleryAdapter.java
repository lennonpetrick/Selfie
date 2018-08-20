package com.test.selfie.gallery;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.android.volley.toolbox.NetworkImageView;
import com.test.selfie.R;
import com.test.selfie.application.AppController;
import com.test.selfie.domain.model.Picture;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {

    private ItemListener<Picture> mItemListener;
    private List<Picture> mPictures;

    public GalleryAdapter(@NonNull List<Picture> pictures) {
        this.mPictures = pictures;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.layout_gallery_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Picture picture = mPictures.get(position);
        holder.loadImageUrl(picture.getThumbnail());
    }

    @Override
    public int getItemCount() {
        return mPictures.size();
    }

    public void setPictures(List<Picture> pictures) {
        mPictures = pictures;
        notifyDataSetChanged();
    }

    public void addPicture(Picture picture) {
        mPictures.add(picture);
        notifyItemInserted(mPictures.size() - 1);
    }

    public void removePicture(int position) {
        mPictures.remove(position);
        notifyItemRemoved(position);
    }

    public void setItemListener(ItemListener<Picture> itemListener) {
        this.mItemListener = itemListener;
    }

    public interface ItemListener<T> {
        void onDeleteClick(T object, int position);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.imgPicture_gallery) NetworkImageView mImgPicture;
        @BindView(R.id.btnDelete_gallery) ImageButton mBtnDelete;

        private ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mBtnDelete.setOnClickListener(v -> {
                if (mItemListener != null) {
                    final int position = getAdapterPosition();
                    mItemListener.onDeleteClick(mPictures.get(position), position);
                }
            });
        }

        private void loadImageUrl(String url) {
            mImgPicture.setImageUrl(url, AppController
                    .getInstance()
                    .getImageLoader());
            mImgPicture.setDefaultImageResId(android.R.drawable.ic_menu_camera);
            mImgPicture.setErrorImageResId(android.R.drawable.ic_menu_camera);
        }
    }
}
