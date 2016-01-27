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

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.solovyev.android.calculator.PersistedEntitiesContainer;
import org.solovyev.android.calculator.variables.CppVariable;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

@Root(name = "vars")
public class OldVars implements PersistedEntitiesContainer<OldVar> {

    public static final String PREFS_KEY = "org.solovyev.android.calculator.CalculatorModel_vars";
    @ElementList(type = OldVar.class)
    private List<OldVar> vars = new ArrayList<OldVar>();

    public OldVars() {
    }

    public List<OldVar> getEntities() {
        return vars;
    }

    @Nonnull
    public static List<CppVariable> toCppVariables(@Nonnull OldVars oldVariables) {
        final List<CppVariable> variables = new ArrayList<>();
        /*for (OldVar oldVar : oldVariables.vars) {
            final String name = oldVar.getName();
            final String body = oldVar.getContent();
            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(body)) {
                continue;
            }
            variables.add(CppFunction.builder(name, body)
                    .withParameters(oldVar.getParameterNames())
                    .withDescription(oldVar.getDescription()).build());
        }*/
        return variables;
    }
}
