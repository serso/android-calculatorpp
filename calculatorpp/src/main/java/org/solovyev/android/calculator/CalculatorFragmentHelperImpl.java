package org.solovyev.android.calculator;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.ads.AdView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.ads.AdsController;

/**
 * User: serso
 * Date: 9/26/12
 * Time: 10:14 PM
 */
public class CalculatorFragmentHelperImpl extends AbstractCalculatorHelper implements CalculatorFragmentHelper {

    @Nullable
    private AdView adView;

    private int layoutId;

    public CalculatorFragmentHelperImpl(int layoutId) {
        this.layoutId = layoutId;
    }

    @Override
    public boolean isPane(@NotNull Fragment fragment) {
        return fragment.getActivity() instanceof CalculatorActivity;
    }

    public void setPaneTitle(@NotNull Fragment fragment, int titleResId) {
        final TextView fragmentTitle = (TextView) fragment.getView().findViewById(R.id.fragmentTitle);
        if (fragmentTitle != null) {
            if (!isPane(fragment)) {
                fragmentTitle.setVisibility(View.GONE);
            } else {
                fragmentTitle.setText(fragment.getString(titleResId).toUpperCase());
            }
        }
    }

    @Override
    public void onCreate(@NotNull Fragment fragment) {
        super.onCreate(fragment.getActivity());
    }

    @Override
    public void onViewCreated(@NotNull Fragment fragment, @NotNull View root) {
        final ViewGroup mainFragmentLayout = (ViewGroup) root.findViewById(R.id.main_fragment_layout);
        if (mainFragmentLayout != null) {
            adView = AdsController.getInstance().inflateAd(fragment.getActivity(), mainFragmentLayout, R.id.main_fragment_layout);
        }

        processButtons(fragment.getActivity(), root);
    }

    @Override
    public void onDestroy(@NotNull Fragment fragment) {
        super.onDestroy(fragment.getActivity());

        if (this.adView != null) {
            this.adView.destroy();
        }
    }

    @NotNull
    @Override
    public View onCreateView(@NotNull Fragment fragment, @NotNull LayoutInflater inflater, @Nullable ViewGroup container) {
        return inflater.inflate(layoutId, container, false);
    }
}
