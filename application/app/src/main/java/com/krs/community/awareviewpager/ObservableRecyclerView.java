package com.krs.community.awareviewpager;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by hunter on 2/14/15.
 */
public class ObservableRecyclerView extends RecyclerView {

    private RecyclerListener mListener;

    public ObservableRecyclerView(Context context) {
        super(context);
    }

    public ObservableRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    public ObservableRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setRecyclerListener(RecyclerListener listener) {
        mListener = listener;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (mListener != null) {
            mListener.onScrollChanged(l - oldl, t - oldt);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        if (mListener != null) {
            mListener.onInterceptTouch(e);
        }
        return super.onInterceptTouchEvent(e);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if (mListener != null) {
            mListener.onTouch(e);
        }
        return super.onTouchEvent(e);

    }

    public interface RecyclerListener {
        void onScrollChanged(int deltaX, int deltaY);

        void onInterceptTouch(MotionEvent ev);

        void onTouch(MotionEvent ev);
    }
}
