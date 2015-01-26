package org.solovyev.android.calculator;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;

import javax.annotation.Nullable;

public class AdView extends FrameLayout {

	@Nullable
	private com.google.android.gms.ads.AdView admobView;

	public AdView(Context context) {
		super(context);
	}

	public AdView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public AdView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public void destroy() {
		if (admobView != null) {
			admobView.destroy();
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

		LayoutInflater.from(getContext()).inflate(R.layout.admob, this);
		admobView = (com.google.android.gms.ads.AdView) findViewById(R.id.admob);
		if (admobView == null) throw new AssertionError();

		admobView.setAdListener(new AdListener() {
			@Override
			public void onAdFailedToLoad(int errorCode) {
				hide();
			}

			@Override
			public void onAdLoaded() {
				if (admobView != null) {
					admobView.setVisibility(View.VISIBLE);
				}
				setVisibility(VISIBLE);
			}
		});

		final AdRequest.Builder b = new AdRequest.Builder();
		b.addTestDevice(AdRequest.DEVICE_ID_EMULATOR);
		if (BuildConfig.DEBUG) {
			// LG Nexus 5
			b.addTestDevice("B80E676D60CE6FDBE1B84A55464E3FE1");
		}
		admobView.loadAd(b.build());
	}

	public void hide() {
		if(admobView == null) {
			return;
		}

		setVisibility(GONE);

		admobView.setVisibility(View.GONE);
		admobView.pause();
		admobView.destroy();
		admobView = null;
	}
}
