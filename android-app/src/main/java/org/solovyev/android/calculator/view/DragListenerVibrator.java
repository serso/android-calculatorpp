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

package org.solovyev.android.calculator.view;

import android.content.SharedPreferences;
import android.os.Vibrator;

import org.solovyev.android.calculator.drag.DragButton;
import org.solovyev.android.calculator.drag.DragEvent;
import org.solovyev.android.calculator.drag.DragListener;
import org.solovyev.android.calculator.drag.DragListenerWrapper;
import org.solovyev.android.view.VibratorContainer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * User: serso
 * Date: 4/20/12
 * Time: 3:27 PM
 */
public class DragListenerVibrator extends DragListenerWrapper {

    private static final float VIBRATION_TIME_SCALE = 0.5f;

    @Nonnull
    private final VibratorContainer vibrator;

    public DragListenerVibrator(@Nonnull DragListener dragListener,
                                @Nullable Vibrator vibrator,
                                @Nonnull SharedPreferences preferences) {
        super(dragListener);
        this.vibrator = new VibratorContainer(vibrator, preferences, VIBRATION_TIME_SCALE);
    }

    @Override
    public boolean onDrag(@Nonnull DragButton dragButton, @Nonnull DragEvent event) {
        boolean consumed = super.onDrag(dragButton, event);

        if (consumed) {
            vibrator.vibrate();
        }

        return consumed;
    }
}
