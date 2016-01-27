/*
 * Copyright 2013 serso aka se.solovyev
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Contact details
 *
 * Email: se.solovyev@gmail.com
 * Site:  http://se.solovyev.org
 */

package org.solovyev.android.calculator.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Transient;
import org.solovyev.android.calculator.PersistedEntity;
import org.solovyev.common.math.MathEntity;
import org.solovyev.common.text.Strings;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import jscl.math.function.IFunction;

@Root(name = "function")
public class OldFunction implements IFunction, PersistedEntity, Serializable {

    @Transient
    private Integer id;

    @Element
    private String name;

    @Element(name = "body")
    private String content;

    @ElementList(type = String.class)
    @Nonnull
    private List<String> parameterNames = new ArrayList<String>();

    @Element
    private boolean system;

    @Element(required = false)
    @Nonnull
    private String description = "";

	public OldFunction() {
    }

    public static OldFunction fromIFunction(@Nonnull IFunction function) {
        final OldFunction result = new OldFunction();

        copy(result, function);

        return result;
    }

	private static void copy(@Nonnull OldFunction target,
                             @Nonnull IFunction source) {
        target.name = source.getName();
        target.content = source.getContent();
        target.description = Strings.getNotEmpty(source.getDescription(), "");
        target.system = source.isSystem();
        if (source.isIdDefined()) {
            target.id = source.getId();
        }
        target.parameterNames = new ArrayList<>(source.getParameterNames());
    }

    @Override
    public void copy(@Nonnull MathEntity mathEntity) {
        if (mathEntity instanceof IFunction) {
            copy(this, (IFunction) mathEntity);
        } else {
            throw new IllegalArgumentException("Trying to make a copy of unsupported type: " + mathEntity.getClass());
        }
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

	@Nonnull
    public String getName() {
        return name;
    }

    public void setName(@Nonnull String name) {
        this.name = name;
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
    public void setId(@Nonnull Integer id) {
        this.id = id;
    }

    @Override
    public boolean isIdDefined() {
        return this.id != null;
    }

    @Nonnull
    public String getContent() {
        return content;
    }

    public void setContent(@Nonnull String content) {
        this.content = content;
    }

    @Nonnull
    @Override
    public String getDescription() {
        return this.description;
    }

    @Nonnull
    public List<String> getParameterNames() {
        return parameterNames;
    }
}
