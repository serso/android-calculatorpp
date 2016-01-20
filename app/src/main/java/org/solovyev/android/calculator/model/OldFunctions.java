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

import android.text.TextUtils;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.solovyev.android.calculator.PersistedEntitiesContainer;
import org.solovyev.android.calculator.function.CppFunction;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

@Root(name = "Functions")
public class OldFunctions implements PersistedEntitiesContainer<OldFunction> {

    public static final String PREFS_KEY = "org.solovyev.android.calculator.CalculatorModel_functions";

    @ElementList(type = OldFunction.class)
    private List<OldFunction> functions = new ArrayList<>();

    public OldFunctions() {
    }

    @Nonnull
    public static List<CppFunction> toCppFunctions(@Nonnull OldFunctions oldFunctions) {
        final List<CppFunction> functions = new ArrayList<>();
        for (OldFunction oldFunction : oldFunctions.getEntities()) {
            final String name = oldFunction.getName();
            final String body = oldFunction.getContent();
            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(body)) {
                continue;
            }
            functions.add(CppFunction.builder(name, body)
                    .withParameters(oldFunction.getParameterNames())
                    .withDescription(oldFunction.getDescription()).build());
        }
        return functions;
    }

    public List<OldFunction> getEntities() {
        return functions;
    }
}
