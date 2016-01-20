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

import java.util.ArrayList;
import java.util.List;

/**
 * User: serso
 * Date: 9/29/11
 * Time: 5:19 PM
 */

@Root
public class Vars implements PersistedEntitiesContainer<Var> {

    @ElementList(type = Var.class)
    private List<Var> vars = new ArrayList<Var>();

    public Vars() {
    }

    public List<Var> getEntities() {
        return vars;
    }
}
