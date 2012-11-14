/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.calculator.model;

import jscl.math.function.IConstant;
import jscl.math.function.IFunction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Transient;
import org.solovyev.android.calculator.MathPersistenceEntity;
import org.solovyev.common.math.MathEntity;

import java.util.Collections;
import java.util.List;

/**
 * User: serso
 * Date: 12/22/11
 * Time: 5:25 PM
 */

@Root
public class AFunction implements IFunction, MathPersistenceEntity {

	/*
	**********************************************************************
	*
	*                           FIELDS
	*
	**********************************************************************
	*/
	@Transient
	private Integer id;

	@Element
	@NotNull
	private String name;

	@Element
	@NotNull
	private String content;

	@Element(required = false)
	@NotNull
	private List<String> parameterNames = Collections.emptyList();

	@Element
	private boolean system;

	@Element(required = false)
	@Nullable
	private String description;

	/*
	**********************************************************************
	*
	*                           CONSTRUCTORS
	*
	**********************************************************************
	*/

	public AFunction() {
	}

	public AFunction(Integer id) {
		this.id = id;
	}

	/*
	**********************************************************************
	*
	*                           METHODS
	*
	**********************************************************************
	*/

	@Override
	public void copy(@NotNull MathEntity mathEntity) {
		if (mathEntity instanceof IFunction) {
			final IFunction that = ((IFunction) mathEntity);
			this.name = that.getName();
			this.content = that.getContent();
			this.description = this.getContent();
			this.system = that.isSystem();
			if (that.isIdDefined()) {
				this.id = that.getId();
			}
		} else {
			throw new IllegalArgumentException("Trying to make a copy of unsupported type: " + mathEntity.getClass());
		}
	}

	@Override
	public String toJava() {
		return String.valueOf(this.content);
	}

	/*
	**********************************************************************
	*
	*                           GETTERS/SEETTERS
	*
	**********************************************************************
	*/

	@NotNull
	public String getName() {
		return name;
	}

	@Override
	public boolean isSystem() {
		return system;
	}

	@NotNull
	@Override
	public Integer getId() {
		return this.id;
	}

	@Override
	public boolean isIdDefined() {
		return this.id != null;
	}

	@Override
	public void setId(@NotNull Integer id) {
		this.id = id;
	}

	public void setName(@NotNull String name) {
		this.name = name;
	}

	@NotNull
	public String getContent() {
		return content;
	}

	@Nullable
	@Override
	public String getDescription() {
		return this.description;
	}

	public void setContent(@NotNull String content) {
		this.content = content;
	}

	@NotNull
	public List<String> getParameterNames() {
		return parameterNames;
	}

	public void setParameterNames(@NotNull List<String> parameterNames) {
		this.parameterNames = parameterNames;
	}

	/*
	**********************************************************************
	*
	*                           STATIC
	*
	**********************************************************************
	*/

	public static class Builder implements MathEntityBuilder<AFunction> {

		@NotNull
		private String name;

		@Nullable
		private String value;

		private boolean system = false;

		@Nullable
		private String description;

		@Nullable
		private Integer id;

		@NotNull
		private List<String> parameterNames = Collections.emptyList();

		public Builder() {
		}

		public Builder(@NotNull IFunction function) {
			this.name = function.getName();
			this.value = function.getContent();
			this.system = function.isSystem();
			this.description = function.getDescription();
			this.id = function.getId();
		}

		public Builder(@NotNull IConstant function) {
			this.name = function.getName();

			this.value = function.getValue();

			this.system = function.isSystem();
			this.description = function.getDescription();
			if (function.isIdDefined()) {
				this.id = function.getId();
			}
		}

		public Builder(@NotNull String name, @NotNull Double value) {
			this(name, String.valueOf(value));
		}

		public Builder(@NotNull String name, @Nullable String value) {
			this.name = name;
			this.value = value;
		}


		@NotNull
		public Builder setName(@NotNull String name) {
			this.name = name;
			return this;
		}

		@NotNull
		public Builder setValue(@Nullable String value) {
			this.value = value;
			return this;
		}

		protected Builder setSystem(boolean system) {
			this.system = system;
			return this;
		}

		public void setParameterNames(@NotNull List<String> parameterNames) {
			this.parameterNames = parameterNames;
		}

		@NotNull
		public Builder setDescription(@Nullable String description) {
			this.description = description;
			return this;
		}

		@NotNull
		public AFunction create() {
			final AFunction result;
			if (id != null) {
				result = new AFunction(id);
			} else {
				result = new AFunction();
			}

			result.name = name;
			result.content = value;
			result.system = system;
			result.description = description;
			result.parameterNames = parameterNames;

			return result;
		}
	}
}
