package com.test.selfie.gallery;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class CustomItemDecorator extends RecyclerView.ItemDecoration {

    private int mPadding;

    public CustomItemDecorator(int padding) {
        mPadding = padding;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view,
                               RecyclerView parent,
                               RecyclerView.State state) {
        if(parent.getPaddingLeft()!= mPadding) {
            parent.setPadding(mPadding, mPadding, mPadding, mPadding);
            parent.setClipToPadding(false);
        }

        outRect.top = mPadding;
        outRect.right = mPadding;
        outRect.left = mPadding;
        outRect.bottom = mPadding;
    }
}