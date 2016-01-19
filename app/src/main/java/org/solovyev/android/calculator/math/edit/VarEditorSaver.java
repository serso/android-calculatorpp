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

package org.solovyev.android.calculator.math.edit;

import android.view.View;
import android.widget.EditText;
import jscl.text.Identifier;
import jscl.text.MutableInt;
import jscl.text.ParseException;
import jscl.text.Parser;
import org.solovyev.android.calculator.VarsRegistry;
import org.solovyev.android.calculator.EntitiesRegistry;
import org.solovyev.android.calculator.Locator;
import org.solovyev.android.calculator.R;
import org.solovyev.android.calculator.math.MathType;
import org.solovyev.android.calculator.model.MathEntityBuilder;
import org.solovyev.common.math.MathEntity;
import org.solovyev.common.msg.MessageType;
import org.solovyev.common.text.Strings;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class VarEditorSaver<T extends MathEntity> implements View.OnClickListener {

    @Nonnull
    private final MathEntityBuilder<? extends T> varBuilder;

    @Nullable
    private final T editedInstance;

    @Nonnull
    private final EntitiesRegistry<T> mathRegistry;

    @Nonnull
    private final Object source;

    @Nonnull
    private View editView;

    public VarEditorSaver(@Nonnull MathEntityBuilder<? extends T> varBuilder,
                          @Nullable T editedInstance,
                          @Nonnull View editView,
                          @Nonnull EntitiesRegistry<T> mathRegistry,
                          @Nonnull Object source) {
        this.varBuilder = varBuilder;
        this.editedInstance = editedInstance;
        this.editView = editView;
        this.mathRegistry = mathRegistry;
        this.source = source;
    }

    public static boolean isValidName(@Nullable String name) {
        boolean result = false;

        if (!Strings.isEmpty(name)) {
            try {
                if (name == null) throw new AssertionError();
                Identifier.parser.parse(Parser.Parameters.newInstance(name, new MutableInt(0), Locator.getInstance().getEngine().getMathEngine()), null);
                result = true;
            } catch (ParseException e) {
                // not valid name;
            }
        }

        return result;
    }

    @Override
    public void onClick(View v) {
        final Integer error;

        final EditText editName = (EditText) editView.findViewById(R.id.var_edit_name);
        String name = editName.getText().toString();

        final EditText editValue = (EditText) editView.findViewById(R.id.var_edit_value);
        String value = editValue.getText().toString();

        final EditText editDescription = (EditText) editView.findViewById(R.id.var_edit_description);
        String description = editDescription.getText().toString();

        if (isValidName(name)) {

            boolean canBeSaved = false;

            final T entityFromRegistry = mathRegistry.get(name);
            if (entityFromRegistry == null) {
                canBeSaved = true;
            } else if (editedInstance != null && entityFromRegistry.getId().equals(editedInstance.getId())) {
                canBeSaved = true;
            }

            if (canBeSaved) {
                final MathType.Result mathType = MathType.getType(name, 0, false);

                if (mathType.type == MathType.text || mathType.type == MathType.constant) {

                    if (Strings.isEmpty(value)) {
                        // value is empty => undefined variable
                        varBuilder.setName(name);
                        varBuilder.setDescription(description);
                        varBuilder.setValue(null);
                        error = null;
                    } else {
                        // value is not empty => must be a number
                        boolean valid = VarsFragment.isValidValue(value);

                        if (valid) {
                            varBuilder.setName(name);
                            varBuilder.setDescription(description);
                            varBuilder.setValue(value);
                            error = null;
                        } else {
                            error = R.string.c_value_is_not_a_number;
                        }
                    }
                } else {
                    error = R.string.c_var_name_clashes;
                }
            } else {
                error = R.string.c_var_already_exists;
            }
        } else {
            error = R.string.c_name_is_not_valid;
        }

        if (error != null) {
            Locator.getInstance().getNotifier().showMessage(error, MessageType.error);
        } else {
            VarsRegistry.saveVariable(mathRegistry, varBuilder, editedInstance, source, true);
        }
    }
}
