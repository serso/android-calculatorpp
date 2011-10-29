/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.calculator;

import android.util.Log;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

/**
 * User: serso
 * Date: 10/30/11
 * Time: 1:11 AM
 */
public class RClassUtils {

	@NotNull
	private final static Map<Class<?>, Map<String, Integer>> caches = new HashMap<Class<?>, Map<String, Integer>>(3);

	// not intended for instantiation
	private RClassUtils() {
		throw new AssertionError();
	}

	@NotNull
	public static Map<String, Integer> getCache(@NotNull Class<?> clazz) {
		Map<String, Integer> result = caches.get(clazz);

		if (result == null) {
			result = new HashMap<String, Integer>();

			for (Field field : clazz.getDeclaredFields()) {
				int modifiers = field.getModifiers();
				if (Modifier.isFinal(modifiers) && Modifier.isStatic(modifiers)) {
					try {
						result.put(field.getName(), field.getInt(R.style.class));
					} catch (IllegalAccessException e) {
						Log.e(CalculatorActivity.class.getName(), e.getMessage());
					}
				}
			}

			caches.put(clazz, result);
		}

		return result;
	}
}
