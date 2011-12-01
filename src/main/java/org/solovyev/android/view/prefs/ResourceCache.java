/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.view.prefs;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.calculator.CalculatorActivity;
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

	// first map: key: language id, value: map of captions and translations
	// second mal: key: caption id, value: translation
	private final Map<String, Map<String, String>> captions = new HashMap<String, Map<String, String>>();

	private Class<?> resourceClass;

	private Context context;

	public List<Integer> getDragButtonIds() {
		return dragButtonIds;
	}

	public List<Integer> getButtonIds() {
		return buttonIds;
	}

	/**
	 * Method load captions for default locale using android R class
	 * @param context STATIC CONTEXT
	 * @param resourceClass class of captions in android (SUBCLASS of R class)
	 */
	public void initCaptions(@NotNull Context context, @NotNull Class<?> resourceClass) {
		initCaptions(context, resourceClass, Locale.getDefault());
	}

	/**
	 * Method load captions for specified locale using android R class
	 * @param context STATIC CONTEXT
	 * @param resourceClass class of captions in android (SUBCLASS of R class)
	 * @param locale language to be used for translation
	 */
	public void initCaptions(@NotNull Context context, @NotNull Class<?> resourceClass, @NotNull Locale locale) {
		assert this.resourceClass == null || this.resourceClass.equals(resourceClass);

		this.context = context;
		this.resourceClass = resourceClass;

		if (!initialized(locale)) {
			final Map<String, String> captionsByLanguage = new HashMap<String, String>();

			for (Field field : resourceClass.getDeclaredFields()) {
				int modifiers = field.getModifiers();
				if (Modifier.isFinal(modifiers) && Modifier.isStatic(modifiers)) {
					try {
						int captionId = field.getInt(resourceClass);
						captionsByLanguage.put(field.getName(), context.getString(captionId));
					} catch (IllegalAccessException e) {
						Log.e(ResourceCache.class.getName(), e.getMessage());
					}
				}
			}

			captions.put(locale.getLanguage(), captionsByLanguage);
		}
	}

	private boolean initialized(@NotNull Locale locale) {
		return captions.containsKey(locale.getLanguage());
	}

	/**
	 * @param captionId id of caption to be translated
	 * @return translation by caption id in default language, null if no translation in default language present
	 */
	@Nullable
	public String getCaption(@NotNull String captionId) {
		return getCaption(captionId, Locale.getDefault());
	}


	/**
	 * @param captionId  id of caption to be translated
	 * @param locale language to be used for translation
	 * @return translation by caption id in specified language, null if no translation in specified language present
	 */
	@Nullable
	public String getCaption(@NotNull String captionId, @NotNull final Locale locale) {
		Map<String, String> captionsByLanguage = captions.get(locale.getLanguage());
		if (captionsByLanguage != null) {
			return captionsByLanguage.get(captionId);
		} else {
			assert resourceClass != null && context != null;

			initCaptions(context, resourceClass, locale);

			captionsByLanguage = captions.get(locale.getLanguage());
			if (captionsByLanguage != null) {
				return captionsByLanguage.get(captionId);
			}
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
