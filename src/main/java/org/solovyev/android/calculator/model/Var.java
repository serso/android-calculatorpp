/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.calculator.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;
import org.solovyev.common.utils.StringUtils;

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

	@Element(required = false)
	@Nullable
	private String value;

	@Element
	private boolean system;

	@Element(required = false)
	@Nullable
	private String description;

	public static class Builder {

		@NotNull
		private String name;

		@Nullable
		private String value;

		private boolean system = false;

		@Nullable
		private String description;

		public Builder() {
		}

		public Builder(@NotNull Var var) {
			this.name = var.name;
			this.value = var.value;
			this.system = var.system;
			this.description = var.description;
		}

		public Builder(@NotNull String name, @NotNull Double value) {
			this(name, String.valueOf(value));
		}

		public Builder(@NotNull String name, @Nullable String value) {
			this.name = name;
			this.value = value;
		}

		public void setName(@NotNull String name) {
			this.name = name;
		}

		public void setValue(@Nullable String value) {
			this.value = value;
		}

		protected Builder setSystem(boolean system) {
			this.system = system;
			return this;
		}

		public Builder setDescription(@Nullable String description) {
			this.description = description;
			return this;
		}

		protected Var create () {
			final Var var = new Var();

			var.name = name;
			var.value = value;
			var.system = system;
			var.description = description;

			return var;
		}
	}

	private Var() {
	}

	public void copy(@NotNull Var var) {
		this.name = var.name;
		this.value = var.value;
		this.description = var.description;
		this.system = var.system;
	}

	@Nullable
	public String getValue() {
		return value;
	}

	public boolean isSystem() {
		return system;
	}

	@NotNull
	public String getName() {
		return name;
	}

	public boolean isUndefined() {
		return StringUtils.isEmpty(this.value);
	}

	@Nullable
	public String getDescription() {
		return description;
	}

	@Override
	public String toString() {
		if (value != null) {
			return getName() + " = " + value;
		} else {
			return getName();
		}
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
