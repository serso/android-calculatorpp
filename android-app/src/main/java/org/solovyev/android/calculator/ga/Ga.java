package org.solovyev.android.calculator.ga;

import android.content.Context;
import android.util.Log;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import org.solovyev.android.calculator.R;
import org.solovyev.common.listeners.JEvent;
import org.solovyev.common.listeners.JEventListener;
import org.solovyev.common.listeners.JEventListeners;

import javax.annotation.Nonnull;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public final class Ga {

	@Nonnull
	private final GoogleAnalytics analytics;

	@Nonnull
	private final Tracker tracker;

	public Ga(@Nonnull Context context, @Nonnull JEventListeners<JEventListener<? extends JEvent>, JEvent> bus) {
		analytics = GoogleAnalytics.getInstance(context);
		tracker = analytics.newTracker(R.xml.ga);
	}

	@Nonnull
	private String getStackTrace(@Nonnull Exception e) {
		try {
			final ByteArrayOutputStream out = new ByteArrayOutputStream();
			e.printStackTrace(new PrintStream(out));
			return new String(out.toByteArray());
		} catch (Exception e1) {
			Log.e("Ga", e1.getMessage(), e1);
		}
		return "";
	}

	@Nonnull
	public GoogleAnalytics getAnalytics() {
		return analytics;
	}

	@Nonnull
	public Tracker getTracker() {
		return tracker;
	}
}
