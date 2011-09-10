package org.solovyev.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StringUtils {

	public static boolean isEmpty ( @Nullable CharSequence s ){
		return s == null || s.length() == 0;
	}
	
	@NotNull
	public static String getNotEmpty ( @Nullable CharSequence s, @NotNull String defaultValue ){
		return isEmpty(s) ? defaultValue : s.toString();
	}
}

