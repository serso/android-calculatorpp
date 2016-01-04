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

package org.solovyev.android.calculator.function;

import android.content.Context;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.AbsSavedState;
import android.widget.EditText;

public class FunctionParamEditText extends EditText {

    public FunctionParamEditText(Context context) {
        super(context);
    }

    public FunctionParamEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FunctionParamEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    // we restore state manually outside
    @Override
    public Parcelable onSaveInstanceState() {
        super.onSaveInstanceState();
        return AbsSavedState.EMPTY_STATE;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(null);
    }
}
