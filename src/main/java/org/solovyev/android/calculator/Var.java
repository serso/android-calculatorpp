/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.calculator;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * User: serso
 * Date: 9/28/11
 * Time: 11:22 PM
 */

@Root
public class Var {

	@Element
	@NotNull
	private String name;

	@Element
	@NotNull
	private String value;

	private boolean system;

	@Element(required = false)
	@Nullable
	private String description;

	public Var() {
	}

	public Var(@NotNull String name, @NotNull Double value, boolean system) {
		this(name, String.valueOf(value), system);
	}

	public Var(@NotNull String name, @NotNull String value, boolean system) {
		this.name = name;
		this.value = value;
		this.system = system;
	}

	@NotNull
	public String getValue() {
		return value;
	}

	public void setValue(@NotNull String value) {
		this.value = value;
	}

	public boolean isSystem() {
		return system;
	}

	public void setSystem(boolean system) {
		this.system = system;
	}

	@NotNull
	public String getName() {
		return name;
	}

	public void setName(@NotNull String name) {
		this.name = name;
	}

	@Nullable
	public String getDescription() {
		return description;
	}

	public void setDescription(@Nullable String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return getName() + " = " + value;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Var var = (Var) o;

		if (!name.equals(var.name)) return false;

		return true;
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}
}
