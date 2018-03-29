package com.evaluate.adapter;

import android.support.annotation.IdRes;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public abstract class AbsViewBinder<T> extends RecyclerView.ViewHolder {
    private T data;

    private View mView;

    public AbsViewBinder(final View view) {
        super(view);
        mView = view;
        getViews();
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onViewClick(view, data);
            }
        });
    }

    protected abstract void getViews();

    public <V extends View> V getView(@IdRes int id) {
        return (V) mView.findViewById(id);
    }

    public abstract void bind(T t);

    protected void onViewClick(View view, T data) {
    }

    protected void setData(T data) {
        this.data = data;
    }

}
