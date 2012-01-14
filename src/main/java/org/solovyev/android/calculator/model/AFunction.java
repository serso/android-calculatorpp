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
import org.solovyev.common.text.StringMapper;
import org.solovyev.common.utils.CollectionsUtils;

import java.util.List;

/**
 * User: serso
 * Date: 12/22/11
 * Time: 5:25 PM
 */

@Root
public class AFunction implements MathPersistenceEntity {

	@Element
	@NotNull
	private String name;

	@Element
	@NotNull
	private String content;


	@Element(required = false)
	@Nullable
	private String parameterNames;


	@NotNull
	public String getName() {
		return name;
	}

	public void setName(@NotNull String name) {
		this.name = name;
	}

	@NotNull
	public String getContent() {
		return content;
	}

	public void setContent(@NotNull String content) {
		this.content = content;
	}

	@Nullable
	public String getParameterNames() {
		return parameterNames;
	}

	public void setParameterNames(@Nullable String[] parameterNames) {
		this.parameterNames = CollectionsUtils.formatValue(CollectionsUtils.asList(parameterNames), ";", new StringMapper());
	}

	public void setParameterNames(@Nullable String parameterNames) {
		this.parameterNames = parameterNames;
	}

	@NotNull
	public String[] getParameterNamesAsArray() {
		final List<String> parameterNamesAsList = CollectionsUtils.split(parameterNames, ";");
		return parameterNamesAsList.toArray(new String[parameterNamesAsList.size()]);
	}
}
