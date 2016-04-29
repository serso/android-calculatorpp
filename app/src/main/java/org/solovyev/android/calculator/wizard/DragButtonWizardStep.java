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

package org.solovyev.android.calculator.wizard;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import org.solovyev.android.calculator.BaseActivity;
import org.solovyev.android.calculator.R;
import org.solovyev.android.calculator.keyboard.BaseKeyboardUi;
import org.solovyev.android.views.Adjuster;
import org.solovyev.android.views.dragbutton.DirectionDragButton;
import org.solovyev.android.views.dragbutton.DirectionDragListener;
import org.solovyev.android.views.dragbutton.DragDirection;
import org.solovyev.android.views.dragbutton.DragEvent;

import java.util.Arrays;

import javax.annotation.Nullable;
import javax.inject.Inject;

import static org.solovyev.android.calculator.App.cast;

public class DragButtonWizardStep extends WizardFragment {

    private static final String ACTION = "action";

    @Nullable
    private TextView actionTextView;

    private DragButtonAction action = DragButtonAction.center;

    @Inject
    Typeface typeface;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cast(this).getComponent().inject(this);
    }

    @Override
    protected int getViewResId() {
        return R.layout.cpp_wizard_step_drag_button;
    }

    @Override
    public void onViewCreated(View root, Bundle savedInstanceState) {
        super.onViewCreated(root, savedInstanceState);

        final DirectionDragButton dragButton =(DirectionDragButton) root.findViewById(R.id.wizard_dragbutton);
        dragButton.setOnClickListener(this);
        dragButton.setOnDragListener(new DirectionDragListener(getActivity()) {
            @Override
            protected boolean onDrag(@NonNull View view, @NonNull DragEvent event, @NonNull DragDirection direction) {
                if (action.dragDirection == direction) {
                    setNextAction();
                    return true;
                }
                return false;
            }
        });
        Adjuster.adjustText(dragButton, BaseKeyboardUi.getTextScale(getActivity()));
        actionTextView = (TextView) root.findViewById(R.id.wizard_dragbutton_action_textview);
        if (savedInstanceState != null) {
            setAction((DragButtonAction) savedInstanceState.getSerializable(ACTION));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable(ACTION, action);
    }

    private void setNextAction() {
        setAction(action.getNextAction());
    }

    private void setAction(DragButtonAction action) {
        if (this.action != action) {
            this.action = action;
            if (actionTextView != null) {
                actionTextView.setText(this.action.actionTextResId);
            }
        }
    }

    private enum DragButtonAction {
        center(R.string.cpp_wizard_dragbutton_action_center, null),
        up(R.string.cpp_wizard_dragbutton_action_up, DragDirection.up),
        down(R.string.cpp_wizard_dragbutton_action_down, DragDirection.down),
        end(R.string.cpp_wizard_dragbutton_action_end, null);

        private final int actionTextResId;

        @Nullable
        private final DragDirection dragDirection;

        DragButtonAction(int actionTextResId, @Nullable DragDirection dragDirection) {
            this.actionTextResId = actionTextResId;
            this.dragDirection = dragDirection;
        }

        @Nullable
        DragButtonAction getNextAction() {
            final DragButtonAction[] values = values();
            final int position = Arrays.binarySearch(values, this);
            if (position < values.length - 1) {
                return values[position + 1];
            } else {
                return values[0];
            }
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.wizard_dragbutton) {
            if (action == DragButtonAction.center || action == DragButtonAction.end) {
                setNextAction();
            }
            return;
        }
        super.onClick(v);
    }
}
