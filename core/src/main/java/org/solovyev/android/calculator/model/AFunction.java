/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.calculator.model;

import jscl.math.function.IFunction;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Transient;
import org.solovyev.android.calculator.CalculatorParseException;
import org.solovyev.android.calculator.Locator;
import org.solovyev.android.calculator.MathPersistenceEntity;
import org.solovyev.common.math.MathEntity;
import org.solovyev.common.msg.Message;
import org.solovyev.common.msg.MessageLevel;
import org.solovyev.common.text.Strings;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * User: serso
 * Date: 12/22/11
 * Time: 5:25 PM
 */

@Root(name = "function")
public class AFunction implements IFunction, MathPersistenceEntity, Serializable {

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
	@Nonnull
	private String name;

	@Element(name = "body")
	@Nonnull
	private String content;

	@ElementList(type = String.class)
	@Nonnull
	private List<String> parameterNames = new ArrayList<String>();

	@Element
	private boolean system;

	@Element(required = false)
	@Nonnull
	private String description = "";

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

	public static AFunction fromIFunction(@Nonnull IFunction function) {
		final AFunction result = new AFunction();

		copy(result, function);

		return result;
	}

	/*
	**********************************************************************
	*
	*                           METHODS
	*
	**********************************************************************
	*/

	@Override
	public void copy(@Nonnull MathEntity mathEntity) {
		if (mathEntity instanceof IFunction) {
			copy(this, (IFunction) mathEntity);
		} else {
			throw new IllegalArgumentException("Trying to make a copy of unsupported type: " + mathEntity.getClass());
		}
	}

	private static void copy(@Nonnull AFunction target,
							 @Nonnull IFunction source) {
		target.name = source.getName();
		target.content = source.getContent();
		target.description = Strings.getNotEmpty(source.getDescription(), "");
		target.system = source.isSystem();
		if (source.isIdDefined()) {
			target.id = source.getId();
		}
		target.parameterNames = new ArrayList<String>(source.getParameterNames());
	}

	@Override
	public String toJava() {
		return String.valueOf(this.content);
	}

	@Override
	public String toString() {
		return "AFunction{" +
				"name='" + name + '\'' +
				", parameterNames=" + parameterNames +
				", content='" + content + '\'' +
				'}';
	}

	/*
	**********************************************************************
	*
	*                           GETTERS/SETTERS
	*
	**********************************************************************
	*/

	@Nonnull
	public String getName() {
		return name;
	}

	@Override
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

	public void setName(@Nonnull String name) {
		this.name = name;
	}

	@Nonnull
	public String getContent() {
		return content;
	}

	@Nonnull
	@Override
	public String getDescription() {
		return this.description;
	}

	public void setContent(@Nonnull String content) {
		this.content = content;
	}

	@Nonnull
	public List<String> getParameterNames() {
		return parameterNames;
	}

	public void setParameterNames(@Nonnull List<String> parameterNames) {
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

		@Nonnull
		private String name;

		@Nullable
		private String value;

		private boolean system = false;

		@Nullable
		private String description;

		@Nullable
		private Integer id;

		@Nonnull
		private List<String> parameterNames = Collections.emptyList();

		public Builder() {
		}

		public Builder(@Nonnull IFunction function) {
			this.name = function.getName();
			this.value = function.getContent();
			this.system = function.isSystem();
			this.description = function.getDescription();
			if (function.isIdDefined()) {
				this.id = function.getId();
			}
			this.parameterNames = new ArrayList<String>(function.getParameterNames());
		}

		public Builder(@Nonnull String name,
					   @Nonnull String value,
					   @Nonnull List<String> parameterNames) {
			this.name = name;
			this.value = value;
			this.parameterNames = parameterNames;
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

		public void setParameterNames(@Nonnull List<String> parameterNames) {
			this.parameterNames = parameterNames;
		}

		@Nonnull
		public Builder setDescription(@Nullable String description) {
			this.description = description;
			return this;
		}

		@Nonnull
		public AFunction create() throws AFunction.Builder.CreationException {
			final AFunction result;
			if (id != null) {
				result = new AFunction(id);
			} else {
				result = new AFunction();
			}

			result.name = name;
			try {
				result.content = Locator.getInstance().getCalculator().prepareExpression(value).toString();
			} catch (CalculatorParseException e) {
				throw new CreationException(e);
			}
			result.system = system;
			result.description = Strings.getNotEmpty(description, "");
			result.parameterNames = new ArrayList<String>(parameterNames);

			return result;
		}

		public static class CreationException extends RuntimeException implements Message {

			@Nonnull
			private final CalculatorParseException message;

			public CreationException(@Nonnull CalculatorParseException cause) {
				super(cause);
				message = cause;
			}

			@Nonnull
			@Override
			public String getMessageCode() {
				return message.getMessageCode();
			}

			@Nonnull
			@Override
			public List<Object> getParameters() {
				return message.getParameters();
			}

			@Nonnull
			@Override
			public MessageLevel getMessageLevel() {
				return message.getMessageLevel();
			}

			@Override
			@Nonnull
			public String getLocalizedMessage() {
				return message.getLocalizedMessage();
			}

			@Nonnull
			@Override
			public String getLocalizedMessage(@Nonnull Locale locale) {
				return message.getLocalizedMessage(locale);
			}
		}
	}
}
