/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.calculator.model;

import jscl.math.function.Constant;
import jscl.math.function.ExtendedConstant;
import jscl.math.function.IConstant;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Transient;
import org.solovyev.android.calculator.MathPersistenceEntity;
import org.solovyev.common.JBuilder;
import org.solovyev.common.math.MathEntity;
import org.solovyev.common.text.Strings;

/**
 * User: serso
 * Date: 9/28/11
 * Time: 11:22 PM
 */

@Root
public class Var implements IConstant, MathPersistenceEntity {

	@Transient
	private Integer id;

	@Element
	@Nonnull
	private String name;

	@Element(required = false)
	@Nullable
	private String value;

	@Element
	private boolean system;

	@Element(required = false)
	@Nullable
	private String description;

	@Transient
	private Constant constant;

	public static class Builder implements JBuilder<Var>, MathEntityBuilder<Var> {

		@Nonnull
		private String name;

		@Nullable
		private String value;

		private boolean system = false;

		@Nullable
		private String description;

		@Nullable
		private Integer id;

		public Builder() {
		}

		public Builder(@Nonnull Var var) {
			this.name = var.name;
			this.value = var.value;
			this.system = var.system;
			this.description = var.description;
			this.id = var.id;
		}

		public Builder(@Nonnull IConstant iConstant) {
			this.name = iConstant.getName();

			this.value = iConstant.getValue();

			this.system = iConstant.isSystem();
			this.description = iConstant.getDescription();
			if (iConstant.isIdDefined()) {
				this.id = iConstant.getId();
			}
		}

		public Builder(@Nonnull String name, @Nonnull Double value) {
			this(name, String.valueOf(value));
		}

		public Builder(@Nonnull String name, @Nullable String value) {
			this.name = name;
			this.value = value;
		}


		@Nonnull
		public Builder setName(@Nonnull String name) {
			this.name = name;
			return this;
		}

		@Nonnull
		public Builder setValue(@Nullable String value) {
			this.value = value;
			return this;
		}

		protected Builder setSystem(boolean system) {
			this.system = system;
			return this;
		}

		@Nonnull
		public Builder setDescription(@Nullable String description) {
			this.description = description;
			return this;
		}

		@Nonnull
		public Var create() {
			final Var result;
			if (id != null) {
				result = new Var(id);
			} else {
				result = new Var();
			}

			result.name = name;
			result.value = value;
			result.system = system;
			result.description = description;

			return result;
		}
	}

	private Var() {
	}

	private Var(@Nonnull Integer id) {
		this.id = id;
	}

	public void copy(@Nonnull MathEntity o) {
		if (o instanceof IConstant) {
			final IConstant that = ((IConstant) o);
			this.name = that.getName();
			this.value = that.getValue();
			this.description = that.getDescription();
			this.system = that.isSystem();
			if (that.isIdDefined()) {
				this.id = that.getId();
			}
		} else {
			throw new IllegalArgumentException("Trying to make a copy of unsupported type: " + o.getClass());
		}
	}

	@Nullable
	public Double getDoubleValue() {
		Double result = null;
		if (value != null) {
			try {
				result = Double.valueOf(value);
			} catch (NumberFormatException e) {
				// do nothing - string is not a double
			}
		}
		return result;
	}

	@Nullable
	public String getValue() {
		return value;
	}

	@Nonnull
	@Override
	public String toJava() {
		return String.valueOf(value);
	}

	public boolean isSystem() {
		return system;
	}

	@Nonnull
	@Override
	public Integer getId() {
		return this.id;
	}

	@Override
	public boolean isIdDefined() {
		return this.id != null;
	}

	@Override
	public void setId(@Nonnull Integer id) {
		this.id = id;
	}

	@Nonnull
	public String getName() {
		return name;
	}

	@Nonnull
	@Override
	public Constant getConstant() {
		if (constant == null) {
			constant = new Constant(this.name);
		}
		return constant;
	}

	@Nullable
	public String getDescription() {
		return description;
	}

	@Override
	public boolean isDefined() {
		return !Strings.isEmpty(value);
	}

	@Override
	public String toString() {
		return ExtendedConstant.toString(this);
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
