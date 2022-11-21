package xyz.slkagura.common.lifecycle;

import android.view.View;

import androidx.databinding.BindingAdapter;

public class BindAdapter {
    @BindingAdapter("setGone")
    public static void setGone(View view, boolean visible) {
        view.setVisibility(visible ? View.VISIBLE : View.GONE);
    }
}
