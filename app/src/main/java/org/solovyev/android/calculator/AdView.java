package org.solovyev.android.calculator;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class AdView extends FrameLayout {

    @Nullable
    private com.google.android.gms.ads.AdView admobView;
    @Nullable
    private AdView.AdViewListener admobListener;

    public AdView(Context context) {
        super(context);
        init();
    }

    public AdView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AdView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        setVisibility(GONE);
        setId(R.id.cpp_ad);
    }

    public void destroy() {
        destroyAdmobView();
    }

    private void destroyAdmobView() {
        if (admobView != null) {
            admobView.destroy();
            admobView.setAdListener(null);
            admobView = null;
        }
        if (admobListener != null) {
            admobListener.destroy();
            admobListener = null;
        }
    }

    public void pause() {
        if (admobView != null) {
            admobView.pause();
        }
    }

    public void resume() {
        if (admobView != null) {
            admobView.resume();
        }
    }

    public void show() {
        if (admobView != null) {
            return;
        }

        admobView = addAdmobView();
        admobListener = new AdView.AdViewListener(this);
        admobView.setAdListener(admobListener);

        final AdRequest.Builder b = new AdRequest.Builder();
        b.addTestDevice(AdRequest.DEVICE_ID_EMULATOR);
        if (BuildConfig.DEBUG) {
            // LG Nexus 5
            b.addTestDevice("B80E676D60CE6FDBE1B84A55464E3FE1");
        }
        admobView.loadAd(b.build());
    }

    @Nonnull
    private com.google.android.gms.ads.AdView addAdmobView() {
        final com.google.android.gms.ads.AdView v = new com.google.android.gms.ads.AdView(getContext());
        v.setVisibility(GONE);
        v.setAdSize(AdSize.BANNER);
        v.setAdUnitId(getResources().getString(R.string.admob));
        final LayoutParams lp = new LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        lp.gravity = Gravity.CENTER;
        addView(v, lp);
        return v;
    }

    public void hide() {
        if (admobView == null) {
            return;
        }

        setVisibility(GONE);

        admobView.setVisibility(View.GONE);
        admobView.pause();
        destroyAdmobView();
    }

    private static class AdViewListener extends AdListener {

        @Nullable
        private AdView adView;

        public AdViewListener(@Nonnull AdView adView) {
            this.adView = adView;
        }

        void destroy() {
            adView = null;
        }

        @Override
        public void onAdFailedToLoad(int errorCode) {
            if (adView != null) {
                adView.hide();
                adView = null;
            }
        }

        @Override
        public void onAdLoaded() {
            if (adView != null) {
                final com.google.android.gms.ads.AdView admobView = adView.admobView;
                if (admobView != null) {
                    admobView.setVisibility(View.VISIBLE);
                }
                adView.setVisibility(VISIBLE);
                adView = null;
            }
        }
    }
}
