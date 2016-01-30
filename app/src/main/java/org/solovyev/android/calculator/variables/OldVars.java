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

package org.solovyev.android.calculator.variables;

import android.text.TextUtils;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Strings.nullToEmpty;

@Root(name = "vars")
public class OldVars {

    public static final String PREFS_KEY = "org.solovyev.android.calculator.CalculatorModel_vars";

    @ElementList(type = OldVar.class, name = "vars")
    public List<OldVar> list = new ArrayList<OldVar>();

    public OldVars() {
    }

    @Nonnull
    public static List<CppVariable> toCppVariables(@Nonnull OldVars oldVariables) {
        final List<CppVariable> variables = new ArrayList<>();
        for (OldVar oldVar : oldVariables.list) {
            final String name = oldVar.name;
            if (TextUtils.isEmpty(name)) {
                continue;
            }
            variables.add(CppVariable.builder(name)
                    .withValue(nullToEmpty(oldVar.value))
                    .withDescription(nullToEmpty(oldVar.description)).build());
        }
        return variables;
    }
}
