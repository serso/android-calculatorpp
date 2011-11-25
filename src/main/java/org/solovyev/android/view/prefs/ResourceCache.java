package org.solovyev.android.view.prefs;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.calculator.R;
import org.solovyev.android.view.widgets.DragButton;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * User: serso
 * Date: 11/25/11
 * Time: 1:52 PM
 */
public enum ResourceCache {

	instance;

	// ids of drag buttons in R.class
	private List<Integer> dragButtonIds = null;

	// ids of buttons in R.class
	private List<Integer> buttonIds = null;

	private static final Map<String, Map<String, String>> captions = new HashMap<String, Map<String, String>>();

	public List<Integer> getDragButtonIds() {
		return dragButtonIds;
	}

	public List<Integer> getButtonIds() {
		return buttonIds;
	}

	public void initCaptions(@NotNull Class<?> resourceClass, @NotNull Activity activity) {
		final Locale locale = Locale.getDefault();

		if (!captions.containsKey(locale.getLanguage())) {

			final Map<String, String> captionsByLanguage = new HashMap<String, String>();

			for (Field field : resourceClass.getDeclaredFields()) {
				int modifiers = field.getModifiers();
				if (Modifier.isFinal(modifiers) && Modifier.isStatic(modifiers)) {
					try {
						int captionId = field.getInt(resourceClass);
						captionsByLanguage.put(field.getName(), activity.getString(captionId));
					} catch (IllegalAccessException e) {
						Log.e(ResourceCache.class.getName(), e.getMessage());
					}
				}
			}

			captions.put(locale.getLanguage(), captionsByLanguage);
		}
	}

	@Nullable
	public String getCaption(@NotNull String captionId) {
		final Locale locale = Locale.getDefault();

		final Map<String, String> captionsByLanguage = captions.get(locale.getLanguage());
		if (captionsByLanguage != null) {
			return captionsByLanguage.get(captionId);
		}

		return null;
	}

	public void init(@NotNull Class<?> resourceClass, @NotNull Activity activity) {
		dragButtonIds = new ArrayList<Integer>();
		buttonIds = new ArrayList<Integer>();

		for (Field field : resourceClass.getDeclaredFields()) {
			int modifiers = field.getModifiers();
			if (Modifier.isFinal(modifiers) && Modifier.isStatic(modifiers)) {
				try {
					int viewId = field.getInt(resourceClass);
					final View view = activity.findViewById(viewId);
					if (view instanceof DragButton) {
						dragButtonIds.add(viewId);
					}
					if (view instanceof Button) {
						buttonIds.add(viewId);
					}
				} catch (IllegalAccessException e) {
					Log.e(ResourceCache.class.getName(), e.getMessage());
				}
			}
		}
	}
}
