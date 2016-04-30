package org.solovyev.android.calculator;

import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.view.*;
import org.solovyev.android.calculator.ads.AdUi;
import org.solovyev.android.plotter.Check;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static android.view.Menu.NONE;
import static org.solovyev.android.calculator.App.cast;

public abstract class BaseFragment extends Fragment {

    private final int layout;
    @Inject
    AdUi adUi;
    @Inject
    public Typeface typeface;

    protected BaseFragment(@LayoutRes int layout) {
        this.layout = layout;
    }

    @Nonnull
    public static MenuItem addMenu(@Nonnull ContextMenu menu, @StringRes int label,
        @Nonnull MenuItem.OnMenuItemClickListener listener) {
        return menu.add(NONE, label, NONE, label).setOnMenuItemClickListener(listener);
    }

    @NonNull
    public static <P extends Parcelable> P getParcelable(@NonNull Bundle bundle,
        @NonNull String key) {
        final P parcelable = bundle.getParcelable(key);
        Check.isNotNull(parcelable);
        return parcelable;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inject(cast(getActivity().getApplication()).getComponent());
        adUi.onCreate();
    }

    protected void inject(@Nonnull AppComponent component) {
        component.inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        final View view = inflater.inflate(layout, container, false);
        adUi.onCreateView(view);
        BaseActivity.fixFonts(view, typeface);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        adUi.onResume();
    }

    @Override
    public void onPause() {
        adUi.onPause();
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        adUi.onDestroyView();
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        adUi.onDestroy();
        super.onDestroy();
    }
}
