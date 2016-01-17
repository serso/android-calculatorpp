package org.solovyev.android.calculator;

import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.view.*;

import javax.annotation.Nonnull;

import static android.view.Menu.NONE;

public abstract class BaseFragment extends Fragment {

    @Nonnull
    protected final FragmentUi ui;

    public BaseFragment(@Nonnull CalculatorFragmentType type) {
        ui = new FragmentUi(type.getDefaultLayoutId(), type.getDefaultTitleResId(), false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inject(((CalculatorApplication) getActivity().getApplication()).getComponent());
        ui.onCreate(this);
    }

    protected void inject(@Nonnull AppComponent component) {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return ui.onCreateView(this, inflater, container);
    }

    @Override
    public void onViewCreated(View root, Bundle savedInstanceState) {
        super.onViewCreated(root, savedInstanceState);
        ui.onViewCreated(this, root);
    }

    @Override
    public void onResume() {
        super.onResume();
        ui.onResume(this);
    }

    @Override
    public void onPause() {
        ui.onPause(this);
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        ui.onDestroyView(this);
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        ui.onDestroy(this);
        super.onDestroy();
    }

    @Nonnull
    protected static MenuItem addMenu(@Nonnull ContextMenu menu, @StringRes int label, @Nonnull MenuItem.OnMenuItemClickListener listener) {
        return menu.add(NONE, label, NONE, label).setOnMenuItemClickListener(listener);
    }
}
