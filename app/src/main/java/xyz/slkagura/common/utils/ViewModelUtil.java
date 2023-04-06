package xyz.slkagura.common.utils;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class ViewModelUtil {
    @Nullable
    public static <T extends ViewModel> T get(LifecycleOwner owner, Class<T> vmClazz) {
        ViewModelProvider provider;
        if (owner instanceof Fragment) {
            provider = new ViewModelProvider((Fragment) owner);
        } else if (owner instanceof FragmentActivity) {
            provider = new ViewModelProvider((FragmentActivity) owner);
        } else {
            return null;
        }
        return provider.get(vmClazz);
    }
}
