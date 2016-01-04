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

package org.solovyev.android.calculator.drag;

import java.util.EventListener;

import javax.annotation.Nonnull;


public interface DragListener extends EventListener {

    /**
     * @return 'true': if drag event has taken place (i.e. onDrag() method returned true) then click action will be suppresed
     */
    boolean isSuppressOnClickEvent();

    /**
     * @param dragButton drag button object for which onDrag listener was set
     * @param event      drag event
     * @return 'true' if drag event occurred, 'false' otherwise
     */
    boolean onDrag(@Nonnull DragButton dragButton, @Nonnull DragEvent event);

}
