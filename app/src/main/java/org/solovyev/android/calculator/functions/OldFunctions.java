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

package org.solovyev.android.calculator.functions;

import android.text.TextUtils;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

@Root(name = "Functions")
public class OldFunctions {

    public static final String PREFS_KEY = "org.solovyev.android.calculator.CalculatorModel_functions";

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    @ElementList(type = OldFunction.class, name = "functions")
    public List<OldFunction> list = new ArrayList<>();

    public OldFunctions() {
    }

    @Nonnull
    public static List<CppFunction> toCppFunctions(@Nonnull OldFunctions oldFunctions) {
        final List<CppFunction> functions = new ArrayList<>();
        for (OldFunction oldFunction : oldFunctions.list) {
            final String name = oldFunction.name;
            final String body = oldFunction.content;
            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(body)) {
                continue;
            }
            functions.add(CppFunction.builder(name, body)
                    .withParameters(oldFunction.parameterNames)
                    .withDescription(oldFunction.description).build());
        }
        return functions;
    }
}
