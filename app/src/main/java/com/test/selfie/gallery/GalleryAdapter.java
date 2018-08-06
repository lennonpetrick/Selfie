package com.test.selfie.gallery;

import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.test.selfie.R;
import com.test.selfie.domain.model.Picture;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {

    private OnItemClickListener<Picture> mOnItemClickListener;
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

        if (picture.getBitmap() == null) {
            final byte[] data = picture.getData();
            picture.setBitmap(BitmapFactory
                    .decodeByteArray(data, 0, data.length));
        }

        holder.mImgPicture.setImageBitmap(picture.getBitmap());
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

    public void setOnItemClickListener(OnItemClickListener<Picture> onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener<T> {
        void onItemClick(T object, int position);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.imgPicture_gallery) ImageView mImgPicture;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(v -> {
                final int position = getAdapterPosition();
                mOnItemClickListener.onItemClick(mPictures.get(position), position);
            });
        }
    }
}
