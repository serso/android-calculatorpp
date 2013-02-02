/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.calculator.model;

import jscl.math.function.IFunction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Transient;
import org.solovyev.android.calculator.CalculatorParseException;
import org.solovyev.android.calculator.Locator;
import org.solovyev.android.calculator.MathPersistenceEntity;
import org.solovyev.common.math.MathEntity;
import org.solovyev.common.msg.Message;
import org.solovyev.common.msg.MessageType;
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
	@NotNull
	private String name;

	@Element(name = "body")
	@NotNull
	private String content;

	@ElementList(type = String.class)
	@NotNull
	private List<String> parameterNames = new ArrayList<String>();

	@Element
	private boolean system;

	@Element(required = false)
	@NotNull
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

	public static AFunction fromIFunction(@NotNull IFunction function) {
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
	public void copy(@NotNull MathEntity mathEntity) {
		if (mathEntity instanceof IFunction) {
			copy(this, (IFunction) mathEntity);
		} else {
			throw new IllegalArgumentException("Trying to make a copy of unsupported type: " + mathEntity.getClass());
		}
	}

	private static void copy(@NotNull AFunction target,
							 @NotNull IFunction source) {
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

	@NotNull
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
            if (function.isIdDefined()) {
                this.id = function.getId();
            }
            this.parameterNames = new ArrayList<String>(function.getParameterNames());
        }

        public Builder(@NotNull String name,
                       @NotNull String value,
                       @NotNull List<String> parameterNames) {
            this.name = name;
            this.value = value;
            this.parameterNames = parameterNames;
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
		public AFunction create()  throws AFunction.Builder.CreationException{
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

			@NotNull
			private final CalculatorParseException message;

			public CreationException(@NotNull CalculatorParseException cause) {
				super(cause);
				message = cause;
			}

			@NotNull
			@Override
			public String getMessageCode() {
				return message.getMessageCode();
			}

			@NotNull
			@Override
			public List<Object> getParameters() {
				return message.getParameters();
			}

			@NotNull
			@Override
			public MessageType getMessageType() {
				return message.getMessageType();
			}

			@Override
			@NotNull
			public String getLocalizedMessage() {
				return message.getLocalizedMessage();
			}

			@NotNull
			@Override
			public String getLocalizedMessage(@NotNull Locale locale) {
				return message.getLocalizedMessage(locale);
			}
		}
	}
}
