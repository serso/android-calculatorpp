package org.solovyev.android.calculator.view;

import android.content.SharedPreferences;
import android.os.Vibrator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
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

	@NotNull
	private final VibratorContainer vibrator;

	public OnDragListenerVibrator(@NotNull OnDragListener onDragListener,
								  @Nullable Vibrator vibrator,
								  @NotNull SharedPreferences preferences) {
		super(onDragListener);
		this.vibrator = new VibratorContainer(vibrator, preferences, VIBRATION_TIME_SCALE);
	}

	@Override
	public boolean onDrag(@NotNull DragButton dragButton, @NotNull org.solovyev.android.view.drag.DragEvent event) {
		boolean result = super.onDrag(dragButton, event);

		if (result) {
			vibrator.vibrate();
		}

		return result;
	}
}
