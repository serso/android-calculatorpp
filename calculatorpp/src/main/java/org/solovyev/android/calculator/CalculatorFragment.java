package org.solovyev.android.calculator;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.actionbarsherlock.app.SherlockFragment;
import org.jetbrains.annotations.NotNull;
import org.solovyev.android.calculator.about.CalculatorFragmentType;

/**
 * User: Solovyev_S
 * Date: 03.10.12
 * Time: 14:18
 */
public abstract class CalculatorFragment extends SherlockFragment {

    @NotNull
    private final CalculatorFragmentHelper fragmentHelper;

    protected CalculatorFragment(int layoutResId, int titleResId) {
        fragmentHelper = CalculatorApplication.getInstance().createFragmentHelper(layoutResId, titleResId);
    }

    protected CalculatorFragment(@NotNull CalculatorFragmentType fragmentType) {
        fragmentHelper = CalculatorApplication.getInstance().createFragmentHelper(fragmentType.getDefaultLayoutId(), fragmentType.getDefaultTitleResId());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fragmentHelper.onCreate(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return fragmentHelper.onCreateView(this, inflater, container);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fragmentHelper.onViewCreated(this, view);
    }

    @Override
    public void onDestroy() {
        fragmentHelper.onDestroy(this);
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();

        this.fragmentHelper.onResume(this);
    }

    @Override
    public void onPause() {
        this.fragmentHelper.onPause(this);

        super.onPause();
    }
}
