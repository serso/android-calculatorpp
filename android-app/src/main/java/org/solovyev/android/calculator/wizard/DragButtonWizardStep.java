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

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import org.solovyev.android.calculator.R;
import org.solovyev.android.calculator.drag.DirectionDragButton;
import org.solovyev.android.calculator.drag.DragButton;
import org.solovyev.android.calculator.drag.DragDirection;
import org.solovyev.android.calculator.drag.SimpleDragListener;
import org.solovyev.common.math.Point2d;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;

public class DragButtonWizardStep extends WizardFragment {

	/*
	**********************************************************************
	*
	*                           CONSTANTS
	*
	**********************************************************************
	*/

	private static final String ACTION = "action";

	/*
	**********************************************************************
	*
	*                           FIELDS
	*
	**********************************************************************
	*/

	@Nullable
	private DirectionDragButton dragButton;

	@Nullable
	private TextView actionTextView;

	@Nonnull
	private TextView descriptionTextView;

	private DragButtonAction action = DragButtonAction.center;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected int getViewResId() {
		return R.layout.cpp_wizard_step_drag_button;
	}

	@Override
	public void onViewCreated(View root, Bundle savedInstanceState) {
		super.onViewCreated(root, savedInstanceState);

		dragButton = (DirectionDragButton) root.findViewById(R.id.wizard_dragbutton);
		dragButton.setOnClickListener(new DragButtonOnClickListener());
		dragButton.setOnDragListener(new SimpleDragListener(new DragButtonProcessor(), getActivity()));
		actionTextView = (TextView) root.findViewById(R.id.wizard_dragbutton_action_textview);
		descriptionTextView = (TextView) root.findViewById(R.id.wizard_dragbutton_description_textview);

		if (savedInstanceState != null) {
			setAction((DragButtonAction) savedInstanceState.getSerializable(ACTION));
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		outState.putSerializable(ACTION, action);
	}

	/*
	**********************************************************************
	*
	*                           STATIC/INNER
	*
	**********************************************************************
	*/

	private static enum DragButtonAction {
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

	private class DragButtonOnClickListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			if (action == DragButtonAction.center || action == DragButtonAction.end) {
				setNextAction();
			}
		}
	}

	private void setNextAction() {
		setAction(action.getNextAction());
	}

	private class DragButtonProcessor implements SimpleDragListener.DragProcessor {
		@Override
		public boolean processDragEvent(@Nonnull DragDirection dragDirection,
										@Nonnull DragButton dragButton,
										@Nonnull Point2d startPoint2d,
										@Nonnull MotionEvent motionEvent) {
			if (action.dragDirection == dragDirection) {
				setNextAction();
				return true;
			}
			return false;
		}
	}

	private void setAction(DragButtonAction action) {
		if (this.action != action) {
			this.action = action;
			if (actionTextView != null) {
				actionTextView.setText(this.action.actionTextResId);
			}

			boolean firstChange = false;
			if (action != DragButtonAction.center) {
				firstChange = true;
			}
			if (firstChange) {
				//descriptionTextView.setVisibility(GONE);
			}
		}
	}
}
