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

import org.solovyev.common.msg.Message;
import org.solovyev.common.msg.MessageType;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * User: serso
 * Date: 9/22/12
 * Time: 2:00 PM
 */
public class DummyCalculatorNotifier implements CalculatorNotifier {

    @Override
    public void showMessage(@Nonnull Message message) {
    }

    @Override
    public void showMessage(@Nonnull Integer messageCode, @Nonnull MessageType messageType, @Nonnull List<Object> parameters) {
    }

    @Override
    public void showMessage(@Nonnull Integer messageCode, @Nonnull MessageType messageType, @Nullable Object... parameters) {
    }

    @Override
    public void showDebugMessage(@Nullable String tag, @Nonnull String message) {
    }
}
