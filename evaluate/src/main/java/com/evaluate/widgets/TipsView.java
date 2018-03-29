package com.evaluate.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.evaluate.R;

public class TipsView extends RelativeLayout {
    private TextView mTips;
    private OnDismissListener mListener;

    public TipsView(Context context) {
        super(context);
        init();
    }

    public TipsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TipsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.oc_evaluate_tips, this);
        mTips = (TextView) findViewById(R.id.tips_content);

        findViewById(R.id.tips_close).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (mListener != null) {
                    mListener.onDismiss();
                }
            }
        });
    }

    public void setTips(CharSequence tips) {
        mTips.setText(tips);
    }

    public void show() {
        setVisibility(View.VISIBLE);
    }

    public void dismiss() {
        setVisibility(View.GONE);
    }

    public void setOnDismissListener(OnDismissListener listener) {
        mListener = listener;
    }


    public interface OnDismissListener {
        void onDismiss();
    }
}
