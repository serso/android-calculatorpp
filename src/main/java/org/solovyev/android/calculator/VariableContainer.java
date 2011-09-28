/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.calculator;

import org.jetbrains.annotations.NotNull;
import org.solovyev.common.definitions.Identity;

/**
 * User: serso
 * Date: 9/28/11
 * Time: 11:22 PM
 */
public class VariableContainer extends Identity<String>{

	@NotNull
	private Double value;

	private boolean system;

	public VariableContainer(@NotNull String id, @NotNull Double value, boolean system) {
		super(id);
		this.value = value;
		this.system = system;
	}

	@NotNull
	public Double getValue() {
		return value;
	}

	public void setValue(@NotNull Double value) {
		this.value = value;
	}

	public boolean isSystem() {
		return system;
	}

	public void setSystem(boolean system) {
		this.system = system;
	}

	@Override
	public String toString() {
		return getId() + " = " + value;
	}
}
