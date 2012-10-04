package org.solovyev.android.calculator;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    private int titleResId = -1;

    private boolean listenersOnCreate = true;

    public CalculatorFragmentHelperImpl(int layoutId) {
        this.layoutId = layoutId;
    }

    public CalculatorFragmentHelperImpl(int layoutId, int titleResId) {
        this.layoutId = layoutId;
        this.titleResId = titleResId;
    }

    public CalculatorFragmentHelperImpl(int layoutId, int titleResId, boolean listenersOnCreate) {
        this.layoutId = layoutId;
        this.titleResId = titleResId;
        this.listenersOnCreate = listenersOnCreate;
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

        if (listenersOnCreate) {
            if ( fragment instanceof CalculatorEventListener ) {
                CalculatorLocatorImpl.getInstance().getCalculator().addCalculatorEventListener((CalculatorEventListener) fragment);
            }
        }
    }

    @Override
    public void onResume(@NotNull Fragment fragment) {
        if (!listenersOnCreate) {
            if ( fragment instanceof CalculatorEventListener ) {
                CalculatorLocatorImpl.getInstance().getCalculator().addCalculatorEventListener((CalculatorEventListener) fragment);
            }
        }
    }

    @Override
    public void onPause(@NotNull Fragment fragment) {
        if (!listenersOnCreate) {
            if ( fragment instanceof CalculatorEventListener ) {
                CalculatorLocatorImpl.getInstance().getCalculator().removeCalculatorEventListener((CalculatorEventListener) fragment);
            }
        }
    }

    @Override
    public void onViewCreated(@NotNull Fragment fragment, @NotNull View root) {
        final ViewGroup adParentView = (ViewGroup) root.findViewById(R.id.ad_parent_view);
        final ViewGroup mainFragmentLayout = (ViewGroup) root.findViewById(R.id.main_fragment_layout);

        if (adParentView != null) {
            adView = AdsController.getInstance().inflateAd(fragment.getActivity(), adParentView, R.id.ad_parent_view);
        } else if ( mainFragmentLayout != null ) {
            adView = AdsController.getInstance().inflateAd(fragment.getActivity(), mainFragmentLayout, R.id.main_fragment_layout);
        }

        processButtons(fragment.getActivity(), root);

        if (titleResId >= 0) {
            this.setPaneTitle(fragment, titleResId);
        }
    }

    @Override
    public void onDestroy(@NotNull Fragment fragment) {
        super.onDestroy(fragment.getActivity());

        if (listenersOnCreate) {
            if ( fragment instanceof CalculatorEventListener ) {
                CalculatorLocatorImpl.getInstance().getCalculator().removeCalculatorEventListener((CalculatorEventListener) fragment);
            }
        }

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
