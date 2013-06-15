package org.solovyev.android.calculator.view;

import android.content.SharedPreferences;
import android.os.Vibrator;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.solovyev.android.view.VibratorContainer;
import org.solovyev.android.view.drag.DragButton;
import org.solovyev.android.view.drag.OnDragListener;
import org.solovyev.android.view.drag.OnDragListenerWrapper;

/**
 * User: serso
 * Date: 4/20/12
 * Time: 3:27 PM
 */
public class OnDragListenerVibrator extends OnDragListenerWrapper {

	private static final float VIBRATION_TIME_SCALE = 0.5f;

	@Nonnull
	private final VibratorContainer vibrator;

	public OnDragListenerVibrator(@Nonnull OnDragListener onDragListener,
								  @Nullable Vibrator vibrator,
								  @Nonnull SharedPreferences preferences) {
		super(onDragListener);
		this.vibrator = new VibratorContainer(vibrator, preferences, VIBRATION_TIME_SCALE);
	}

	@Override
	public boolean onDrag(@Nonnull DragButton dragButton, @Nonnull org.solovyev.android.view.drag.DragEvent event) {
		boolean result = super.onDrag(dragButton, event);

		if (result) {
			vibrator.vibrate();
		}

		return result;
	}
}
