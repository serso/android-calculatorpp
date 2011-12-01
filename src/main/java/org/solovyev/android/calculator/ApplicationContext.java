package org.solovyev.android.calculator;

import org.jetbrains.annotations.NotNull;

/**
 * User: serso
 * Date: 12/1/11
 * Time: 1:21 PM
 */
public class ApplicationContext extends android.app.Application {

	@NotNull
    private static ApplicationContext instance;

    public ApplicationContext() {
		instance = this;
    }

	@NotNull
	public static ApplicationContext getInstance() {
		return instance;
	}
}
