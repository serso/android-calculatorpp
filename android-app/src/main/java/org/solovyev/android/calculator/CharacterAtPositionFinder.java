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

package org.solovyev.android.calculator;

import org.solovyev.common.JPredicate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * User: serso
 * Date: 10/3/11
 * Time: 12:54 AM
 */
public class CharacterAtPositionFinder implements JPredicate<Character> {

    @Nonnull
    private final String targetString;
    private int i;

    public CharacterAtPositionFinder(@Nonnull String targetString, int i) {
        this.targetString = targetString;
        this.i = i;
    }

    @Override
    public boolean apply(@Nullable Character s) {
        return s != null && s.equals(targetString.charAt(i));
    }

    public void setI(int i) {
        this.i = i;
    }
}
